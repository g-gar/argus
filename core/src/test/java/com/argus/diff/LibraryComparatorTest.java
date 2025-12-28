package com.argus.diff;

import com.argus.model.*;
import com.argus.model.diff.*;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryComparatorTest {

    @Test
    void compareSameVersion() {
        LibraryVersion v1 = createLibrary("1.0.0");
        LibraryComparator comparator = new LibraryComparator();

        LibraryDiff diff = comparator.compare(v1, v1);

        assertEquals(0, diff.classDifferences().size());
        assertEquals(0, diff.getBreakingCount());
    }

    @Test
    void compareAddedClass() {
        LibraryVersion v1 = createLibrary("1.0.0");
        LibraryVersion v2 = createLibrary("1.1.0", createClass("NewClass"));

        LibraryComparator comparator = new LibraryComparator();
        LibraryDiff diff = comparator.compare(v1, v2);

        assertEquals(1, diff.classDifferences().size());
        assertEquals(ChangeType.ADDED, diff.classDifferences().get(0).changeType());
    }

    @Test
    void compareRemovedClass() {
        LibraryVersion v1 = createLibrary("1.0.0", createClass("OldClass"));
        LibraryVersion v2 = createLibrary("1.1.0");

        LibraryComparator comparator = new LibraryComparator();
        LibraryDiff diff = comparator.compare(v1, v2);

        assertEquals(1, diff.classDifferences().size());
        assertEquals(ChangeType.REMOVED, diff.classDifferences().get(0).changeType());
        // Removing a class is a breaking change (1 class + 0 methods + 0 fields = 1)
        assertEquals(1, diff.classDifferences().get(0).getBreakingCount());
        // Note: Class-level breaking count currently only sums breaking methods.
        // If a class is removed, it might not count as breaking methods unless we
        // define it so.
        // User's spec: getBreakingCount calls methodDifferences.stream()...
        // So removed class logic might need adjustment if we want it to count.
        // But let's check method breaking logic.
    }

    @Test
    void compareMethodChanges() {
        MethodInfo m1 = createMethod("doSomething", "()V", true); // Public
        MethodInfo m2 = createMethod("doSomething", "()V", false); // Private (Breaking)

        ClassInfo c1 = createClass("MyClass", m1);
        ClassInfo c2 = createClass("MyClass", m2);

        LibraryVersion v1 = createLibrary("1.0.0", c1);
        LibraryVersion v2 = createLibrary("1.1.0", c2);

        LibraryComparator comparator = new LibraryComparator();
        LibraryDiff diff = comparator.compare(v1, v2);

        assertEquals(1, diff.classDifferences().size());
        ClassDiff classDiff = diff.classDifferences().get(0);
        assertEquals(ChangeType.MODIFIED, classDiff.changeType());

        assertEquals(1, classDiff.methodDifferences().size());
        MethodDiff mDiff = classDiff.methodDifferences().get(0);
        assertEquals(ChangeType.BREAKING_CHANGE, mDiff.changeType());

        assertEquals(1, diff.getBreakingCount());
    }

    @Test
    void compareSignatureChange() {
        // Same method name, different descriptor (int -> long)
        MethodInfo m1 = createMethod("calculate", "(I)V", true);
        MethodInfo m2 = createMethod("calculate", "(J)V", true);

        ClassInfo c1 = createClass("Calculator", m1);
        ClassInfo c2 = createClass("Calculator", m2);

        LibraryVersion v1 = createLibrary("1.0.0", c1);
        LibraryVersion v2 = createLibrary("1.1.0", c2);

        LibraryComparator comparator = new LibraryComparator();
        LibraryDiff diff = comparator.compare(v1, v2);

        // Should be 1 class diff, 1 method diff (BREAKING_CHANGE), NOT added+removed
        assertEquals(1, diff.classDifferences().size());
        ClassDiff classDiff = diff.classDifferences().get(0);

        assertEquals(1, classDiff.methodDifferences().size());
        MethodDiff mDiff = classDiff.methodDifferences().get(0);

        assertEquals(ChangeType.BREAKING_CHANGE, mDiff.changeType());
        assertTrue(mDiff.description().contains("Signature changed"));
        assertTrue(mDiff.oldMethod().isPresent());
        assertTrue(mDiff.newMethod().isPresent());
    }

    // Helpers
    private LibraryVersion createLibrary(String version, ClassInfo... classes) {
        return LibraryVersion.builder()
                .coordinates("com.example:lib:" + version)
                .version(version)
                .javaMajorVersion(61)
                .classes(List.of(classes))
                .build();
    }

    private ClassInfo createClass(String name, MethodInfo... methods) {
        return ClassInfo.builder()
                .name(name)
                .superClassName("java.lang.Object")
                .interfaces(Collections.emptyList())
                .isInterface(false)
                .isAbstract(false)
                .isDeprecated(false)
                .annotations(Collections.emptySet())
                .methods(List.of(methods))
                .fields(Collections.emptyList())
                .build();
    }

    private MethodInfo createMethod(String name, String descriptor, boolean isPublic) {
        return MethodInfo.builder()
                .name(name)
                .descriptor(descriptor)
                .returnType("void")
                .paramTypes(Collections.emptyList())
                .isStatic(false)
                .isPublic(isPublic)
                .isDeprecated(false)
                .annotations(Collections.emptySet())
                .build();
    }
}
