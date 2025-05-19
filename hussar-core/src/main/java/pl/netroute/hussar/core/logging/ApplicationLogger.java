package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.helper.ClassHelper;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationLogger {
    private static final String APPLICATION_RESTARTED_TEMPLATE = "Restarted Application[type: %s]";

    public static void logApplicationRestarted(@NonNull Application application) {
        var applicationType = ClassHelper.toSimpleName(application);

        var logText = APPLICATION_RESTARTED_TEMPLATE.formatted(applicationType);

        log.info(logText);
    }

}
