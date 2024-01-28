package pl.netroute.hussar.service.sql;

import lombok.NonNull;

public record DatabaseCredentials(@NonNull String username,
                                  @NonNull String password) {
}
