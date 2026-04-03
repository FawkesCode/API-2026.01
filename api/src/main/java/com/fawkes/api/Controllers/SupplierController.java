package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.Suppliers;
import com.fawkes.api.Services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<Suppliers>> listAll() {
        return ResponseEntity.ok(supplierService.listAll());
    }

    @PostMapping
    public ResponseEntity<Suppliers> create(@RequestBody Suppliers supplier) {
        return ResponseEntity.ok(supplierService.create(supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}