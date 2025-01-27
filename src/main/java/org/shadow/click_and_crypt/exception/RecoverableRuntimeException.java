package org.shadow.click_and_crypt.exception;

import org.jetbrains.annotations.Nullable;

public class RecoverableRuntimeException extends org.shadow.lib.exception.RecoverableRuntimeException {
    private final RecoverableErrorCause errorCause;

    public RecoverableRuntimeException(String message, RecoverableErrorCause errorCause) {
        super(message);
        this.errorCause = errorCause;
    }

    public RecoverableRuntimeException(String message, @Nullable String details, RecoverableErrorCause errorCause) {
        super(message, details);
        this.errorCause = errorCause;
    }

    public RecoverableErrorCause getErrorCause() {
        return errorCause;
    }
}
