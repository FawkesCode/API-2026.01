package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Array;
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

    public Order(String requesterName, String product, String sector, String paymentMethod, int quantity, double totalValue, String status, String description, List<RequestSupplier> suppliersList, List<RequestItem> itemsList, int id) {
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

    public static Order fromJson(JsonNode node) {
        // 1. Valores que estão na raiz do JSON
        double total = node.has("totalValue") && !node.get("totalValue").isNull() ? node.get("totalValue").asDouble() : 0.0;
        String status = node.has("status") && !node.get("status").isNull() ? node.get("status").asText() : "UNKNOWN";

        int orderId = node.path("id").asInt();

        List<RequestSupplier> suppliersList = new ArrayList<>();
        List<RequestItem> requestItemsList = new ArrayList<>();
        // 2. Valores padrão para os dados aninhados
        String requester = "N/A";
        String sec = "Setor N/A";
        String pay = "N/A";
        String prod = "Produto N/A";
        int qty = 0;

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

                RequestProduct product = new RequestProduct(productId, productName);
                RequestItem requestItem = new RequestItem(id, product, quantity, totalPrice, unitPrice);

                requestItemsList.add(requestItem);
            }
        }

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

//        if (node.has("supplier") && node.get("supplier").isArray()) {
//            JsonNode supplierArray = node.get("supplier");
//
//            for (JsonNode supplierItem : supplierArray) {
//                int id = supplierItem.has("id") ? supplierItem.get("id").asInt() : 0;
//                String name = supplierItem.has("supplierName") ? supplierItem.get("supplierName").asText() : "N/A";
//
//                ArrayList<String> paymentMethods = new ArrayList<>();
//                JsonNode methodsNode = supplierItem.path("paymentMethods");
//
//                if (methodsNode.isArray()) {
//                    for (JsonNode method : methodsNode) {
//                        paymentMethods.add(method.asText());
//                    }
//                }
//
//                RequestSupplier sup = new RequestSupplier(id, paymentMethods, name);
//                suppliersList.add(sup);
//            }
//        }

        if (node.has("supplier") && !node.get("supplier").isNull()) {
            JsonNode supplierNode = node.get("supplier");

            int id = supplierNode.has("id") ? supplierNode.get("id").asInt() : 0;
            String name = supplierNode.has("supplierName") ? supplierNode.get("supplierName").asText() : "N/A";

            ArrayList<String> paymentMethods = new ArrayList<>();
            if (supplierNode.has("paymentMethods") && supplierNode.get("paymentMethods").isArray()) {
                for (JsonNode method : supplierNode.get("paymentMethods")) {
                    paymentMethods.add(method.asText());
                }
            }

            RequestSupplier sup = new RequestSupplier(id, paymentMethods, name);
            suppliersList.add(sup);
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
        String desc = node.path("notes").asText("Sem descição proporcionada pelo solicitante.");

        return new Order(requester, prod, sec, pay, qty, total, status, desc, suppliersList, requestItemsList,orderId);
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
    public List<RequestItem> getItemsList() { return itemsList; };
}