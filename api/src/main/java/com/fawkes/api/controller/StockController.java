package com.fawkes.api.controller;

import com.fawkes.api.Models.entity.Stock;
import com.fawkes.api.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // POST /api/stock
    @PostMapping
    public ResponseEntity<Stock> cadastrar(@RequestBody Map<String, Object> body) {
        Integer produtoId    = (Integer) body.get("produtoId");
        Integer qtdInicial   = (Integer) body.get("qtdInicial");
        Integer qtdMinima    = (Integer) body.get("qtdMinima");
        String  unidadeStr   = (String)  body.get("unidadeMedida");

        Stock.MeasurementUnit unidade = unidadeStr != null
                ? Stock.MeasurementUnit.valueOf(unidadeStr)
                : Stock.MeasurementUnit.NAO_DEFINIDO;

        Stock stock = stockService.cadastrar(produtoId, qtdInicial, qtdMinima, unidade);
        return ResponseEntity.ok(stock);
    }

    // GET /api/stock
    @GetMapping
    public ResponseEntity<List<Stock>> listarTodos() {
        return ResponseEntity.ok(stockService.listarTodos());
    }

    // GET /api/stock/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Stock> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.buscarPorId(id));
    }

    // GET /api/stock/produto/{produtoId}
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<Stock> buscarPorProduto(@PathVariable Integer produtoId) {
        return ResponseEntity.ok(stockService.buscarPorProduto(produtoId));
    }

    // PUT /api/stock/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Stock> atualizar(@PathVariable Integer id,
                                           @RequestBody Map<String, Object> body) {
        Integer qtdMinima  = (Integer) body.get("qtdMinima");
        String  unidadeStr = (String)  body.get("unidadeMedida");

        Stock.MeasurementUnit unidade = unidadeStr != null
                ? Stock.MeasurementUnit.valueOf(unidadeStr)
                : null;

        return ResponseEntity.ok(stockService.atualizar(id, qtdMinima, unidade));
    }

    // DELETE /api/stock/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Integer id) {
        stockService.deletar(id);
        return ResponseEntity.ok("Estoque deletado com sucesso.");
    }
}