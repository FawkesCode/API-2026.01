package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque",
       indexes = @Index(columnList = "produto_id_FK"),
       uniqueConstraints = {})
public class Estoque {

    public enum UnidadeMedida {
        METROS, CAIXAS, LITROS, KILOGRAMAS, OUTROS, NAO_DEFINIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id_FK")
    private Produto produto;

    @Column(name = "qtd_produtos", nullable = false)
    private Integer qtdProdutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida",
            columnDefinition = "ENUM('METROS','CAIXAS','LITROS','KILOGRAMAS','OUTROS','NÃO DEFINIDO') DEFAULT 'NÃO DEFINIDO'")
    private UnidadeMedida unidadeMedida = UnidadeMedida.NAO_DEFINIDO;

    @Column(name = "ultima_atualizacao",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime ultimaAtualizacao;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        ultimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQtdProdutos() { return qtdProdutos; }
    public void setQtdProdutos(Integer qtdProdutos) { this.qtdProdutos = qtdProdutos; }

    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedida unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}
