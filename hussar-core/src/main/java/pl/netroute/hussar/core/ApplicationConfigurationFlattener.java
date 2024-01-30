package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ApplicationConfigurationFlattener {
    private static final String ROOT_KEY = "";
    private static final String KEY_NAME_SEPARATOR = ".";
    private static final String COLLECTION_INDEX_TEMPLATE = "[%d]";

    Map<String, Object> flatten(@NonNull Map<String, Object> configuration) {
        return configuration
                .entrySet()
                .stream()
                .map(configurationEntry -> flattenConfigurationEntry(ROOT_KEY, configurationEntry))
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<Map.Entry<String, Object>> flattenConfigurationEntry(String parentKey, Map.Entry<String, Object> configurationEntry) {
        var configurationKey = configurationEntry.getKey();
        var configurationValue = configurationEntry.getValue();

        if(isCollectionConfiguration(configurationValue)) {
            var collectionConfiguration = (Collection<String>) configurationValue;
            var listConfiguration = List.copyOf(collectionConfiguration);

            return IntStream
                    .range(0, listConfiguration.size())
                    .mapToObj(index -> createCollectionConfigurationEntry(index, parentKey, configurationKey, listConfiguration.get(index)))
                    .toList();
        } else if(isMapConfiguration(configurationValue)) {
            var mapConfigurationKey = createSimpleConfigurationKey(parentKey, configurationKey);
            var mapConfiguration = (Map<String, Object>) configurationValue;

            return mapConfiguration
                    .entrySet()
                    .stream()
                    .map(actualConfigurationEntry -> flattenConfigurationEntry(mapConfigurationKey, actualConfigurationEntry))
                    .flatMap(Collection::stream)
                    .toList();
        } else {
            var simpleConfigurationKey = createSimpleConfigurationKey(parentKey, configurationKey);
            var simpleConfigurationEntry = createConfigurationEntry(simpleConfigurationKey, configurationValue);

            return List.of(simpleConfigurationEntry);
        }
    }

    private boolean isCollectionConfiguration(Object configurationValue) {
        return configurationValue instanceof Collection;
    }

    private boolean isMapConfiguration(Object configurationValue) {
        return configurationValue instanceof Map;
    }

    private String createSimpleConfigurationKey(String parentKey, String configurationKey) {
        var separatedParentKey = Optional
                .of(parentKey)
                .filter(actualParentKey -> !actualParentKey.isBlank())
                .map(actualParentKey -> actualParentKey + KEY_NAME_SEPARATOR)
                .orElse(parentKey);

        return separatedParentKey + configurationKey;
    }

    private String createCollectionConfigurationKey(int index, String parentKey, String configurationKey) {
        var simpleConfigurationKey = createSimpleConfigurationKey(parentKey, configurationKey);
        var indexKey = String.format(COLLECTION_INDEX_TEMPLATE, index);

        return simpleConfigurationKey + indexKey;
    }

    private Map.Entry<String, Object> createCollectionConfigurationEntry(int index, String parentKey, String configurationKey, Object configurationValue) {
        var collectionConfigurationKey = createCollectionConfigurationKey(index, parentKey, configurationKey);
        var collectionConfigurationEntry = createConfigurationEntry(collectionConfigurationKey, configurationValue);

        return collectionConfigurationEntry;
    }

    private Map.Entry<String, Object> createConfigurationEntry(String key, Object value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}
