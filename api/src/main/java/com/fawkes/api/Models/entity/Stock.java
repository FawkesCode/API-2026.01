package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock", indexes = @Index(columnList = "produto_id_FK"))
public class Stock {

    public enum MeasurementUnit {
        METROS, CAIXAS, LITROS, KILOGRAMAS, OUTROS, NAO_DEFINIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id_FK")
    private Product produto;

    @Column(name = "qtd_produtos", nullable = false)
    private Integer qtdProdutos;

    @Column(name = "qtd_minima", nullable = false)
    private Integer qtdMinima;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida",
            columnDefinition = "ENUM('METROS','CAIXAS','LITROS','KILOGRAMAS','OUTROS','NAO_DEFINIDO') DEFAULT 'NAO_DEFINIDO'")
    private MeasurementUnit unidadeMedida = MeasurementUnit.NAO_DEFINIDO;

    @Column(name = "lastUpdate",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdate;

    public Stock() {}

    public Stock(Integer id, Product produto, Integer qtdProdutos, Integer qtdMinima,
                 MeasurementUnit unidadeMedida, LocalDateTime lastUpdate) {
        this.id = id;
        this.produto = produto;
        this.qtdProdutos = qtdProdutos;
        this.qtdMinima = qtdMinima;
        this.unidadeMedida = unidadeMedida;
        this.lastUpdate = lastUpdate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Product getProduto() { return produto; }
    public void setProduto(Product produto) { this.produto = produto; }

    public Integer getQtdProdutos() { return qtdProdutos; }
    public void setQtdProdutos(Integer qtdProdutos) { this.qtdProdutos = qtdProdutos; }

    public Integer getQtdMinima() { return qtdMinima; }
    public void setQtdMinima(Integer qtdMinima) { this.qtdMinima = qtdMinima; }

    public MeasurementUnit getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(MeasurementUnit unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
}