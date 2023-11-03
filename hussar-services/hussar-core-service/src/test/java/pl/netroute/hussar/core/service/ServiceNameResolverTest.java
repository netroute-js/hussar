package pl.netroute.hussar.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceNameResolverTest {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    private static final String DEFAULT_SERVICE_NAME_TEMPLATE = "%s_%s";

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldResolveDefaultName(String name) {
        // given
        var serviceName = "some-service";

        // name
        var resolvedName = ServiceNameResolver.resolve(serviceName, name);

        // then
        assertResolvedDefaultName(resolvedName, serviceName);
    }

    @Test
    public void shouldResolveCustomName() {
        // given
        var serviceName = "some-service";
        var customName = "custom-service";

        // name
        var resolvedName = ServiceNameResolver.resolve(serviceName, customName);

        // then
        assertResolvedCustomName(resolvedName, customName);
    }

    private void assertResolvedCustomName(String resolvedName, String expectedName) {
        assertThat(resolvedName).isEqualTo(expectedName);
    }

    private void assertResolvedDefaultName(String resolvedName, String serviceName) {
        var defaultNameRegex = String.format(DEFAULT_SERVICE_NAME_TEMPLATE, serviceName, UUID_REGEX);

        assertThat(resolvedName).matches(defaultNameRegex);
    }

}
