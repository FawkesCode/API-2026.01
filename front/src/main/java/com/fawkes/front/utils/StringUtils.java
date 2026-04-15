package com.fawkes.front.utils;

public class StringUtils {

    public static String roleTranslation(String role) {
        return switch (role) {
            case "DIRECTOR" -> "Diretor";
            case "OPERATIONAL" -> "Operacional";
            case "MANAGER" -> "Gerente";
            default -> role;
        };
    }
}
