package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;

/**
 * A custom type representing SQL DB schema.
 */
public record SQLDatabaseSchema(@NonNull String name,
                                String scriptsLocation) {
    private static final String NO_SCRIPTS = null;

    /**
     * Factory method to create script less SQL DB schema.
     *
     * @param name - the name of the DB schema
     * @return an instance of script less SQL DB schema
     */
    public static SQLDatabaseSchema scriptLess(@NonNull String name) {
        return new SQLDatabaseSchema(name, NO_SCRIPTS);
    }

}
