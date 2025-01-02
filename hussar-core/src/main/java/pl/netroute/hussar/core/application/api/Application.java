package pl.netroute.hussar.core.application.api;

import pl.netroute.hussar.core.api.Accessible;
import pl.netroute.hussar.core.api.Initializable;
import pl.netroute.hussar.core.api.Restartable;
import pl.netroute.hussar.core.api.Startable;
import pl.netroute.hussar.core.api.Stoppable;

/**
 * Hussar interface that tags {@link Application}. All the implementations have to implement it.
 */
public interface Application extends Accessible, Startable<ApplicationStartupContext>, Stoppable, Initializable, Restartable {
}
