package pl.netroute.hussar.core.test.factory;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.PortFinderHelper;
import pl.netroute.hussar.core.helper.SchemesHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointTestFactory {
    private static final Faker FAKER = Faker.instance();

    public static Endpoint createHttp() {
        return create(SchemesHelper.HTTP_SCHEME);
    }

    public static Endpoint createSchemeLess() {
        return create(SchemesHelper.EMPTY_SCHEME);
    }

    public static Endpoint create(@NonNull String scheme) {
        var host = FAKER.internet().domainName();
        var port = PortFinderHelper.findFreePort();

        return  Endpoint.of(scheme, host, port);
    }

}
