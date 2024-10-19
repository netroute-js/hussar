package pl.netroute.hussar.core.api;

/**
 * Hussar interface that tags the {@link Service}. All implementations have to implement it.
 */
public interface Service extends Accessible, Lifecycle<ServiceStartupContext>, ResolvableConfiguration {

    /**
     * Returns name of the {@link Service}.
     *
     * @return the actual name of the {@link Service}.
     */
    String getName();

}
