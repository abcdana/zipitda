package com.danahub.zipitda.common.exception;

public class ZipitdaException extends RuntimeException {
    private final ErrorType errorType;

    public ZipitdaException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}