package com.fawkes.api.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_product_output")
public class ProductOutputs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id_fk", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_fk", nullable = false)
    private Products product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "output_date", nullable = false)
    private LocalDateTime outputDate;

    @Column(name = "responsible", length = 255)
    private String responsible;

    // nullable → ON DELETE SET NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id_fk", nullable = true)
    private Ticket order;

    @PrePersist
    protected void onCreate() {
        outputDate = LocalDateTime.now();
    }
}