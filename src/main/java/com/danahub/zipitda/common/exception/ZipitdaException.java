package com.danahub.zipitda.common.exception;

import java.util.Map;
import java.util.function.Consumer;

public class ZipitdaException extends RuntimeException {
    private final ErrorType errorType;
    private final Map<String, Object> parameters;
    private final Consumer<String> logConsumer;

    public ZipitdaException(ErrorType errorType) {
        this(errorType, Map.of(), message -> {}); // 기본값 설정
    }

    public ZipitdaException(ErrorType errorType, Map<String, Object> parameters) {
        this(errorType, parameters, message -> {}); // 로그 미사용 시 기본값 설정
    }

    public ZipitdaException(ErrorType errorType, Map<String, Object> parameters, Consumer<String> logConsumer) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.parameters = parameters;
        this.logConsumer = logConsumer;

        logConsumer.accept(formatLogMessage());
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    private String formatLogMessage() {
        return String.format("ErrorType: %s, Message: %s, Parameters: %s",
                errorType.name(), errorType.getMessage(), parameters);
    }
}