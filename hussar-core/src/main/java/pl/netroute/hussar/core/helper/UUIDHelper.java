package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.util.UUID;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDHelper {
    private static final int UUID_FIRST_PART_END_INDEX = 8;

    public static String extractFirstPart(@NonNull UUID value) {
        return value.toString().substring(0, UUID_FIRST_PART_END_INDEX);
    }

}
