package com.fawkes.api.Models.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import java.lang.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBproductOutput")
public class ProductOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outputDate", nullable = false)
    private LocalDateTime outputDate;

    // ON DELETE SET NULL → nullable = true (padrão) + orphanRemoval = false
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_FK", nullable = true)
    private Orders order;

}
