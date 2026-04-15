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

    public static String paymentTranslation(String payment) {
        return switch (payment) {
            case "PIX" -> "Pix";
            case "CREDITO" -> "Crédito";
            case "DEBITO" -> "Débito";
            case "BOLETO" -> "Boleto";
            default -> payment;
        };
    }

    public static String measureTranslation(String measure) {
        return switch (measure) {
            case "METROS" -> "Metros";
            case "CAIXAS" -> "Caixas";
            case "LITROS" -> "Litros";
            case "KILOGRAMAS" -> "Kilogramas";
            case "OUTROS" -> "Outros";
            case "NAO_DEFINIDO" -> "Não Definido";
            default -> measure;
        };
    }
}
