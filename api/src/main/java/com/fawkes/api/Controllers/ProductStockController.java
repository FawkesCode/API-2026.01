package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.ProductStock;
import com.fawkes.api.Services.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-stock")
@RequiredArgsConstructor
public class ProductStockController {

    private final ProductStockService productStockService;

    @PatchMapping("/{productId}/limits")
    public ResponseEntity<ProductStock> updateLimits(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {

        Integer min = body.get("minStockQuantity");
        Integer max = body.get("maxStockQuantity");

        return ResponseEntity.ok(productStockService.updateLimits(productId, min, max));
    }

    @GetMapping("/criticos")
    public ResponseEntity<List<ProductStock>> getCriticos() {
        return ResponseEntity.ok(productStockService.getCriticos());
    }
}