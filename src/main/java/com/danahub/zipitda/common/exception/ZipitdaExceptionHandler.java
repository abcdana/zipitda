package com.danahub.zipitda.common.exception;

import com.danahub.zipitda.common.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ZipitdaExceptionHandler {

    @ExceptionHandler(ZipitdaException.class)
    public ResponseEntity<CommonResponse<?>> handleException(ZipitdaException e) {
        log.warn("Exception: {}", e.getErrorType().getMessage());

        return ResponseEntity
                .status(e.getErrorType().getHttpStatus())
                .body(new CommonResponse<>(e.getErrorType().getErrorCode(), e.getErrorType().getMessage(), null));
    }
}