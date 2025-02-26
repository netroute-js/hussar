package pl.netroute.hussar.core;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;

@Slf4j
@InternalUseOnly
class ApplicationRestarter {

    void restart(@NonNull Application application) {
        log.info("Restarting Application[{}]", application.getClass());

        application.restart();
    }

}
