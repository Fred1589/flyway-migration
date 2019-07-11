package com.fb.commons.flyway;

public class MigrationException extends RuntimeException {

    private static final long serialVersionUID = 2875107706496472317L;

    public MigrationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MigrationException(final String message) {
        super(message);
    }

}
