package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A String helper class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringHelper {

    /**
     * Checks whether the value is not null and not blank.
     *
     * @param value - the value to be checked.
     * @return true if there is any value. False otherwise.
     */
    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

}
