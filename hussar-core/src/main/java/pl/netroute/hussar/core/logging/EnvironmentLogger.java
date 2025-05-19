package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.environment.EnvironmentConfigurationExtractor;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.helper.ClassHelper;
import pl.netroute.hussar.core.helper.StringHelper;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentLogger {
    private static final String COMA_DELIMITER = ", ";
    private static final String NEW_LINE_DELIMITER = "\n";
    private static final String NOT_APPLICABLE = "N/A";

    private static final String ENVIRONMENT_STARTUP_TEMPLATE =
            """
            
            Environment
            Type: %s
            Configurer: %s
            Application:
            %s
            Services:
            %s
            Configurations:
            %s
            Started in: %s
            """;

    private static final String APPLICATION_TEMPLATE = "- %s [endpoints: %s]";
    private static final String SERVICE_TEMPLATE = "- %s [type: %s, endpoints: %s]";
    private static final String CONFIGURATION_TEMPLATE = "- %s = %s";

    public static void logEnvironmentStartup(@NonNull EnvironmentConfigurerProvider provider, @NonNull Environment environment, @NonNull Duration startupTime) {
        var application = environment.getApplication();
        var serviceRegistry = environment.getServiceRegistry();
        var configurationRegistry = EnvironmentConfigurationExtractor.extract(environment);

        var environmentType = ClassHelper.toSimpleName(environment);
        var configurerType = ClassHelper.toSimpleName(provider);
        var formattedApplication  = formatApplication(application);
        var formattedServices = formatServices(serviceRegistry);
        var formattedConfigurations = formatConfigurations(configurationRegistry);

        var logText = ENVIRONMENT_STARTUP_TEMPLATE.formatted(
                environmentType,
                configurerType,
                formattedApplication,
                formattedServices,
                formattedConfigurations,
                startupTime
        );

        log.info(logText);
    }

    private static String formatApplication(Application application) {
        var applicationType = ClassHelper.toSimpleName(application);
        var applicationEndpoints = formatEndpoints(application.getEndpoints());

        return APPLICATION_TEMPLATE.formatted(applicationType, applicationEndpoints);
    }

    private static String formatServices(ServiceRegistry serviceRegistry) {
        return Optional
                .of(serviceRegistry.getEntries())
                .filter(actualServices -> !actualServices.isEmpty())
                .map(actualServices -> StringHelper.join(EnvironmentLogger::formatService, NEW_LINE_DELIMITER, actualServices))
                .orElse(NOT_APPLICABLE);
    }

    private static String formatService(Service service) {
        var serviceType = ClassHelper.toSimpleName(service);
        var serviceName = service.getName();
        var serviceEndpoints = formatEndpoints(service.getEndpoints());

        return SERVICE_TEMPLATE.formatted(serviceType, serviceName, serviceEndpoints);
    }

    private static String formatConfigurations(ConfigurationRegistry configurationRegistry) {
        return configurationRegistry
                .getEntries()
                .stream()
                .collect(Collectors.groupingBy(ConfigurationEntry::getClass))
                .values()
                .stream()
                .map(actualConfigurations -> actualConfigurations
                        .stream()
                        .sorted(EnvironmentLogger::sortLexically)
                        .toList())
                .map(actualConfigurations -> StringHelper.join(EnvironmentLogger::formatConfiguration, NEW_LINE_DELIMITER, actualConfigurations))
                .collect(Collectors.joining());
    }

    private static String formatConfiguration(ConfigurationEntry configurationEntry) {
        var configName = configurationEntry.formattedName();
        var configValue = configurationEntry.value();

        return CONFIGURATION_TEMPLATE.formatted(configName, configValue);
    }

    private static String formatEndpoints(List<Endpoint> endpoints) {
        return Optional
                .of(endpoints)
                .filter(actualEndpoints -> !actualEndpoints.isEmpty())
                .map(actualEndpoints -> StringHelper.join(Endpoint::address, COMA_DELIMITER, actualEndpoints))
                .orElse(NOT_APPLICABLE);
    }

    private static int sortLexically(ConfigurationEntry entry1, ConfigurationEntry entry2) {
        return entry1.name().compareTo(entry2.name());
    }

}
