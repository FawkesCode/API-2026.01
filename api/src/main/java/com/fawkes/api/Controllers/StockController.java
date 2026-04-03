package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.StockItemDTO;
import com.fawkes.api.Services.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockItemDTO>> listStock() {
        return ResponseEntity.ok(stockService.listAllStock());
    }
}