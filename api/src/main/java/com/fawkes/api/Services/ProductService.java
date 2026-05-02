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

    @Transactional
    public void delete(Long id) {
        productsRepository.deleteById(id);
    }

    @Transactional
    public Products update(Long id, ProductRequest request) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getProductType() != null) {
            product.setProductType(request.getProductType());
        }
        if (request.getMeasurementUnit() != null) {
            try {
                product.setMeasurementUnit(Products.MeasurementUnit.valueOf(request.getMeasurementUnit()));
            } catch (IllegalArgumentException e) {
                product.setMeasurementUnit(Products.MeasurementUnit.NAO_DEFINIDO);
            }
        }
        if (request.getUnitValue() != null) {
            product.setUnitValue(request.getUnitValue());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getSupplierId() != null) {
            Suppliers supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            product.setSuppliers(supplier);
        }

        return productsRepository.save(product);
    }
}
