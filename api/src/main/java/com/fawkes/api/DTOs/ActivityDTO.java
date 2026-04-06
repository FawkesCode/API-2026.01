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
        String productName = (input.getProduct() != null) ? input.getProduct().getProductName() : "Produto Indisponível";
        return new ActivityDTO(
                input.getId(),
                "ENTRADA",
                productName,
                input.getQuantity(),
                input.getInputDate()
        );
    }

    public static ActivityDTO fromOutput(ProductOutputs output) {
        String productName = (output.getProduct() != null) ? output.getProduct().getProductName() : "Produto Indisponível";
        return new ActivityDTO(
                output.getId(),
                "SAIDA",
                productName,
                output.getQuantity(),
                output.getOutputDate()
        );
    }
}