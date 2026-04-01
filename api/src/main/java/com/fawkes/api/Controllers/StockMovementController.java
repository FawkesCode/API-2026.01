package com.fawkes.api.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fawkes.api.Entities.ProductInputs;
import com.fawkes.api.Entities.ProductOutputs;
import com.fawkes.api.Services.StockMovementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/input")
    public ResponseEntity<ProductInputs> registerInput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        ProductInputs input = stockMovementService.registerInput(stockId, productId, quantity);
        return ResponseEntity.ok(input);
    }

    @PostMapping("/output")
    public ResponseEntity<ProductOutputs> registerOutput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long orderId) {

        ProductOutputs output = stockMovementService.registerOutput(stockId, productId, quantity, null);
        return ResponseEntity.ok(output);
    }
}