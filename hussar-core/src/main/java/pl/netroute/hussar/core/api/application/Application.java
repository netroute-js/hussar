package pl.netroute.hussar.core.api.application;

import pl.netroute.hussar.core.api.Accessible;
import pl.netroute.hussar.core.api.Initializable;
import pl.netroute.hussar.core.api.Lifecycle;

/**
 * Hussar interface that tags {@link Application}. All the implementations have to implement it.
 */
public interface Application extends Accessible, Lifecycle<ApplicationStartupContext>, Initializable {
}
