package org.sto.exception;

import java.util.function.Supplier;

public class BadRequestException extends RuntimeException {
    public BadRequestException(final String message) {
        super(message);
    }
}