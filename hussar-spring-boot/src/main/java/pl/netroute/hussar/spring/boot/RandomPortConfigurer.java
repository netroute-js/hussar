package pl.netroute.hussar.spring.boot;

import pl.netroute.hussar.core.helper.PortFinderHelper;
import pl.netroute.hussar.core.helper.PropertiesHelper;

class RandomPortConfigurer {

    RandomPortConfigurer() {}

    void configure() {
        var port = PortFinderHelper.findFreePort();

        PropertiesHelper.setProperty(SpringProperties.SERVER_PORT, port + "");
    }

}
