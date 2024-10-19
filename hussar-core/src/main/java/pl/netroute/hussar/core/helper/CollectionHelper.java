package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection helper.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionHelper {

    /**
     * It checks whether given {@link Collection} is null or empty.
     *
     * @param <T> - the type of elements in {@link Collection}.
     * @param collection - the {@link Collection} to be checked.
     * @return true if {@link Collection} is null or empty. False otherwise.
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Merges two given lists into one.
     *
     * @param <T> - the type of elements in {@link List}.
     * @param firstList - the first input {@link List}.
     * @param secondList - the second input {@link List}.
     * @return a combination of two lists.
     */
    public static <T> List<T> mergeLists(@NonNull List<T> firstList,
                                         @NonNull List<T> secondList) {
        return Stream
                .concat(firstList.stream(), secondList.stream())
                .toList();
    }

    /**
     * Merges two given sets into one.
     *
     * @param <T> - the type of elements in {@link Set}.
     * @param firstSet - the first input {@link Set}.
     * @param secondSet - the second input {@link Set}.
     * @return a combination of two sets.
     */
    public static <T> Set<T> mergeSets(@NonNull Set<T> firstSet,
                                       @NonNull Set<T> secondSet) {
        return Stream
                .concat(firstSet.stream(), secondSet.stream())
                .collect(Collectors.toUnmodifiableSet());
    }

}
