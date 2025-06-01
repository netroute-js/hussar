package pl.netroute.hussar.core.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class DockerAliasGeneratorTest {
    private DockerAliasGeneratorVerifier verifier;

    @BeforeEach
    public void setup() {
        verifier = new DockerAliasGeneratorVerifier();
    }

    @Test
    public void shouldGenerateRandomDockerAlias() {
        // given
        var startRange = 0;
        var aliasCount = 10000;

        // when
        var aliases = IntStream
                .range(startRange, aliasCount)
                .mapToObj(index -> DockerAliasGenerator.generate())
                .distinct()
                .toList();

        // then
        verifier.verifyRandomDockerAliasesGenerated(aliases, aliasCount);
    }

}
