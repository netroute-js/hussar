package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;

public record DatabaseSchema(@NonNull String schema, String scriptsLocation) {
    private static final String NO_SCRIPTS = null;

    public static DatabaseSchema scriptLess(@NonNull String schema) {
        return new DatabaseSchema(schema, NO_SCRIPTS);
    }

}
