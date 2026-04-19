package com.fawkes.api.Services;

import com.fawkes.api.Entities.CompanyStock;
import com.fawkes.api.Entities.Stock;
import com.fawkes.api.Entities.Products;
import com.fawkes.api.Repositories.CompanyStockRepository;
import com.fawkes.api.Repositories.StockRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyStockService {

    private final CompanyStockRepository companyStockRepository;
    private final StockRepository stockRepository;
    private final ProductsRepository productsRepository;

    public List<CompanyStock> listAll() {
        return companyStockRepository.findAll();
    }

    public List<CompanyStock> listByStock(Long stockId) {
        return companyStockRepository.findByStockId(stockId);
    }

    public Optional<CompanyStock> getByStockAndProduct(Long stockId, Long productId) {
        return companyStockRepository.findByStockIdAndProductId(stockId, productId);
    }

    public List<CompanyStock> listByProduct(Long productId) {
        return companyStockRepository.findByProductId(productId);
    }

    public List<CompanyStock> listLowStock() {
        return companyStockRepository.findByCurrentQuantityLessThanEqual(10);
    }

    @Transactional
    public CompanyStock registerInput(Long stockId, Long productId, Integer quantity) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        Optional<CompanyStock> existing = companyStockRepository
                .findByStockIdAndProductId(stockId, productId);

        CompanyStock companyStock;
        if (existing.isPresent()) {
            companyStock = existing.get();
            companyStock.setCurrentQuantity(companyStock.getCurrentQuantity() + quantity);
            companyStock.setLastInputDate(LocalDateTime.now());
        } else {
            companyStock = new CompanyStock();
            companyStock.setStock(stock);
            companyStock.setProduct(product);
            companyStock.setCurrentQuantity(quantity);
            companyStock.setLastInputDate(LocalDateTime.now());
        }

        return companyStockRepository.save(companyStock);
    }

    @Transactional
    public CompanyStock registerOutput(Long stockId, Long productId, Integer quantity) {
        CompanyStock companyStock = companyStockRepository
                .findByStockIdAndProductId(stockId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque da empresa não encontrado!"));

        if (companyStock.getCurrentQuantity() < quantity) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + companyStock.getCurrentQuantity());
        }

        companyStock.setCurrentQuantity(companyStock.getCurrentQuantity() - quantity);
        companyStock.setLastOutputDate(LocalDateTime.now());
        return companyStockRepository.save(companyStock);
    }

    @Transactional
    public CompanyStock updateMinMax(Long stockId, Long productId, Integer minQuantity, Integer maxQuantity) {
        CompanyStock companyStock = companyStockRepository
                .findByStockIdAndProductId(stockId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque da empresa não encontrado!"));

        companyStock.setMinStockQuantity(minQuantity);
        companyStock.setMaxStockQuantity(maxQuantity);
        return companyStockRepository.save(companyStock);
    }

    public void delete(Long id) {
        companyStockRepository.deleteById(id);
    }
}