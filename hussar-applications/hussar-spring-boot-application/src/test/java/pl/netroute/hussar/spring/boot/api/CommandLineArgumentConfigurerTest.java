package pl.netroute.hussar.spring.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineArgumentConfigurerTest {
    private static final String ARGUMENT_TEMPLATE = "--%s=%s";

    private CommandLineArgumentConfigurer argumentConfigurer;

    @BeforeEach
    public void setup() {
        argumentConfigurer = new CommandLineArgumentConfigurer();
    }

    @Test
    public void shouldConfigureArguments() {
        // given
        var serverPortProperty = ConfigurationEntry.property("server.port", "30000");
        var metricsUrlEnvVariable = ConfigurationEntry.envVariable("METRICS_URL", "https://hussar.dev/metrics");
        var externalConfigurations = Set.<ConfigurationEntry>of(serverPortProperty, metricsUrlEnvVariable);

        // when
        var arguments = argumentConfigurer.configure(externalConfigurations);

        // then
        assertArgumentsConfigured(arguments, externalConfigurations);
    }

    @Test
    public void shouldSkipConfiguringArguments() {
        // given
        var externalConfigurations = Set.<ConfigurationEntry>of();

        // when
        var arguments = argumentConfigurer.configure(externalConfigurations);

        // then
        assertNoArgumentsConfigured(arguments);
    }

    private void assertArgumentsConfigured(List<String> arguments,
                                           Set<ConfigurationEntry> externalConfigurations) {
        var expectedArguments = externalConfigurations
                .stream()
                .map(configuration -> ARGUMENT_TEMPLATE.formatted(configuration.name(), configuration.value()))
                .toList();

        assertThat(arguments).isEqualTo(expectedArguments);
    }

    private void assertNoArgumentsConfigured(List<String> arguments) {
        assertThat(arguments).isEmpty();
    }

}
