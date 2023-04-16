package pl.netroute.hussar.core.helper;

public class StringHelper {

    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

}
