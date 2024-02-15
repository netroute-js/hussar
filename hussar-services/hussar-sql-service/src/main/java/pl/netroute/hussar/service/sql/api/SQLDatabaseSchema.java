package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;

public record SQLDatabaseSchema(@NonNull String name,
                                String scriptsLocation) {
    private static final String NO_SCRIPTS = null;

    public static SQLDatabaseSchema scriptLess(@NonNull String name) {
        return new SQLDatabaseSchema(name, NO_SCRIPTS);
    }

}
