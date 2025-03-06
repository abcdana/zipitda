package com.danahub.zipitda.common.dto;

public record CommonResponse<T>(int code, String message, T data) {

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(0, "Success", data);
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(0, "Success", null);
    }

    public static <T> CommonResponse<T> error(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}