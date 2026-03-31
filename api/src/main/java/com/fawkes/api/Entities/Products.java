package com.fawkes.api.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_product")
public class Products {

    public enum MeasurementUnit {
        METROS, CAIXAS, LITROS, KILOGRAMAS, OUTROS, NAO_DEFINIDO
    }
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", length = 255, nullable = false)
    private String productName;

    @Column(name = "product_type", length = 255)
    private String productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit", nullable = false)
    private MeasurementUnit measurementUnit = MeasurementUnit.NAO_DEFINIDO;

    @Column(name = "unit_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id_fk", nullable = false)
    private Suppliers suppliers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id_fk", nullable = false)
    private Stock stock;
}