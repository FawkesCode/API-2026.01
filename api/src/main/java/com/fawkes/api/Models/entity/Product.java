package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBproduct")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "productName", length = 255)
    private String productName;

    @Column(name = "productType", length = 255)
    private String productType;

    @Column(name = "unitValue", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String descripction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierIdFk", nullable = false)
    private Supplier supplier;

    
}
