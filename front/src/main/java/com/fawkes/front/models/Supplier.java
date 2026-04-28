package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;

/*
 Espelha a entidade Suppliers do back-end (TBsupplier).
 Campos: id, nomeFornecedor, cnpjFornecedor, meioPagamento.
 */
public class Supplier {

    private Long id;
    private String name;
    private String cnpj;
    private String paymentMethod;
    private String active;

    public Supplier() {}

    public Supplier(Long id, String name, String cnpj, String paymentMethod, String active) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.paymentMethod = paymentMethod;
        this.setActive(active);
    }

    public static Supplier fromJson(JsonNode node) {
        Long id = node.path("id").asLong();
        String name = node.path("supplierName").asText("-");
        String cnpj = node.path("cnpj").asText("-");
//        String payment = node.path("paymentMethods").asText("-");
        boolean active = node.path("isActive").asBoolean(true);
        String status = active ? "Ativo" : "Inativo";
        JsonNode paymentNode = node.path("paymentMethods");
        String payment = "-";

        if (paymentNode.isArray() && paymentNode.size() > 0) {
            payment = paymentNode.get(0).asText("-");
        }

        return new Supplier(id, name, cnpj, payment, status);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}