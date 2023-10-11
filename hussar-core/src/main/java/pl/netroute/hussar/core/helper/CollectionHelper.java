package pl.netroute.hussar.core.helper;

import java.util.ArrayList;
import java.util.List;

public class CollectionHelper {

    private CollectionHelper() {}

    public static <T> List<T> mergeLists(List<T> firstList, List<T> secondList) {
        var mergedList = new ArrayList<>(firstList);
        mergedList.addAll(secondList);

        return List.copyOf(mergedList);
    }

}
