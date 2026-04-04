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

    public Supplier() {}

    public Supplier(Long id, String name, String cnpj, String paymentMethod) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.paymentMethod = paymentMethod;
    }

    public static Supplier fromJson(JsonNode node) {
        Long id = node.path("id").asLong();
        String name = node.path("nomeFornecedor").asText("-");
        String cnpj = node.path("cnpjFornecedor").asText("-");
        String payment = node.path("meioPagamento").asText("-");
        return new Supplier(id, name, cnpj, payment);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}