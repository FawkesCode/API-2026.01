package com.fawkes.api.DTOs.Response;

import com.fawkes.api.Entities.PaymentMethod;
import com.fawkes.api.Entities.TicketEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;

    private Long userId;
    private String userName;

    private Long departmentId;
    private String departmentName;

    private Long supplierId;
    private String supplierName;

    private PaymentMethod formaPagamento;
    private String descricao;
    private Integer quantidadeItens;

    private Long orderNoteId;
    private String orderNoteNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime orderDate;

    private TicketEnum status;
    private BigDecimal value;

    public OrderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public PaymentMethod getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(PaymentMethod formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getQuantidadeItens() { return quantidadeItens; }
    public void setQuantidadeItens(Integer quantidadeItens) { this.quantidadeItens = quantidadeItens; }

    public Long getOrderNoteId() { return orderNoteId; }
    public void setOrderNoteId(Long orderNoteId) { this.orderNoteId = orderNoteId; }

    public String getOrderNoteNumber() { return orderNoteNumber; }
    public void setOrderNoteNumber(String orderNoteNumber) { this.orderNoteNumber = orderNoteNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public TicketEnum getStatus() { return status; }
    public void setStatus(TicketEnum status) { this.status = status; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}