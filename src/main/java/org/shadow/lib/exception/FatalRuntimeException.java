package org.shadow.lib.exception;

import org.jetbrains.annotations.Nullable;

public class FatalRuntimeException extends RuntimeException implements DetailsSupplier {
    @Nullable private final String details;

    public FatalRuntimeException(String message) {
        super(message);
        details = null;
    }
    public FatalRuntimeException(String message, @Nullable String details) {
        super(message);
        this.details = details;
    }

    public @Nullable String getDetails() {
        return details;
    }
}
