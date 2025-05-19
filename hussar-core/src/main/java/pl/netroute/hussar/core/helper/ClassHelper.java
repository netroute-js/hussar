package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassHelper {

    public static String toSimpleName(@NonNull Object object) {
        return object.getClass().getSimpleName();
    }

}
