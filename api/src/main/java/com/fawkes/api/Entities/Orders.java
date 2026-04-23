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
@Table(name = "TBorders")
public class Orders {
public enum Status {
        pendente, processando, concluido, cancelado
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_fk", nullable = false)
    private Users user;

    @Column(name = "created_at", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id_fk", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_note_id_fk")
    private OrderNote invoice;

<<<<<<< HEAD
=======
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
>>>>>>> 0aead16 (Fix: Refatoração para conexão com o aplicativo)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            columnDefinition = "ENUM('pendente','processando','concluido','cancelado') DEFAULT 'pendente'")
    private Status status = Status.pendente;

    @Column(name = "total_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (orderDate == null) orderDate = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
