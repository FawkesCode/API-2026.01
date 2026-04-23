/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.DTOs.Request;

import com.fawkes.api.Entities.TicketEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author vitor
 */
@Data

public class TicketUpdateRequest {
    
    
    @NotNull(message = "O status não pode ser nulo!")
    private TicketEnum status;
    
}
