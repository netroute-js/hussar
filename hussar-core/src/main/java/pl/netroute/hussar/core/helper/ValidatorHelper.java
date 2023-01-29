package pl.netroute.hussar.core.helper;

import java.util.Objects;
import java.util.function.Supplier;

public class ValidatorHelper {

    public static void requireNonEmpty(String value, String errorMessage) {
        Objects.requireNonNull(errorMessage, "errorMessage is required");

        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static String requireNonEmptyOrElseGet(String value, Supplier<String> elseValue) {
        Objects.requireNonNull(elseValue, "elseValue is required");

        if(value == null || value.isBlank()) {
            value = elseValue.get();

            requireNonEmpty(value, "Expected elseValue to return non empty String");
        }

        return value;
    }

}