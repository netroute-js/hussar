package pl.netroute.hussar.service.nosql.mongodb;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerServiceConfigurer;
import pl.netroute.hussar.service.nosql.mongodb.assertion.MongoDBAssertionHelper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MongoDBDockerServiceIT extends BaseServiceIT<MongoDBDockerService> {

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var databaseAssertion = new MongoDBAssertionHelper(actualService);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserMongoDBAccessible();
            databaseAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "mongodb-instance";
        var dockerVersion = "6.0.13";

        var endpointProperty = "mongodb.url";
        var endpointEnvVariable = "MONGODB_URL";

        var endpointWithCredentialsProperty = "mongodb.alternative.url";
        var endpointWithCredentialsEnvVariable = "MONGODB_ALTERNATIVE_URL";

        var usernameProperty = "mongodb.username";
        var usernameEnvVariable = "MONGODB_USERNAME";

        var passwordProperty = "mongodb.password";
        var passwordEnvVariable = "MONGODB_PASSWORD";

        var service = MongoDBDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerEndpointWithCredentialsUnderProperty(endpointWithCredentialsProperty)
                .registerEndpointWithCredentialsUnderEnvironmentVariable(endpointWithCredentialsEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var databaseAssertion = new MongoDBAssertionHelper(actualService);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserMongoDBAccessible();
            databaseAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
            databaseAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
            databaseAssertion.assertRegisteredEndpointWithCredentialsUnderProperty(endpointWithCredentialsProperty);
            databaseAssertion.assertRegisteredEndpointWithCredentialsUnderEnvironmentVariable(endpointWithCredentialsEnvVariable);
            databaseAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
            databaseAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
            databaseAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
            databaseAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, BiConsumer<MongoDBDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (BiConsumer<MongoDBDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var databaseAssertion = new MongoDBAssertionHelper(actualService);
            databaseAssertion.assertMongoDBNotAccessible(endpoint);
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, BiConsumer<MongoDBDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
