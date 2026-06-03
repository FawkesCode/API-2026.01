package com.fawkes.api.Entities;

import java.math.BigDecimal;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "unit_value",  columnDefinition="Decimal(10,2) default '0.00'")
    @ColumnDefault("0.00")
    private BigDecimal unitValue = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id_fk", nullable = false)
    private Suppliers suppliers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id_fk", nullable = false)
    private Stock stock;
}