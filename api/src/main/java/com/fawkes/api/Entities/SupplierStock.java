package com.fawkes.api.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_supplier_stock")
public class SupplierStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id_fk", nullable = false)
    private Suppliers supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_fk", nullable = false)
    private Products product;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @Column(name = "min_stock_quantity")
    private Integer minStockQuantity;

    @Column(name = "max_stock_quantity")
    private Integer maxStockQuantity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}