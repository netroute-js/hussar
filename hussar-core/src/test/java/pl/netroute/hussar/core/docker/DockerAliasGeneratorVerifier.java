package pl.netroute.hussar.core.docker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class DockerAliasGeneratorVerifier {
    private static final String DOCKER_ALIAS_REGEX = "^hussar-svc-[a-f0-9]{8}-[a-f0-9]{8}$";

    public void verifyRandomDockerAliasesGenerated(@NonNull List<String> aliases, int expectedSize) {
        assertThat(aliases).hasSize(expectedSize);

        aliases.forEach(this::verifyRandomDockerAliasGenerated);
    }

    private void verifyRandomDockerAliasGenerated(String alias) {
        assertThat(alias).matches(DOCKER_ALIAS_REGEX);
    }

}