package com.fawkes.front.utils;

import java.text.NumberFormat;
import java.util.Locale;

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

    public static String requestStatusTranslation(String status) {
        return switch (status != null ? status : "") {
            case "draft"     -> "Rascunho";
            case "pending"   -> "Sob Revisão";
            case "quoted"    -> "Em Cotação";
            case "confirmed" -> "Aprovado para Compra";
            case "shipped"   -> "Em Trânsito";
            case "received"  -> "Recebido";
            case "cancelled" -> "Negado para Compra";
            case "overdue"   -> "Em Atraso";
            case "problem"   -> "Problemas no Recebimento";
            case "returned"  -> "Devolvido";
            default          -> status != null ? status : "";
        };
    }

    public static String getStatusColor(String status) {
        return switch (status != null ? status : "") {
            case "draft"     -> "#6B7280";
            case "pending"   -> "#F59E0B";
            case "quoted"    -> "#3B82F6";
            case "confirmed" -> "#10B981";
            case "shipped"   -> "#8B5CF6";
            case "received"  -> "#059669";
            case "cancelled" -> "#EF4444";
            case "overdue"   -> "#DC2626";
            case "problem"   -> "#F97316";
            case "returned"  -> "#374151";
            default          -> "#6B7280";
        };
    }



    public static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
}
