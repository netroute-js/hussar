package pl.netroute.hussar.junit5.client;

import feign.Feign;
import feign.optionals.OptionalDecoder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import pl.netroute.hussar.core.api.Endpoint;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientFactory {

    public static <T> T create(@NonNull Endpoint endpoint,
                               @NonNull Class<T> type) {
        var url = endpoint.address();

        var clientBuilder = Feign.builder()
                .encoder(new SpringEncoder(HttpMessageConverters::new))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(HttpMessageConverters::new, new EmptyObjectProvider<>()))))
                .dismiss404()
                .contract(new SpringMvcContract());

        return clientBuilder.target(type, url);
    }

    private static class EmptyObjectProvider<T> implements ObjectProvider<T> {

        @Override
        public T getObject(Object... args) throws BeansException {
            return null;
        }

        @Override
        public T getIfAvailable() throws BeansException {
            return null;
        }

        @Override
        public T getIfUnique() throws BeansException {
            return null;
        }

        @Override
        public T getObject() throws BeansException {
            return null;
        }

        @Override
        public void forEach(Consumer action) {
        }

    }
}
