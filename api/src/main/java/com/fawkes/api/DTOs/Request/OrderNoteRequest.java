/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.DTOs.Request;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author vitor
 */
@Data
public class OrderNoteRequest {
    private String numbeNote;
    private String serie;
    private LocalDate orderNoteDate;
}
