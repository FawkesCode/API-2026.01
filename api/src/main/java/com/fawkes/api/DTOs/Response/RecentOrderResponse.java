package com.fawkes.api.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderResponse {

    private Long id;
    private String type;               // "ORDER" | "PURCHASE_ORDER"
    private String requesterName;      // user.userName — campo "Solicitante"
    private String supplierName;       // só para PurchaseOrder — campo "Fornecedor"
    private String departmentName;
    private BigDecimal totalValue;
    private String status;             // valor interno: "confirmed", "pendente", etc.
    private String statusLabel;        // valor exibido: "Aprovado", "Pendente", "Em atraso", etc.
    private String statusColor;        // hex para o badge colorido
    private LocalDateTime createdAt;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private String criticalityLabel;   // "NORMAL" | "CRITICAL"
}