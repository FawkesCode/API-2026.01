/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.DTOs.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;


@Data
public class TicketRequest {
    @NotNull(message = "O ID do usuário não pode ser nulo!")
    private Long userId;

    @NotNull(message = "O ID do departamento não pode ser nulo!")
    private Long departmentId;

    @NotNull(message = "O valor não pode ser nulo!")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero!")
    private BigDecimal value;
    
    
}
