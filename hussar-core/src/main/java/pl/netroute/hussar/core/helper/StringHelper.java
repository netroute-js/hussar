package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringHelper {
    private static final String DEFAULT_DELIMITER = ",";

    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    public static String concat(@NonNull String... values) {
        return Stream
                .of(values)
                .collect(Collectors.joining(DEFAULT_DELIMITER));
    }

}
