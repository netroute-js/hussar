package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.test.factory.EnvironmentTestFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentRegistryTest {

    @Test
    public void shouldRegisterEnvironment() {
        // given
        var testInstance = new Object();
        var environment = EnvironmentTestFactory.create();
        var registry = new EnvironmentRegistry();

        // when
        registry.register(testInstance, environment);

        // then
        assertEnvironmentRegistered(registry, testInstance, environment);
    }

    @Test
    public void shouldSkipRegisteringEnvironmentIfAlreadyPresent() {
        // given
        var testInstance = new Object();

        var environmentA = EnvironmentTestFactory.create();
        var environmentB = EnvironmentTestFactory.create();

        var registry = new EnvironmentRegistry(Map.of(testInstance, environmentA));

        // when
        registry.register(testInstance, environmentB);

        // then
        assertEnvironmentRegistered(registry, testInstance, environmentA);
    }

    @Test
    public void shouldReturnRegisteredEnvironment() {
        // given
        var testInstance = new Object();
        var environment = EnvironmentTestFactory.create();
        var registry = new EnvironmentRegistry(Map.of(testInstance, environment));

        // when
        var maybeEnvironment = registry.getEntry(testInstance);

        // then
        assertEnvironmentPresent(maybeEnvironment, environment);
    }

    @Test
    public void shouldReturnEmptyWhenNoEnvironmentExists() {
        // given
        var testInstance = new Object();
        var registry = new EnvironmentRegistry();

        // when
        var maybeEnvironment = registry.getEntry(testInstance);

        // then
        assertNoEnvironmentPresent(maybeEnvironment);
    }

    @Test
    public void shouldReturnNoEntriesWhenNoEnvironmentExists() {
        // given
        var registry = new EnvironmentRegistry();

        // when
        var environments = registry.getEntries();

        // then
        assertNoEnvironmentPresent(environments);
    }

    private void assertEnvironmentRegistered(EnvironmentRegistry registry, Object testInstance, Environment environment) {
        assertThat(registry.getEntry(testInstance)).hasValue(environment);
    }

    private void assertEnvironmentPresent(Optional<Environment> maybeEnvironment, Environment environment) {
        assertThat(maybeEnvironment).hasValue(environment);
    }

    private void assertNoEnvironmentPresent(Optional<Environment> maybeEnvironment) {
        assertThat(maybeEnvironment).isEmpty();
    }

    private void assertNoEnvironmentPresent(Set<Environment> environments) {
        assertThat(environments).isEmpty();
    }

}
