package pl.netroute.hussar.core.helper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class FileHelper {
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";
    private static final List<String> YAML_FILE_EXTENSIONS = List.of(".yml", ".yaml");

    private FileHelper() {}

    public static boolean isPropertiesFile(Path file) {
        Objects.requireNonNull(file, "file is required");

        return file
                .toFile()
                .getName()
                .endsWith(PROPERTIES_FILE_EXTENSION);
    }

    public static boolean isYamlFile(Path file) {
        Objects.requireNonNull(file, "file is required");

        String fileName = file
                .toFile()
                .getName();

        return YAML_FILE_EXTENSIONS
                .stream()
                .anyMatch(fileName::endsWith);
    }

}
