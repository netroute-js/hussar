package pl.netroute.hussar.core.test.factory;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;

import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationEntryTestFactory {
    private static final FakeValuesService VALUE_FAKER = new FakeValuesService(Locale.ENGLISH, new RandomService());

    private static final String PROPERTY_NAME_FORMAT = "application.????.???";
    private static final String ENV_VARIABLE_NAME_FORMAT = "?????_ENV";
    private static final String VALUE_FORMAT = "?????????";

    private static final boolean UPPER_CASE = true;

    public static PropertyConfigurationEntry createProperty() {
        var propertyName = VALUE_FAKER.bothify(PROPERTY_NAME_FORMAT);
        var propertyValue = VALUE_FAKER.bothify(VALUE_FORMAT);

        return ConfigurationEntry.property(propertyName, propertyValue);
    }

    public static EnvVariableConfigurationEntry createEnvVariable() {
        var envVariableName = VALUE_FAKER.bothify(ENV_VARIABLE_NAME_FORMAT, UPPER_CASE);
        var envVariableValue = VALUE_FAKER.bothify(VALUE_FORMAT);

        return ConfigurationEntry.envVariable(envVariableName, envVariableValue);
    }

}
