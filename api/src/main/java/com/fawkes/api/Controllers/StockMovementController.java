package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.ProductInputs;
import com.fawkes.api.Entities.ProductOutputs;
import com.fawkes.api.Services.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    // POST /api/stock/movements/input?stockId=1&productId=2&quantity=50 exemplos de "paginação"
    @PostMapping("/input")
    public ResponseEntity<ProductInputs> registerInput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        ProductInputs input = stockMovementService.registerInput(stockId, productId, quantity);
        return ResponseEntity.ok(input);
    }

    // POST /api/stock/movements/output?stockId=1&productId=2&quantity=10&orderId=5 exemplos de "paginação"
    @PostMapping("/output")
    public ResponseEntity<ProductOutputs> registerOutput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long orderId) {

        // order pode ser null — buscar na service se necessário
        ProductOutputs output = stockMovementService.registerOutput(stockId, productId, quantity, null);
        return ResponseEntity.ok(output);
    }
}