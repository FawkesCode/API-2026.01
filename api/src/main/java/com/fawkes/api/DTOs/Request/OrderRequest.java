package com.fawkes.api.DTOs.Request;

import com.fawkes.api.Entities.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "ID do departamento é obrigatório")
    private Long departmentId;

    @NotNull(message = "ID do fornecedor é obrigatório")
    private Long supplierId;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private PaymentMethod formaPagamento;

    private String descricao;

    @NotNull(message = "Quantidade de itens é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidadeItens;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private java.math.BigDecimal value;

    private Long orderNoteId;

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public PaymentMethod getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(PaymentMethod formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getQuantidadeItens() { return quantidadeItens; }
    public void setQuantidadeItens(Integer quantidadeItens) { this.quantidadeItens = quantidadeItens; }

    public java.math.BigDecimal getValue() { return value; }
    public void setValue(java.math.BigDecimal value) { this.value = value; }

    public Long getOrderNoteId() { return orderNoteId; }
    public void setOrderNoteId(Long orderNoteId) { this.orderNoteId = orderNoteId; }
}