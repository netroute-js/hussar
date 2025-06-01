package pl.netroute.hussar.core.docker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.UUIDHelper;

import java.util.UUID;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerAliasGenerator {
    private static final String DOCKER_ALIAS_TEMPLATE = "hussar-svc-%s-%s";

    public static String generate() {
        var aliasFirstPart = UUIDHelper.extractFirstPart(UUID.randomUUID());
        var aliasSecondPart = UUIDHelper.extractFirstPart(UUID.randomUUID());

        return DOCKER_ALIAS_TEMPLATE.formatted(aliasFirstPart, aliasSecondPart);
    }

}
