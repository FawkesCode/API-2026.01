/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author vitor
 */
@Data
public class OrderNoteRequest {
    @NotBlank(message = "O número da nota não pode estar em branco!")
    private String numberNote;
    
    @NotBlank(message = "A série não pode estar em branco!")
    private String serie;
    
    @NotNull(message = "A data da nota fiscal não pode ser nula!")
    private LocalDate orderNoteDate;
}
