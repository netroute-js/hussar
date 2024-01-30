package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.helper.FileHelper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ApplicationConfigurationLoader {

    @NonNull
    private final ApplicationConfigurationFlattener configurationFlattener;

    Map<String, Object> load(@NonNull Application application) {
        return application
                .getConfigurationFile()
                .map(this::loadConfigurationFile)
                .orElse(Map.of());
    }

    private Map<String, Object> loadConfigurationFile(Path configurationFile) {
        return Optional
                .of(configurationFile)
                .filter(FileHelper::isPropertiesFile)
                .map(this::loadPropertiesConfigurationFile)
                .orElseGet(() -> Optional
                        .of(configurationFile)
                        .filter(FileHelper::isYamlFile)
                        .map(this::loadYamlConfigurationFile)
                        .orElseThrow(() -> new IllegalStateException("Expected application configuration file to be yaml or properties file"))
                );
    }

    private Map<String, Object> loadPropertiesConfigurationFile(Path configurationFile) {
        try(var fileStream = new FileInputStream(configurationFile.toFile())) {
            var properties = new Properties();
            properties.load(fileStream);

            return properties
                    .entrySet()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
        } catch(Exception ex) {
            throw new IllegalStateException("Could not load properties configuration file", ex);
        }
    }

    private Map<String, Object> loadYamlConfigurationFile(Path configurationFile) {
        try(InputStream stream = new FileInputStream(configurationFile.toFile())) {
            Map<String, Object> configuration = new Yaml().load(stream);

            return configurationFlattener.flatten(configuration);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not load application configuration file", ex);
        }
    }

}
