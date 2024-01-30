package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.nio.file.Path;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHelper {
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";
    private static final List<String> YAML_FILE_EXTENSIONS = List.of(".yml", ".yaml");

    public static boolean isPropertiesFile(@NonNull Path file) {
        return file
                .toFile()
                .getName()
                .endsWith(PROPERTIES_FILE_EXTENSION);
    }

    public static boolean isYamlFile(@NonNull Path file) {
        String fileName = file
                .toFile()
                .getName();

        return YAML_FILE_EXTENSIONS
                .stream()
                .anyMatch(fileName::endsWith);
    }

}
