package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionHelper {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> mergeLists(@NonNull List<T> firstList,
                                         @NonNull List<T> secondList) {
        return Stream
                .concat(firstList.stream(), secondList.stream())
                .toList();
    }

    public static <T> Set<T> getSetOrEmpty(Collection<T> collection) {
        return Optional
                .ofNullable(collection)
                .map(Set::copyOf)
                .orElse(Set.of());
    }

}
