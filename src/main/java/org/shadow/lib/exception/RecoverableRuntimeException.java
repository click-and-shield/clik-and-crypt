package org.shadow.lib.exception;

import org.jetbrains.annotations.Nullable;

public class RecoverableRuntimeException extends RuntimeException implements DetailsSupplier {
  @Nullable
  private final String details;

  public RecoverableRuntimeException(String message) {
    super(message);
    details = null;
  }

  public RecoverableRuntimeException(String message, @Nullable String details) {
    super(message);
    this.details = details;
  }

  public @Nullable String getDetails() {
    return details;
  }
}
