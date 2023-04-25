package pl.netroute.hussar.spring.boot;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

class ConfigurationFileResolver {
    static final String APPLICATION_YML = "application.yml";
    static final String APPLICATION_PROPERTIES = "application.properties";

    Optional<Path> resolveDefault(Class<?> applicationClass) {
        Objects.requireNonNull(applicationClass, "applicationClass is required");

        var classLoader = applicationClass.getClassLoader();

        return resolveApplicationYml(classLoader).or(() -> resolveApplicationProperties(classLoader));
    }

    private Optional<Path> resolveApplicationYml(ClassLoader classLoader) {
        return resolveClasspathFile(classLoader, APPLICATION_YML);
    }

    private Optional<Path> resolveApplicationProperties(ClassLoader classLoader) {
        return resolveClasspathFile(classLoader, APPLICATION_PROPERTIES);
    }

    private Optional<Path> resolveClasspathFile(ClassLoader classLoader, String fileName) {
        return Optional
                .ofNullable(classLoader.getResource(fileName))
                .map(this::mapToURI)
                .map(Path::of);
    }

    private URI mapToURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not resolve URI", ex);
        }
    }

}
