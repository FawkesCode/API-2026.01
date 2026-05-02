package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;

public class Order {
    private String requesterName;
    private String product;
    private String sector;
    private String paymentMethod;
    private int quantity;
    private double totalValue;
    private String status;

    public Order(String requesterName, String product, String sector,
                 String paymentMethod, int quantity, double totalValue, String status) {
        this.requesterName = requesterName;
        this.product       = product;
        this.sector        = sector;
        this.paymentMethod = paymentMethod;
        this.quantity      = quantity;
        this.totalValue    = totalValue;
        this.status        = status;
    }

    public static Order fromJson(JsonNode node) {
        double total  = node.path("totalValue").asDouble(0.0);
        String status = node.path("status").asText("UNKNOWN");

        String requester = "N/A";
        String sec       = "N/A";
        String pay       = "N/A";
        String prod      = "Produto N/A";
        int    qty       = 0;

        // Solicitante + Setor
        JsonNode createdBy = node.path("createdBy");
        if (!createdBy.isMissingNode() && !createdBy.isNull()) {
            requester = createdBy.path("userName").asText("N/A");
            JsonNode dept = createdBy.path("departments");
            if (!dept.isMissingNode() && !dept.isNull()) {
                sec = dept.path("departamentName").asText("N/A");
            }
        }

        JsonNode supplier = node.path("supplier");
        if (!supplier.isMissingNode() && !supplier.isNull()) {
            JsonNode pm = supplier.path("paymentMethods");
            if (pm.isArray() && pm.size() > 0) {
                pay = pm.get(0).asText("N/A");
            }
        }

        // Produto e quantidade total
        JsonNode items = node.path("items");
        if (items.isArray() && items.size() > 0) {
            JsonNode firstItem = items.get(0);
            JsonNode productNode = firstItem.path("product");
            if (!productNode.isMissingNode() && !productNode.isNull()) {
                prod = productNode.path("productName").asText("Produto N/A");
                if (items.size() > 1) {
                    prod += " (+" + (items.size() - 1) + " itens)";
                }
            }
            for (JsonNode item : items) {
                qty += item.path("quantity").asInt(0);
            }
        }

        return new Order(requester, prod, sec, pay, qty, total, status);
    }

    // Tradução de status para PT-BR
    public String getStatusPtBr() {
        if (status == null) return "Desconhecido";
        return switch (status.toUpperCase()) {
            case "DRAFT"     -> "Rascunho";
            case "PENDING"   -> "Pendente";
            case "CONFIRMED" -> "Confirmado";
            case "SHIPPED"   -> "Em Transporte";
            case "RECEIVED"  -> "Recebido";
            case "CANCELLED" -> "Cancelado";
            default          -> status;
        };
    }

    // Getters
    public String getRequesterName() { return requesterName; }
    public String getProduct()       { return product; }
    public String getSector()        { return sector; }
    public String getPaymentMethod() { return paymentMethod; }
    public int    getQuantity()      { return quantity; }
    public double getTotalValue()    { return totalValue; }
    public String getStatus()        { return status; }
}