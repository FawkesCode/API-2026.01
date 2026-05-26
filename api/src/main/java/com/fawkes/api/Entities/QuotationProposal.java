package com.fawkes.api.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_quotation_proposal")
public class QuotationProposal {

    public enum Status {
        under_review, selected, not_selected
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id_fk", nullable = false)
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id_fk", nullable = false)
    private Suppliers supplier;

    @Column(name = "delivery_days", nullable = false)
    private Integer deliveryDays;

    @Column(name = "payment_conditions", length = 255)
    private String paymentConditions;

    @Column(name = "total_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.under_review;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationProposalItem> items;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
