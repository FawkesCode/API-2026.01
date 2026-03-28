package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import lombok.*;
import java.lang.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fornecedor")
public class Supplier {

    public enum MeioPagamento {
        PIX, CREDITO, DEBITO, BOLETO
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fornecedor", length = 255)
    private String nomeFornecedor;

    @Column(name = "cnpj_fornecedor", unique = true, length = 20)
    private String cnpjFornecedor;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_pagamento", columnDefinition = "ENUM('PIX','CREDITO','DEBITO','BOLETO') DEFAULT 'PIX'")
    private MeioPagamento meioPagamento = MeioPagamento.PIX;

}
