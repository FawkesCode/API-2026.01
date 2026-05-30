package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String requesterName;
    private String product;
    private String sector;
    private String paymentMethod;
    private int quantity;
    private double totalValue;
    private String status;
    private String description;
    private List<RequestSupplier> suppliersList;
    private List<RequestItem> itemsList;
    private int id;
    private String invoiceNumber;
    private String expectedDeliveryDate; // ISO string, ex: "2026-05-01T00:00:00"

    public Order(String requesterName, String product, String sector, String paymentMethod,
                 int quantity, double totalValue, String status, String description,
                 List<RequestSupplier> suppliersList, List<RequestItem> itemsList, int id) {
        this.id = id;
        this.requesterName = requesterName;
        this.product = product;
        this.sector = sector;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.status = status;
        this.description = description;
        this.suppliersList = suppliersList;
        this.itemsList = itemsList;
    }

    public String getStatusLabel() {
        return switch (status != null ? status : "") {
            case "draft"     -> "Rascunho";
            case "pending"   -> "Pendente";
            case "confirmed" -> "Aprovado";
            case "shipped"   -> "Em trânsito";
            case "received"  -> "Recebido";
            case "cancelled" -> "Cancelado/Recusado";
            case "problem"  -> "Problema no recebimento";
            case "returned" -> "Devolvido";
            default          -> status;
        };
    }

    public static Order fromJson(JsonNode node) {
        double total = node.has("totalValue") && !node.get("totalValue").isNull()
                ? node.get("totalValue").asDouble() : 0.0;
        String status = node.has("status") && !node.get("status").isNull()
                ? node.get("status").asText() : "UNKNOWN";

        int orderId = node.path("id").asInt();

        List<RequestSupplier> suppliersList = new ArrayList<>();
        List<RequestItem> requestItemsList = new ArrayList<>();

        String requester = "N/A";
        String sec = "Setor N/A";
        String pay = "N/A";
        String prod = "Produto N/A";
        int qty = 0;

        if (node.has("createdBy") && !node.get("createdBy").isNull()) {
            JsonNode createdBy = node.get("createdBy");
            if (createdBy.has("userName") && !createdBy.get("userName").isNull()) {
                requester = createdBy.get("userName").asText();
            }
            if (createdBy.has("departments") && !createdBy.get("departments").isNull()) {
                JsonNode dept = createdBy.get("departments");
                if (dept.has("departamentName") && !dept.get("departamentName").isNull()) {
                    sec = dept.get("departamentName").asText();
                }
            }
        }

        if (node.has("supplier") && !node.get("supplier").isNull()) {
            JsonNode supplier = node.get("supplier");
            if (supplier.has("paymentMethods") && supplier.get("paymentMethods").isArray()
                    && supplier.get("paymentMethods").size() > 0) {
                pay = supplier.get("paymentMethods").get(0).asText();
            }
        }

        if (node.has("supplier") && !node.get("supplier").isNull()) {
            JsonNode supplierNode = node.get("supplier");
            int id = supplierNode.has("id") ? supplierNode.get("id").asInt() : 0;
            String name = supplierNode.has("supplierName")
                    ? supplierNode.get("supplierName").asText() : "N/A";
            ArrayList<String> paymentMethods = new ArrayList<>();
            if (supplierNode.has("paymentMethods") && supplierNode.get("paymentMethods").isArray()) {
                for (JsonNode method : supplierNode.get("paymentMethods")) {
                    paymentMethods.add(method.asText());
                }
            }
            suppliersList.add(new RequestSupplier(id, paymentMethods, name));
        }

        JsonNode itemsNode = node.path("items");
        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                int id = item.get("id").asInt();
                int quantity = item.get("quantity").asInt();
                double totalPrice = item.get("totalPrice").asDouble();
                double unitPrice = item.get("unitPrice").asDouble();
                JsonNode productNode = item.get("product");
                int productId = productNode.get("id").asInt();
                String productName = productNode.get("productName").asText();
                RequestProduct product = new RequestProduct(productId, productName,
                        suppliersList.getFirst().getSupplierId());
                requestItemsList.add(new RequestItem(id, product, quantity, totalPrice, unitPrice));
            }
        }

        if (node.has("items") && node.get("items").isArray() && node.get("items").size() > 0) {
            JsonNode firstItem = node.get("items").get(0);
            if (firstItem.has("product") && !firstItem.get("product").isNull()) {
                JsonNode productNode = firstItem.get("product");
                if (productNode.has("productName") && !productNode.get("productName").isNull()) {
                    prod = productNode.get("productName").asText();
                    if (node.get("items").size() > 1) {
                        prod += " (+" + (node.get("items").size() - 1) + " itens)";
                    }
                }
            }
            for (JsonNode item : node.get("items")) {
                if (item.has("quantity") && !item.get("quantity").isNull()) {
                    qty += item.get("quantity").asInt();
                }
            }
        }

        String desc = node.path("notes").asText("Sem descição proporcionada pelo solicitante.");

        // Extrai número da nota fiscal
        String invoiceNumber = null;
        if (node.has("orderNote") && !node.get("orderNote").isNull()) {
            invoiceNumber = node.path("orderNote").path("numberNote").asText(null);
        }

        Order ord = new Order(requester, prod, sec, pay, qty, total, status, desc,
                suppliersList, requestItemsList, orderId);
        String expectedDeliveryDate = node.path("expectedDeliveryDate").asText(null);
        if ("null".equals(expectedDeliveryDate)) expectedDeliveryDate = null;
        ord.setInvoiceNumber(invoiceNumber);
        ord.setExpectedDeliveryDate(expectedDeliveryDate);
        return ord;
    }

    public String getEffectiveStatus() {
        if (!"shipped".equals(status)) return status;
        if (expectedDeliveryDate == null) return "shipped";
        try {
            java.time.LocalDateTime delivery = java.time.LocalDateTime.parse(expectedDeliveryDate);
            return delivery.isBefore(java.time.LocalDateTime.now()) ? "overdue" : "shipped";
        } catch (Exception e) {
            return "shipped";
        }
    }


    // --- Getters ---
    public int getId() { return id; }
    public String getRequesterName() { return requesterName; }
    public String getProduct() { return product; }
    public String getSector() { return sector; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getQuantity() { return quantity; }
    public double getTotalValue() { return totalValue; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public List<RequestSupplier> getSuppliersList() { return suppliersList; }
    public List<RequestItem> getItemsList() { return itemsList; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(String v) { this.expectedDeliveryDate = v; }
}