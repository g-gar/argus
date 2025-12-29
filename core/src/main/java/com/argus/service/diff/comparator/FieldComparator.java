package com.argus.service.diff.comparator;

import com.argus.model.diff.ChangeType;
import com.argus.model.FieldInfo;
import com.argus.model.diff.FieldDiff;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldComparator {

    public List<FieldDiff> compare(List<FieldInfo> list1, List<FieldInfo> list2) {
        Map<String, FieldInfo> f1Map = toMap(list1, FieldInfo::name);
        Map<String, FieldInfo> f2Map = toMap(list2, FieldInfo::name);

        Set<String> allFields = new HashSet<>();
        allFields.addAll(f1Map.keySet());
        allFields.addAll(f2Map.keySet());

        List<FieldDiff> diffs = new ArrayList<>();

        for (String name : allFields) {
            FieldInfo f1 = f1Map.get(name);
            FieldInfo f2 = f2Map.get(name);

            if (f1 == null) {
                diffs.add(FieldDiff.builder()
                        .name(name)
                        .changeType(ChangeType.ADDED)
                        .build());
            } else if (f2 == null) {
                diffs.add(FieldDiff.builder()
                        .name(name)
                        .changeType(ChangeType.REMOVED)
                        .build());
            } else {
                // Modification check
                if (!f1.type().equals(f2.type())) {
                    diffs.add(FieldDiff.builder()
                            .name(name)
                            .changeType(ChangeType.MODIFIED)
                            .build());
                }
            }
        }
        return diffs;
    }

    private <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyMapper) {
        if (list == null)
            return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity(), (a, b) -> a));
    }
}
