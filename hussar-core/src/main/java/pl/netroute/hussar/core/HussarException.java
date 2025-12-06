package pl.netroute.hussar.core;

import lombok.NonNull;

public class HussarException extends RuntimeException {

    public HussarException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

}
