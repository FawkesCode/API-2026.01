package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orderNote")
public class OrderNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "noteNumber", unique = true, length = 45, columnDefinition = "CHAR(45)")
    private String numeroNota;

    @Column(name = "serie", length = 20)
    private String serie;

    @Column(name = "orderDate")
    private LocalDate dataEmissao;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumeroNota() { return numeroNota; }
    public void setNumeroNota(String numeroNota) { this.numeroNota = numeroNota; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
}
