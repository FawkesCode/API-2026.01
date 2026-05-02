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

    @PutMapping("/{id}")
    public ResponseEntity<Suppliers> update(@PathVariable Long id, @RequestBody Suppliers supplier) {
        return ResponseEntity.ok(supplierService.update(id, supplier));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Suppliers> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.toggleStatus(id));
    }
}