package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.ProductInputs;
import com.fawkes.api.Entities.ProductOutputs;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public record ActivityDTO(
        Long id,
        String type,
        String productName,
        Integer quantity,
        LocalDateTime date,
        String responsible
) {
    public static ActivityDTO fromInput(ProductInputs input) {
        try {
            if (input == null) {
                log.warn("ProductInputs é null!");
                throw new IllegalArgumentException("ProductInputs não pode ser null");
            }
            
            String productName = (input.getProduct() != null) ? input.getProduct().getProductName() : "Produto Indisponível";
            
            if (input.getProduct() == null) {
                log.warn("ProductInputs {} não tem product associado", input.getId());
            }
            
            return new ActivityDTO(
                    input.getId(),
                    "ENTRADA",
                    productName,
                    input.getQuantity(),
                    input.getInputDate(),
                    input.getResponsible()
            );
        } catch (Exception e) {
            log.error("Erro ao converter ProductInputs para ActivityDTO", e);
            throw e;
        }
    }

    public static ActivityDTO fromOutput(ProductOutputs output) {
        try {
            if (output == null) {
                log.warn("ProductOutputs é null!");
                throw new IllegalArgumentException("ProductOutputs não pode ser null");
            }
            
            String productName = (output.getProduct() != null) ? output.getProduct().getProductName() : "Produto Indisponível";
            
            if (output.getProduct() == null) {
                log.warn("ProductOutputs {} não tem product associado", output.getId());
            }
            
            return new ActivityDTO(
                    output.getId(),
                    "SAIDA",
                    productName,
                    output.getQuantity(),
                    output.getOutputDate(),
                    output.getResponsible()
            );
        } catch (Exception e) {
            log.error("Erro ao converter ProductOutputs para ActivityDTO", e);
            throw e;
        }
    }
}