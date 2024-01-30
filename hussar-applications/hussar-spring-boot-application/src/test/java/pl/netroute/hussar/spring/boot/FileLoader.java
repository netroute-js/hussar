package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.URISyntaxException;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FileLoader {

    static Path fromClasspath(@NonNull String fileName) {
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
