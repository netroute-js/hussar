package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.NonNull;

public record MongoDBCredentials(@NonNull String username,
                                 @NonNull String password) {
}
