package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.ProductInputs;
import com.fawkes.api.Entities.ProductOutputs;

import java.time.LocalDateTime;

public record ActivityDTO(
        Long id,
        String type,           // "ENTRADA" ou "SAIDA"
        String productName,
        Integer quantity,
        LocalDateTime date
) {
    public static ActivityDTO fromInput(ProductInputs input) {
        return new ActivityDTO(
                input.getId(),
                "ENTRADA",
                input.getProduct().getProductName(),
                input.getQuantity(),
                input.getInputDate()
        );
    }

    public static ActivityDTO fromOutput(ProductOutputs output) {
        return new ActivityDTO(
                output.getId(),
                "SAIDA",
                output.getProduct().getProductName(),
                output.getQuantity(),
                output.getOutputDate()
        );
    }
}