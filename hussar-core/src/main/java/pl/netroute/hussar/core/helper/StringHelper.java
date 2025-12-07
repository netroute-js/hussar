package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A String helper class
 */
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringHelper {
    private static final String ALPHA_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Checks whether the value is not null and not blank.
     *
     * @param value - the value to be checked.
     * @return true if there is any value. False otherwise.
     */
    public static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Joins a collection of values into a single string with a specified delimiter.
     * Each value is first converted to a string using the provided mapper function.
     *
     * @param <T> the type of elements in the collection
     * @param mapper the function to convert each element to a string
     * @param delimiter the string to be used between each element
     * @param values the collection of elements to join
     * @return a string containing all mapped elements separated by the delimiter
     * @throws NullPointerException if mapper, delimiter, or values is null
     */
    public static <T> String join(@NonNull Function<T, String> mapper, @NonNull String delimiter, @NonNull Collection<T> values) {
        return values
                .stream()
                .map(mapper)
                .collect(Collectors.joining(delimiter));
    }

    public static String randomText(int length) {
        var textBuilder = new StringBuilder();
        var availableCharactersLength = ALPHA_CHARACTERS.length();

        IntStream
                .range(0, length)
                .mapToObj(nextCharIndex -> RANDOM.nextInt(availableCharactersLength))
                .map(ALPHA_CHARACTERS::charAt)
                .forEach(textBuilder::append);

        return textBuilder.toString();
    }

    public static String toText(@NonNull InputStream inputStream) {
        try {
            var bytes = inputStream.readAllBytes();

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not deserialize InputStream to String", ex);
        }
    }

    public static boolean isTextBlank(String text) {
        return text == null || text.isBlank();
    }

}
