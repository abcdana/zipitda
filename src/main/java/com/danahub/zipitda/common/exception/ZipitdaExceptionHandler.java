package com.danahub.zipitda.common.exception;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ZipitdaExceptionHandler {

    @ExceptionHandler(ZipitdaException.class)
    public ResponseEntity<CommonResponse<?>> handleException(ZipitdaException e) {
        String log = e.getErrorType().getMessage();
        String parameters = Joiner.on(',').withKeyValueSeparator('=').join(e.getParameters());
        String exceptions = ExceptionUtils.getStackTrace(e);
        e.getLogConsumer().accept(
                """
                ───────────────────────────────
                       ZipitdaException
                ───────────────────────────────
                ErrorType  : %s
                Parameter  : %s
                StackTrace :
                %s
                ───────────────────────────────
                """.formatted(log, parameters, exceptions)
        );

        return ResponseEntity
                .status(e.getErrorType().getHttpStatus())
                .body(new CommonResponse<>(e.getErrorType().getErrorCode(), e.getErrorType().getMessage(), null));
    }
}