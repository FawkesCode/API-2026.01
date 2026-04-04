package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;

/*
 Representa uma entrada do histórico de movimentações de estoque.
 Espelha ProductInputs / ProductOutputs do back-end.

 ATENÇÃO: ajuste os nomes dos campos conforme o JSON real retornado
 pelo endpoint de histórico quando ele for implementado no back.
 */

public class HistoryLog {

    public enum MovementType { ENTRADA, SAIDA }

    private Long id;
    private MovementType type;
    private String productName;
    private Integer quantity;
    private String date;
    private String responsible;

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

    public static HistoryLog fromJson(JsonNode node, MovementType type) {
        Long id = node.path("id").asLong();
        String productName = node.path("product").path("productName")
                .asText(node.path("productName").asText("-"));
        Integer quantity = node.path("quantity").asInt(0);
        String rawDate = node.path("movementDate")
                .asText(node.path("date").asText(""));
        String date = formatDate(rawDate);
        String responsible = node.path("user").path("userName")
                .asText(node.path("responsible").asText("-"));
        return new HistoryLog(id, type, productName, quantity, date, responsible);
    }

    private static String formatDate(String iso) {
        if (iso == null || iso.isBlank()) return "-";
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(
                    iso.length() > 19 ? iso.substring(0, 19) : iso);
            String[] months = {"Jan.", "Fev.", "Mar.", "Abr.", "Mai.", "Jun.",
                    "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."};
            return dt.getDayOfMonth() + " de " + months[dt.getMonthValue() - 1]
                    + " " + dt.getYear() + " às "
                    + String.format("%02d:%02d", dt.getHour(), dt.getMinute());
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