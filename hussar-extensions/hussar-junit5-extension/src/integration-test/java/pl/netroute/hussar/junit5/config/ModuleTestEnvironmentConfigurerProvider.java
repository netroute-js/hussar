package pl.netroute.hussar.junit5.config;

import pl.netroute.hussar.core.api.application.ModuleApplication;
import pl.netroute.hussar.core.api.environment.EnvironmentConfigurer;
import pl.netroute.hussar.core.api.environment.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.environment.LocalEnvironmentConfigurer;
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
