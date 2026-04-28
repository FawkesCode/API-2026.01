package com.fawkes.api.DTOs.Request;

import com.fawkes.api.Entities.TicketEnum;
import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private TicketEnum status;

    public TicketEnum getStatus() { return status; }
    public void setStatus(TicketEnum status) { this.status = status; }
}