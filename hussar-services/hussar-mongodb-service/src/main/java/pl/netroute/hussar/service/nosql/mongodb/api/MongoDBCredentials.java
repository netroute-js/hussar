package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.NonNull;

/**
 * A custom type representing MongoDB credentials.
 */
public record MongoDBCredentials(@NonNull String username,
                                 @NonNull String password) {
}
