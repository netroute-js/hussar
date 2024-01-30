package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringHelper {

    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

}
