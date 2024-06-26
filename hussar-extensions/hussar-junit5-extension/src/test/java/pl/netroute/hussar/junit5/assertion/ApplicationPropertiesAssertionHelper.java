package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationPropertiesAssertionHelper {

    public static void assertPropertyConfigured(@NonNull String property,
                                                @NonNull String expectedValue,
                                                @NonNull SimpleApplicationClient applicationClient) {
        var foundProperty = applicationClient.getProperty(property);

        assertThat(foundProperty).hasValue(expectedValue);
    }

}
