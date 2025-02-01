package pl.netroute.hussar.service.nosql.mongodb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.api.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.service.nosql.mongodb.assertion.MongoDBAssertionHelper;

import java.util.Optional;

public class MongoDBDockerServiceIT {
    private MongoDBDockerService databaseService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(databaseService)
                .ifPresent(MongoDBDockerService::shutdown);
    }

    @Test
    public void shouldStartDatabaseService() {
        // given
        databaseService = MongoDBDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        // then
        var databaseAssertion = new MongoDBAssertionHelper(databaseService);
        databaseAssertion.assertSingleEndpoint();
        databaseAssertion.asserDatabaseAccessible();
        databaseAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartDatabaseServiceWithFullConfiguration() {
        // given
        var name = "mongodb-instance";
        var dockerVersion = "6.0.13";

        var endpointProperty = "mongodb.url";
        var endpointEnvVariable = "MONGODB_URL";

        var usernameProperty = "mongodb.username";
        var usernameEnvVariable = "MONGODB_USERNAME";

        var passwordProperty = "mongodb.password";
        var passwordEnvVariable = "MONGODB_PASSWORD";

        databaseService = MongoDBDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        // then
        var databaseAssertion = new MongoDBAssertionHelper(databaseService);
        databaseAssertion.assertSingleEndpoint();
        databaseAssertion.asserDatabaseAccessible();
        databaseAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        databaseAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        databaseAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
        databaseAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
        databaseAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
        databaseAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
    }

    @Test
    public void shouldShutdownDatabaseService() {
        var name = "mongodb-instance";
        var dockerVersion = "6.0.13";

        databaseService = MongoDBDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(databaseService);

        databaseService.shutdown();

        // then
        var databaseAssertion = new MongoDBAssertionHelper(databaseService);
        databaseAssertion.assertDatabaseNotAccessible(endpoint);
    }
}
