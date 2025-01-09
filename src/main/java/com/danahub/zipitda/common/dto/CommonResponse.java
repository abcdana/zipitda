package com.danahub.zipitda.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.ConstructorArgs;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private int code;       // 에러 코드
    private String message; // 메시지
    private T data;         // 응답 데이터

    // 성공 응답용 메서드
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(0, "Success", data);
    }

    // 에러 응답용 메서드
    public static <T> CommonResponse<T> error(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
