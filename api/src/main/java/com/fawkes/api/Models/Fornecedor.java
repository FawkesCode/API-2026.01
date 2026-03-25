package com.fawkes.api.Models;

import jakarta.persistence.*;

@Entity
@Table (name = "fornecedor")
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String cnpj;
    private MeioPagamento meioPagamento;

    public Fornecedor(){};

    public Fornecedor(int id, String nome, String cnpj, MeioPagamento meioPagamento){
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.meioPagamento = meioPagamento;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return this.cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public MeioPagamento getMeioPagamento() {
        return this.meioPagamento;
    }

    public void setMeioPagamento(MeioPagamento meioPagamento) {
        this.meioPagamento = meioPagamento;
    }
}
