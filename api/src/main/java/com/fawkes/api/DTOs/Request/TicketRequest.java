/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.DTOs.Request;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author vitor
 */

@Data
public class TicketRequest {
    private long userId;
    private long departmentId;
    private BigDecimal value;
    
    
}
