package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.HussarApplicationRestart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnnotationDetectorTest {
    private AnnotationDetector detector;

    @BeforeEach
    public void setup() {
        detector = new AnnotationDetector();
    }

    @Test
    public void shouldDetectAnnotation() {
        // given
        var method = mock(Method.class);
        var incrementor = new Incrementor();

        when(method.getAnnotation(HussarApplicationRestart.class)).thenReturn(new HussarApplicationRestart() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return HussarApplicationRestart.class;
            }

        });

        // when
        detector.detect(method, HussarApplicationRestart.class, annotation -> incrementor.increment());

        // then
        assertAnnotationDetected(incrementor);
    }

    @Test
    public void shouldNotDetectAnnotation() {
        // given
        var method = mock(Method.class);
        var incrementor = new Incrementor();

        // when
        detector.detect(method, HussarApplicationRestart.class, annotation -> incrementor.increment());

        // then
        assertAnnotationNotDetected(incrementor);
    }

    private void assertAnnotationNotDetected(Incrementor incrementor) {
        assertThat(incrementor.getCounter()).isZero();
    }

    private void assertAnnotationDetected(Incrementor incrementor) {
        assertThat(incrementor.getCounter()).isOne();
    }

    private static class Incrementor {
        private int counter;

        void increment() {
            counter++;
        }

        int getCounter() {
            return counter;
        }
    }

}
