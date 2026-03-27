package com.fawkes.api.service;

import com.fawkes.api.Models.entity.Product;
import com.fawkes.api.Models.entity.Stock;
import com.fawkes.api.Repository.StockRepository;
import com.fawkes.api.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    public StockService(StockRepository stockRepository, ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    public Stock cadastrar(Integer produtoId, Integer qtdInicial, Integer qtdMinima, Stock.MeasurementUnit unidade) {
        // produto precisa existir
        Product produto = productRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));

        // produto não pode ter estoque duplicado
        if (stockRepository.existsByProdutoId(produtoId)) {
            throw new RuntimeException("Já existe estoque cadastrado para este produto.");
        }

        Stock stock = new Stock();
        stock.setProduto(produto);
        stock.setQtdProdutos(qtdInicial);
        stock.setQtdMinima(qtdMinima);
        stock.setUnidadeMedida(unidade != null ? unidade : Stock.MeasurementUnit.NAO_DEFINIDO);

        return stockRepository.save(stock);
    }

    public List<Stock> listarTodos() {
        return stockRepository.findAll();
    }

    public Stock buscarPorId(Integer id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado: " + id));
    }

    public Stock buscarPorProduto(Integer produtoId) {
        return stockRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto: " + produtoId));
    }

    public Stock atualizar(Integer id, Integer qtdMinima, Stock.MeasurementUnit unidade) {
        Stock stock = buscarPorId(id);
        if (qtdMinima != null) stock.setQtdMinima(qtdMinima);
        if (unidade != null) stock.setUnidadeMedida(unidade);
        return stockRepository.save(stock);
    }

    public void deletar(Integer id) {
        Stock stock = buscarPorId(id);
        stockRepository.delete(stock);
    }
}