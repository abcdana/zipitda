package com.danahub.zipitda.common.exception;

import lombok.Getter;

import java.util.Map;
import java.util.function.Consumer;
@Getter
public class ZipitdaException extends RuntimeException {
    private final ErrorType errorType;
    private final Map<String, Object> parameters;
    private final Consumer<String> logConsumer;
    private final Exception exception;

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
        this.exception = null;
    }


    public ZipitdaException(ErrorType errorType, Map<String, Object> parameters, Consumer<String> logConsumer, Exception exception) {
        this.errorType = errorType;
        this.parameters = parameters;
        this.logConsumer = logConsumer;
        this.exception = exception;
    }
}