package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.SupplierStock;
import com.fawkes.api.Services.SupplierStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-stock")
@RequiredArgsConstructor
public class SupplierStockController {

    private final SupplierStockService supplierStockService;

    @GetMapping
    public ResponseEntity<List<SupplierStock>> listAll() {
        return ResponseEntity.ok(supplierStockService.listAll());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SupplierStock>> listBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierStockService.listBySupplier(supplierId));
    }

    @GetMapping("/supplier/{supplierId}/active")
    public ResponseEntity<List<SupplierStock>> listActiveBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierStockService.listActiveBySupplier(supplierId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SupplierStock>> listByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(supplierStockService.listByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<SupplierStock> create(@RequestBody SupplierStock supplierStock) {
        return ResponseEntity.ok(supplierStockService.create(supplierStock));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SupplierStock> activate(@PathVariable Long id) {
        return ResponseEntity.ok(supplierStockService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SupplierStock> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(supplierStockService.deactivate(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierStockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}