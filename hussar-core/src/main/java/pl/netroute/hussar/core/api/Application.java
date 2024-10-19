package pl.netroute.hussar.core.api;

/**
 * Hussar interface that tags {@link Application}. All the implementations have to implement it.
 */
public interface Application extends Accessible, Lifecycle<ApplicationStartupContext>, Initializable {
}
