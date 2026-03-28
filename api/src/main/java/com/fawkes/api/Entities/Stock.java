package com.fawkes.api.Entities;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.lang.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBstock",
       uniqueConstraints = {})
public class Stock {

    public enum measurementUnit {
        METROS, CAIXAS, LITROS, KILOGRAMAS, OUTROS, NAO_DEFINIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qtd_produtos", nullable = false)
    private Integer qtdProdutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida",
            columnDefinition = "ENUM('METROS','CAIXAS','LITROS','KILOGRAMAS','OUTROS','NÃO DEFINIDO') DEFAULT 'NÃO DEFINIDO'")
    private measurementUnit unidadeMedida = measurementUnit.NAO_DEFINIDO;

    @Column(name = "lastUpdate",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products produto;


    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
}
