package pl.netroute.hussar.spring.boot;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.net.URISyntaxException;
import java.nio.file.Path;

class FileLoader {

    private FileLoader() {}

    static Path fromClasspath(String fileName) {
        ValidatorHelper.requireNonEmpty(fileName, "fileName is required");

        try {
            var fileURI = FileLoader.class
                    .getClassLoader()
                    .getResource(fileName)
                    .toURI();

            return Path.of(fileURI);
        } catch(URISyntaxException ex) {
            throw new IllegalStateException("Could not load file from classpath", ex);
        }
    }

}
