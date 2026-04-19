package com.fawkes.api.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_company_stock")
public class CompanyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id_fk", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_fk", nullable = false)
    private Products product;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity = 0;

    @Column(name = "min_stock_quantity")
    private Integer minStockQuantity;

    @Column(name = "max_stock_quantity")
    private Integer maxStockQuantity;

    @Column(name = "last_input_date")
    private java.time.LocalDateTime lastInputDate;

    @Column(name = "last_output_date")
    private java.time.LocalDateTime lastOutputDate;
}