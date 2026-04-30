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

    public Order(String requesterName, String product, String sector, String paymentMethod, int quantity, double totalValue, String status) {
        this.requesterName = requesterName;
        this.product = product;
        this.sector = sector;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.status = status;
    }

    public static Order fromJson(JsonNode node) {
        // 1. Valores que estão na raiz do JSON
        double total = node.has("totalValue") && !node.get("totalValue").isNull() ? node.get("totalValue").asDouble() : 0.0;
        String status = node.has("status") && !node.get("status").isNull() ? node.get("status").asText() : "UNKNOWN";

        // 2. Valores padrão para os dados aninhados
        String requester = "N/A";
        String sec = "Setor N/A";
        String pay = "N/A";
        String prod = "Produto N/A";
        int qty = 0;

        // 3. Extraindo o Solicitante (userName) e Setor (departamentName)
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

        // 4. Extraindo o Método de Pagamento do Fornecedor (pegando o primeiro da lista)
        if (node.has("supplier") && !node.get("supplier").isNull()) {
            JsonNode supplier = node.get("supplier");
            if (supplier.has("paymentMethods") && supplier.get("paymentMethods").isArray() && supplier.get("paymentMethods").size() > 0) {
                pay = supplier.get("paymentMethods").get(0).asText();
            }
        }

        // 5. Extraindo Produtos e somando as Quantidades da lista de itens
        if (node.has("items") && node.get("items").isArray() && node.get("items").size() > 0) {
            // Pega o nome do primeiro produto da lista para exibir na tabela
            JsonNode firstItem = node.get("items").get(0);
            if (firstItem.has("product") && !firstItem.get("product").isNull()) {
                JsonNode productNode = firstItem.get("product");
                if (productNode.has("productName") && !productNode.get("productName").isNull()) {
                    prod = productNode.get("productName").asText();

                    // Se houver mais de um item no pedido, adiciona um indicativo visual
                    if (node.get("items").size() > 1) {
                        prod += " (+" + (node.get("items").size() - 1) + " itens)";
                    }
                }
            }

            // Soma a quantidade de todos os itens do pedido
            for (JsonNode item : node.get("items")) {
                if (item.has("quantity") && !item.get("quantity").isNull()) {
                    qty += item.get("quantity").asInt();
                }
            }
        }

        return new Order(requester, prod, sec, pay, qty, total, status);
    }

    // --- Getters ---
    public String getRequesterName() { return requesterName; }
    public String getProduct() { return product; }
    public String getSector() { return sector; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getQuantity() { return quantity; }
    public double getTotalValue() { return totalValue; }
    public String getStatus() { return status; }
}