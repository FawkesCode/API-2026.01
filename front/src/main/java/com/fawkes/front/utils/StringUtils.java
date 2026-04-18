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

    public static String roleDescTranslation(String role) {
        return switch (role) {
            case "DIRECTOR" -> "Os diretores tem acesso total ao sistema, podendo gerenciar outros usuários, entradas e saídas de estoque e aprovações de pedidos.";
            case "OPERATIONAL" -> "Os operacionais tem acesso restrito ao sistema, podendo apenas visualizar o estoque e realizar novos pedidos.";
            case "MANAGER" -> "Os gerentes tem quase acesso total ao sistema, se diferenciando dos diretores apenas por não poderem aprovar novos pedidos.";

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
