package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.ProductRequest;
import com.fawkes.api.Entities.*;
import com.fawkes.api.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductsRepository productsRepository;
    private final SupplierRepository supplierRepository;
    private final StockRepository stockRepository;

    public List<Products> listAll() {
        return productsRepository.findAll();
    }

    @Transactional
    public Products create(ProductRequest request) {
        Suppliers supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        Products.MeasurementUnit unit;
        try {
            unit = Products.MeasurementUnit.valueOf(request.getMeasurementUnit());
        } catch (IllegalArgumentException e) {
            unit = Products.MeasurementUnit.NAO_DEFINIDO;
        }

        Products product = new Products();
        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setMeasurementUnit(unit);
        product.setUnitValue(request.getUnitValue());
        product.setDescription(request.getDescription());
        product.setSuppliers(supplier);
        product.setStock(stock);

        return productsRepository.save(product);
    }

    public void delete(Long id) {
        productsRepository.deleteById(id);
    }
}
