package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;

/**
 * A custom type representing SQL DB credentials.
 */
public record SQLDatabaseCredentials(@NonNull String username,
                                     @NonNull String password) {
}
