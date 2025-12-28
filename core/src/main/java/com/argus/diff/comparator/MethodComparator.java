package com.argus.diff.comparator;

import com.argus.model.diff.ChangeType;
import com.argus.model.MethodInfo;
import com.argus.model.diff.MethodDiff;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodComparator {

    public List<MethodDiff> compare(List<MethodInfo> list1, List<MethodInfo> list2) {
        List<MethodDiff> results = new ArrayList<>();

        // Maps for exact lookup and consumption
        // Use mutable maps to remove matched items
        Map<String, MethodInfo> v1Remaining = new HashMap<>(toMap(list1, MethodInfo::getUniqueSignature));
        Map<String, MethodInfo> v2Remaining = new HashMap<>(toMap(list2, MethodInfo::getUniqueSignature));

        // 1. Exact Match (Signature)
        Set<String> commonSignatures = new HashSet<>(v1Remaining.keySet());
        commonSignatures.retainAll(v2Remaining.keySet());

        for (String sig : commonSignatures) {
            MethodInfo m1 = v1Remaining.remove(sig);
            MethodInfo m2 = v2Remaining.remove(sig);

            boolean isBreaking = false;
            List<String> changes = new ArrayList<>();

            if (m1.isDeprecated() != m2.isDeprecated()) {
                changes.add("Deprecation changed: " + m1.isDeprecated() + " -> " + m2.isDeprecated());
            }

            if (m1.isPublic() && !m2.isPublic()) {
                isBreaking = true;
                changes.add("Visibility reduced from public");
            }

            if (m1.isStatic() != m2.isStatic()) {
                isBreaking = true;
                changes.add("Static modifier changed: " + m1.isStatic() + " -> " + m2.isStatic());
            }

            if (!changes.isEmpty()) {
                results.add(MethodDiff.builder()
                        .uniqueSignature(sig)
                        .changeType(isBreaking ? ChangeType.BREAKING_CHANGE : ChangeType.MODIFIED)
                        .oldMethod(Optional.of(m1))
                        .newMethod(Optional.of(m2))
                        .description(String.join(", ", changes))
                        .build());
            }
        }

        // 2. Fuzzy Match (Same Name, Changed Signature) -> Breaking Change
        Iterator<Map.Entry<String, MethodInfo>> itV1 = v1Remaining.entrySet().iterator();
        while (itV1.hasNext()) {
            Map.Entry<String, MethodInfo> entry1 = itV1.next();
            MethodInfo m1 = entry1.getValue();

            // Find first V2 method with same name (ignoring descriptor)
            Optional<MethodInfo> match = v2Remaining.values().stream()
                    .filter(m2 -> m2.name().equals(m1.name()))
                    .findFirst();

            if (match.isPresent()) {
                MethodInfo m2 = match.get();

                results.add(MethodDiff.builder()
                        .uniqueSignature(m2.getUniqueSignature()) // Use new signature
                        .changeType(ChangeType.BREAKING_CHANGE)
                        .oldMethod(Optional.of(m1))
                        .newMethod(Optional.of(m2))
                        .description("Signature changed: " + m1.descriptor() + " -> " + m2.descriptor())
                        .build());

                // Consume both
                itV1.remove();
                v2Remaining.remove(m2.getUniqueSignature());
            }
        }

        // 3. Residuals (Truly Added or Removed)
        for (MethodInfo m1 : v1Remaining.values()) {
            results.add(MethodDiff.builder()
                    .uniqueSignature(m1.getUniqueSignature())
                    .changeType(ChangeType.REMOVED)
                    .oldMethod(Optional.of(m1))
                    .newMethod(Optional.empty())
                    .description("Method removed")
                    .build());
        }

        for (MethodInfo m2 : v2Remaining.values()) {
            results.add(MethodDiff.builder()
                    .uniqueSignature(m2.getUniqueSignature())
                    .changeType(ChangeType.ADDED)
                    .oldMethod(Optional.empty())
                    .newMethod(Optional.of(m2))
                    .description("New method added")
                    .build());
        }

        return results;
    }

    private <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyMapper) {
        if (list == null)
            return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyMapper, Function.identity(), (a, b) -> a));
    }
}
