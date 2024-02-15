package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;

public record SQLDatabaseCredentials(@NonNull String username,
                                     @NonNull String password) {
}
