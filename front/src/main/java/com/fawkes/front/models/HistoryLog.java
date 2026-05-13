package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Representa uma entrada do histórico unificado de movimentações.
 * Espelha o ActivityDTO do back-end.
 * Campos: id, type, productName, quantity, date.
 */
public class HistoryLog {

    public enum MovementType { ENTRADA, SAIDA, REVISAO, APROVACAO, NEGACAO, COMPRA, EM_TRANSITO, EM_ATRASO, RECEBIDO, PROBLEMA, NAO_RECEBIDO, DEVOLVIDO }

    private Long id;
    private MovementType type;
    private String productName;
    private Integer quantity;
    private String date;
    private String responsible; // reservado para uso futuro

    public HistoryLog() {}

    public HistoryLog(Long id, MovementType type, String productName,
                      Integer quantity, String date, String responsible) {
        this.id = id;
        this.type = type;
        this.productName = productName;
        this.quantity = quantity;
        this.date = date;
        this.responsible = responsible;
    }

    /**
     * Lê o JSON retornado pelo endpoint GET /api/stock/movements/activity.
     * Campos esperados: id, type ("ENTRADA"/"SAIDA"), productName, quantity, date.
     * O parâmetro movType é ignorado — o tipo vem do campo "type" do JSON.
     */
    public static HistoryLog fromJson(JsonNode node, MovementType movType) {
        Long id = node.path("id").asLong();

        // tipo vem do JSON (ActivityDTO.type), fallback para o parâmetro
        String typeStr = node.path("type").asText("");
        MovementType type = "SAIDA".equals(typeStr) ? MovementType.SAIDA
                : "ENTRADA".equals(typeStr) ? MovementType.ENTRADA
                : movType;

        // productName vem direto do ActivityDTO
        String productName = node.path("productName").asText(
                node.path("product").path("productName").asText("-"));

        Integer quantity = node.path("quantity").asInt(0);

        // data: ActivityDTO serializa LocalDateTime como array [ano,mes,dia,hora,min,seg,nano]
        // ou como string ISO — tratamos os dois casos
        String date = parseDate(node.path("date"));

        String responsible = node.path("responsible").asText("-");

        return new HistoryLog(id, type, productName, quantity, date, responsible);
    }

    /**
     * Jackson serializa LocalDateTime de duas formas dependendo da config:
     * - Array: [2026, 4, 3, 21, 30, 0, 0]
     * - String ISO: "2026-04-03T21:30:00"
     */
    private static String parseDate(JsonNode dateNode) {
        if (dateNode == null || dateNode.isMissingNode()) return "-";

        if (dateNode.isArray() && dateNode.size() >= 5) {
            int year  = dateNode.get(0).asInt();
            int month = dateNode.get(1).asInt();
            int day   = dateNode.get(2).asInt();
            int hour  = dateNode.get(3).asInt();
            int min   = dateNode.get(4).asInt();
            String[] months = {"Jan.", "Fev.", "Mar.", "Abr.", "Mai.", "Jun.",
                    "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."};
            return String.format("%02d de %s %d às %02d:%02d",
                    day, months[month - 1], year, hour, min);
        }

        // String ISO
        String iso = dateNode.asText("");
        if (iso.isBlank()) return "-";
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(
                    iso.length() > 19 ? iso.substring(0, 19) : iso);
            String[] months = {"Jan.", "Fev.", "Mar.", "Abr.", "Mai.", "Jun.",
                    "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."};
            return String.format("%02d de %s %d às %02d:%02d",
                    dt.getDayOfMonth(), months[dt.getMonthValue() - 1],
                    dt.getYear(), dt.getHour(), dt.getMinute());
        } catch (Exception e) {
            return iso;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }
}