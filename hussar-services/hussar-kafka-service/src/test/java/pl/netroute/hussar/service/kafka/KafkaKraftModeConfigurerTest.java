package pl.netroute.hussar.service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.KafkaContainer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KafkaKraftModeConfigurerTest {
    private KafkaKraftModeConfigurer kraftModeConfigurer;

    @BeforeEach
    public void setup() {
        kraftModeConfigurer = new KafkaKraftModeConfigurer();
    }

    @Test
    public void shouldConfigureKraftMode() {
        // given
        var container = createStubContainer();

        // when
        kraftModeConfigurer.configure(container);

        // then
        verify(container).withKraft();
    }

    private KafkaContainer createStubContainer() {
        return mock(KafkaContainer.class, Mockito.RETURNS_DEEP_STUBS);
    }

}
