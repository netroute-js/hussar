 package pl.netroute.hussar.service.nosql.redis;

 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import pl.netroute.hussar.core.api.ConfigurationEntry;
 import pl.netroute.hussar.core.api.ConfigurationRegistry;
 import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
 import pl.netroute.hussar.core.api.MapConfigurationRegistry;
 import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
 import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;

 import java.util.Set;

 import static org.assertj.core.api.Assertions.assertThat;

public class RedisCredentialsRegistererTest {
    private ConfigurationRegistry configurationRegistry;
    private RedisCredentialsRegisterer credentialsRegisterer;

    @BeforeEach
    public void setup() {
        configurationRegistry = new MapConfigurationRegistry();

        credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
    }

    @Test
    public void shouldRegisterUsernameUnderProperty() {
        // given
        var username = "some-user";
        var password = "some-password";
        var credentials = new RedisCredentials(username, password);

        var usernameProperty = "a.property";

        // when
        credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty);

        // then
        var registeredUsernameProperty = new PropertyConfigurationEntry(usernameProperty, username);
        var registeredProperties = Set.of(registeredUsernameProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterUsernameUnderEnvironmentVariable() {
        // given
        var username = "some-user";
        var password = "some-password";
        var credentials = new RedisCredentials(username, password);

        var usernameEnvVariable = "a.property";

        // when
        credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable);

        // then
        var registeredUsernameEnvVariable = new EnvVariableConfigurationEntry(usernameEnvVariable, username);
        var registeredEnvVariables = Set.of(registeredUsernameEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    @Test
    public void shouldRegisterPasswordUnderProperty() {
        // given
        var username = "some-user";
        var password = "some-password";
        var credentials = new RedisCredentials(username, password);

        var passwordProperty = "a.password";

        // when
        credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty);

        // then
        var registeredUsernameProperty = new PropertyConfigurationEntry(passwordProperty, password);
        var registeredProperties = Set.of(registeredUsernameProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterPasswordUnderEnvironmentVariable() {
        // given
        var username = "some-user";
        var password = "some-password";
        var credentials = new RedisCredentials(username, password);

        var passwordEnvVariable = "a.property";

        // when
        credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable);

        // then
        var registeredPasswordEnvVariable = new EnvVariableConfigurationEntry(passwordEnvVariable, password);
        var registeredEnvVariables = Set.of(registeredPasswordEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    private void assertRegisteredConfigs(Set<? extends ConfigurationEntry> registeredEntries) {
        assertThat(configurationRegistry.getEntries()).containsExactlyInAnyOrderElementsOf(registeredEntries);
    }

}
