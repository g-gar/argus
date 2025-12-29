package com.argus.service.diff;

import com.argus.service.diff.comparator.FieldComparator;
import com.argus.service.diff.comparator.MethodComparator;
import com.argus.model.*;
import com.argus.model.diff.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LibraryComparisonService {

    private final MethodComparator methodComparator = new MethodComparator();
    private final FieldComparator fieldComparator = new FieldComparator();

    public LibraryDiff compare(LibraryVersion v1, LibraryVersion v2) {
        // 1. Index classes by name
        Map<String, ClassInfo> v1Classes = toMap(v1.classes(), ClassInfo::name);
        Map<String, ClassInfo> v2Classes = toMap(v2.classes(), ClassInfo::name);

        // 2. Get all unique keys
        Set<String> allClasses = new HashSet<>();
        allClasses.addAll(v1Classes.keySet());
        allClasses.addAll(v2Classes.keySet());

        List<ClassDiff> classDiffs = new ArrayList<>();

        for (String className : allClasses) {
            ClassInfo c1 = v1Classes.get(className);
            ClassInfo c2 = v2Classes.get(className);

            if (c1 == null && c2 != null) {
                // ADDED
                classDiffs.add(ClassDiff.builder()
                        .className(className)
                        .changeType(ChangeType.ADDED)
                        .oldClass(Optional.empty())
                        .newClass(Optional.of(c2))
                        .methodDifferences(Collections.emptyList())
                        .fieldDifferences(Collections.emptyList())
                        .build());
            } else if (c1 != null && c2 == null) {
                // REMOVED
                classDiffs.add(ClassDiff.builder()
                        .className(className)
                        .changeType(ChangeType.REMOVED)
                        .oldClass(Optional.of(c1))
                        .newClass(Optional.empty())
                        .methodDifferences(Collections.emptyList())
                        .fieldDifferences(Collections.emptyList())
                        .build());
            } else {
                // MODIFIED, potentially
                List<MethodDiff> methodDiffs = methodComparator.compare(c1.methods(), c2.methods());
                List<FieldDiff> fieldDiffs = fieldComparator.compare(c1.fields(), c2.fields());

                if (!methodDiffs.isEmpty() || !fieldDiffs.isEmpty()) {
                    classDiffs.add(ClassDiff.builder()
                            .className(className)
                            .changeType(ChangeType.MODIFIED)
                            .oldClass(Optional.of(c1))
                            .newClass(Optional.of(c2))
                            .methodDifferences(methodDiffs)
                            .fieldDifferences(fieldDiffs)
                            .build());
                }
            }
        }

        return LibraryDiff.builder()
                .oldVersion(v1)
                .newVersion(v2)
                .classDifferences(classDiffs)
                .build();
    }

    private <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyMapper) {
        if (list == null)
            return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity(), (a, b) -> a));
    }
}
