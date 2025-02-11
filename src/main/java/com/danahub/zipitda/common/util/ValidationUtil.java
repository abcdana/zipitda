package com.danahub.zipitda.common.util;

public class ValidationUtil {

    /**
     * 전화번호 형식: "010-1234-5678", "01012345678"
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;

        return phoneNumber.matches("^010-?\\d{4}-?\\d{4}$");
    }

}
