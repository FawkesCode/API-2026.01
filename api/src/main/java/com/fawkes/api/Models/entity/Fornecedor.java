package com.fawkes.api.Models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {

    public enum MeioPagamento {
        PIX, CREDITO, DEBITO, BOLETO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_fornecedor", length = 255)
    private String nomeFornecedor;

    @Column(name = "cnpj_fornecedor", unique = true, length = 20)
    private String cnpjFornecedor;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_pagamento", columnDefinition = "ENUM('PIX','CREDITO','DEBITO','BOLETO') DEFAULT 'PIX'")
    private MeioPagamento meioPagamento = MeioPagamento.PIX;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }

    public String getCnpjFornecedor() { return cnpjFornecedor; }
    public void setCnpjFornecedor(String cnpjFornecedor) { this.cnpjFornecedor = cnpjFornecedor; }

    public MeioPagamento getMeioPagamento() { return meioPagamento; }
    public void setMeioPagamento(MeioPagamento meioPagamento) { this.meioPagamento = meioPagamento; }
}
