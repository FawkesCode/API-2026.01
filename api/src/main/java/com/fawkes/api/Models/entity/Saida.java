package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saidas")
public class Saida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_saida", nullable = false)
    private LocalDateTime dataSaida;

    // ON DELETE SET NULL → nullable = true (padrão) + orphanRemoval = false
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_FK", nullable = true)
    private Pedido pedido;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
}
