package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.StockItemDTO;
import com.fawkes.api.Entities.Stock;
import com.fawkes.api.Repositories.StockRepository;
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
    private final StockRepository stockRepository;

    @GetMapping
    public ResponseEntity<List<StockItemDTO>> listStock() {
        return ResponseEntity.ok(stockService.listAllStock());
    }

    // Retorna o estoque principal para o front descobrir o ID real
    @GetMapping("/first")
    public ResponseEntity<Stock> getFirstStock() {
        return stockRepository.findByStockName("Estoque Principal")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}