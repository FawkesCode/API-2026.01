package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.CheaperProductDTO;
import com.fawkes.api.Services.ProductComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products/comparison")
@RequiredArgsConstructor
public class ProductComparisonController {

    private final ProductComparisonService service;

    @GetMapping("/cheapest")
    public ResponseEntity<List<CheaperProductDTO>>getCheapest() {
        return ResponseEntity.ok(service.getMostCheaperByProduct());
    }
}