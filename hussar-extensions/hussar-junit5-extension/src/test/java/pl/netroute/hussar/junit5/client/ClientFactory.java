package pl.netroute.hussar.junit5.client;

import feign.Feign;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import pl.netroute.hussar.core.Endpoint;

import java.util.Objects;
import java.util.function.Consumer;

public class ClientFactory {

    private ClientFactory() {}

    public static <T> T create(Endpoint endpoint, Class<T> type) {
        Objects.requireNonNull(endpoint, "endpoint is required");
        Objects.requireNonNull(type, "type is required");

        String url = endpoint.getAddress();

        Feign.Builder clientBuilder = Feign.builder()
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
            // do nothing
        }

    }
}
