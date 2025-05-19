package pl.netroute.hussar.core;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.logging.ApplicationLogger;

@InternalUseOnly
class ApplicationRestarter {

    void restart(@NonNull Application application) {
        application.restart();

        ApplicationLogger.logApplicationRestarted(application);
    }

}
