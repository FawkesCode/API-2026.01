package com.fawkes.api.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_product_stock")
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_fk", nullable = false, unique = true)
    private Products product;

    @Column(name = "min_stock_quantity", nullable = false)
    private Integer minStockQuantity;

    @Column(name = "max_stock_quantity", nullable = false)
    private Integer maxStockQuantity;

    @Column(name = "current_stock_quantity", nullable = false)
    private Integer currentStockQuantity = 0;
}