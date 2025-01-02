package pl.netroute.hussar.junit5.config;

import pl.netroute.hussar.core.application.api.ModuleApplication;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurer;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.junit5.factory.MySQLServiceFactory;

public class ModuleTestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {

    @Override
    public EnvironmentConfigurer provide() {
        var application = ModuleApplication.newApplication();
        var mySQLService = MySQLServiceFactory.create();

        return LocalEnvironmentConfigurer
                .newInstance()
                .withApplication(application)
                .withService(mySQLService)
                .done();
    }

}
