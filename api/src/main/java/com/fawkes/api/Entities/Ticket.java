package com.fawkes.api.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import java.lang.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBtickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private Users usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamentId", nullable = false)
    private Department departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId", nullable = false)
    private Suppliers fornecedor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod formaPagamento;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "quantidade_itens", nullable = false)
    private Integer quantidadeItens;

    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('pendente','processando','concluido','cancelado') DEFAULT 'pendente'")
    private TicketEnum status = TicketEnum.pendente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderNoteId")
    private OrderNote notaFiscal;

    @Column(name = "orderDate",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataPedido;

    @Column(name = "created_at", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (dataPedido == null) dataPedido = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}