package org.shadow.lib.exception;

import org.jetbrains.annotations.Nullable;

public class CompileTimeError extends Exception implements DetailsSupplier {
    @Nullable
    private final String details;

    public CompileTimeError(String message) {
        super(message);
        details = null;
    }

    public CompileTimeError(String message, @Nullable String details) {
        super(message);
        this.details = details;
    }

    public @Nullable String getDetails() {
        return details;
    }
}
