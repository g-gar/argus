package com.argus.bat.asm;

import com.argus.model.AnnotationInfo;
import com.argus.model.ClassInfo;
import com.argus.model.FieldInfo;
import com.argus.model.LibraryVersion;
import com.argus.model.MethodInfo;
import com.argus.port.ArtifactAnalyzer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class AsmArtifactAnalyzer implements ArtifactAnalyzer {

    @Override
    public boolean canHandle(InputStream input) throws IOException {
        if (!input.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark/reset");
        }

        input.mark(4);
        try {
            byte[] magic = new byte[4];
            int read = input.read(magic);
            if (read < 4) {
                return false;
            }
            // Check for ZIP/JAR magic number: PK\003\004 (0x50 0x4B 0x03 0x04)
            return magic[0] == 0x50 && magic[1] == 0x4B && magic[2] == 0x03 && magic[3] == 0x04;
        } finally {
            input.reset();
        }
    }

    @Override
    public int getJavaMajorVersion(InputStream input) throws IOException {
        if (input.markSupported()) {
            if (!canHandle(input)) {
                throw new IllegalArgumentException("Artifact format not supported");
            }
        }

        try (JarInputStream jis = new JarInputStream(input)) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    byte[] buffer = new byte[8];
                    int totalRead = 0;
                    while (totalRead < 8) {
                        int read = jis.read(buffer, totalRead, 8 - totalRead);
                        if (read == -1)
                            break;
                        totalRead += read;
                    }

                    if (totalRead >= 8) {
                        int magic = ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) |
                                ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);

                        if (magic == 0xCAFEBABE) {
                            return ((buffer[6] & 0xFF) << 8) | (buffer[7] & 0xFF);
                        }
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public LibraryVersion getArtifactMetadata(InputStream input) throws IOException {
        try (JarInputStream jis = new JarInputStream(input)) {
            Manifest manifest = jis.getManifest();

            String coordinates = null;
            String version = null;

            if (manifest != null) {
                Attributes attributes = manifest.getMainAttributes();
                version = attributes.getValue("Implementation-Version");
                String title = attributes.getValue("Implementation-Title");
                String group = attributes.getValue("Implementation-Vendor-Id");

                if (group != null && title != null) {
                    coordinates = group + ":" + title;
                } else if (title != null) {
                    coordinates = title;
                }

                if (coordinates != null && version != null) {
                    coordinates = coordinates + ":" + version;
                }
            }

            return LibraryVersion.builder()
                    .coordinates(coordinates != null ? coordinates : "unknown")
                    .version(version != null ? version : "unknown")
                    .javaMajorVersion(0)
                    .classes(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public List<ClassInfo> analyze(InputStream input) throws IOException {
        List<ClassInfo> classes = new ArrayList<>();

        try (JarInputStream jis = new JarInputStream(input)) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class") && !entry.getName().equals("module-info.class")) {
                    try {
                        ClassReader reader = new ClassReader(jis);
                        ClassInfoBuilderVisitor visitor = new ClassInfoBuilderVisitor();
                        reader.accept(visitor,
                                ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        classes.add(visitor.getClassInfo());
                    } catch (Exception e) {
                        // Log error
                    }
                }
            }
        }

        return classes;
    }

    private static class ClassInfoBuilderVisitor extends ClassVisitor {
        private String name;
        private String superName;
        private final List<String> interfaces = new ArrayList<>();
        private boolean isInterface;
        private boolean isAbstract;
        private boolean isDeprecated;
        private final Set<AnnotationInfo> annotations = new HashSet<>();
        private final List<MethodInfo> methods = new ArrayList<>();
        private final List<FieldInfo> fields = new ArrayList<>();

        public ClassInfoBuilderVisitor() {
            super(Opcodes.ASM9);
        }

        public ClassInfo getClassInfo() {
            return ClassInfo.builder()
                    .name(name.replace('/', '.'))
                    .superClassName(superName != null ? superName.replace('/', '.') : null)
                    .interfaces(interfaces)
                    .isInterface(isInterface)
                    .isAbstract(isAbstract)
                    .isDeprecated(isDeprecated)
                    .annotations(annotations)
                    .methods(methods)
                    .fields(fields)
                    .build();
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) {
            this.name = name;
            this.superName = superName;
            if (interfaces != null) {
                for (String iface : interfaces) {
                    this.interfaces.add(iface.replace('/', '.'));
                }
            }
            this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
            this.isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
            this.isDeprecated = (access & Opcodes.ACC_DEPRECATED) != 0;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new AnnotationBuilderVisitor(descriptor, values -> {
                annotations.add(AnnotationInfo.builder()
                        .name(Type.getType(descriptor).getClassName())
                        .values(values)
                        .build());
            });
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
            boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;
            fields.add(FieldInfo.builder()
                    .name(name)
                    .type(Type.getType(descriptor).getClassName())
                    .isStatic(isStatic)
                    .isFinal(isFinal)
                    .build());
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                String[] exceptions) {
            return new MethodBuilderVisitor(access, name, descriptor, methods::add);
        }
    }

    private static class AnnotationBuilderVisitor extends AnnotationVisitor {
        private final String descriptor;
        private final Consumer<Map<String, Object>> onComplete;
        private final Map<String, Object> values = new HashMap<>();

        public AnnotationBuilderVisitor(String descriptor, Consumer<Map<String, Object>> onComplete) {
            super(Opcodes.ASM9);
            this.descriptor = descriptor;
            this.onComplete = onComplete;
        }

        @Override
        public void visit(String name, Object value) {
            values.put(name, value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            values.put(name, value);
        }

        @Override
        public void visitEnd() {
            onComplete.accept(values);
        }
    }

    private static class MethodBuilderVisitor extends MethodVisitor {
        private final int access;
        private final String name;
        private final String descriptor;
        private final Consumer<MethodInfo> onComplete;
        private final Set<AnnotationInfo> annotations = new HashSet<>();

        public MethodBuilderVisitor(int access, String name, String descriptor, Consumer<MethodInfo> onComplete) {
            super(Opcodes.ASM9);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.onComplete = onComplete;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new AnnotationBuilderVisitor(descriptor, values -> {
                annotations.add(AnnotationInfo.builder()
                        .name(Type.getType(descriptor).getClassName())
                        .values(values)
                        .build());
            });
        }

        @Override
        public void visitEnd() {
            Type methodType = Type.getMethodType(descriptor);
            List<String> paramTypes = new ArrayList<>();
            for (Type arg : methodType.getArgumentTypes()) {
                paramTypes.add(arg.getClassName());
            }

            MethodInfo info = MethodInfo.builder()
                    .name(name)
                    .descriptor(descriptor)
                    .returnType(methodType.getReturnType().getClassName())
                    .paramTypes(paramTypes)
                    .isStatic((access & Opcodes.ACC_STATIC) != 0)
                    .isPublic((access & Opcodes.ACC_PUBLIC) != 0)
                    .isDeprecated((access & Opcodes.ACC_DEPRECATED) != 0)
                    .annotations(annotations)
                    .build();

            onComplete.accept(info);
        }
    }
}
