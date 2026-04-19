package com.fawkes.api.Services;

import com.fawkes.api.Entities.SupplierStock;
import com.fawkes.api.Entities.Suppliers;
import com.fawkes.api.Entities.Products;
import com.fawkes.api.Repositories.SupplierStockRepository;
import com.fawkes.api.Repositories.SupplierRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierStockService {

    private final SupplierStockRepository supplierStockRepository;
    private final SupplierRepository supplierRepository;
    private final ProductsRepository productsRepository;

    public List<SupplierStock> listAll() {
        return supplierStockRepository.findAll();
    }

    public List<SupplierStock> listBySupplier(Long supplierId) {
        return supplierStockRepository.findBySupplierId(supplierId);
    }

    public List<SupplierStock> listActiveBySupplier(Long supplierId) {
        return supplierStockRepository.findBySupplierIdAndIsActiveTrue(supplierId);
    }

    public List<SupplierStock> listByProduct(Long productId) {
        return supplierStockRepository.findByProductId(productId);
    }

    public Optional<SupplierStock> getBySupplierAndProduct(Long supplierId, Long productId) {
        return supplierStockRepository.findBySupplierIdAndProductId(supplierId, productId);
    }

    @Transactional
    public SupplierStock create(SupplierStock supplierStock) {
        Suppliers supplier = supplierRepository.findById(supplierStock.getSupplier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Products product = productsRepository.findById(supplierStock.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<SupplierStock> existing = supplierStockRepository
                .findBySupplierIdAndProductId(supplier.getId(), product.getId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Product already registered for this supplier");
        }

        supplierStock.setSupplier(supplier);
        supplierStock.setProduct(product);
        return supplierStockRepository.save(supplierStock);
    }

    @Transactional
    public SupplierStock updateQuantity(Long supplierId, Long productId, Integer newQuantity) {
        SupplierStock supplierStock = supplierStockRepository
                .findBySupplierIdAndProductId(supplierId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier stock not found"));

        supplierStock.setAvailableQuantity(newQuantity);
        return supplierStockRepository.save(supplierStock);
    }

    @Transactional
    public SupplierStock activate(Long id) {
        SupplierStock supplierStock = supplierStockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier stock not found"));
        supplierStock.setIsActive(true);
        return supplierStockRepository.save(supplierStock);
    }

    @Transactional
    public SupplierStock deactivate(Long id) {
        SupplierStock supplierStock = supplierStockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier stock not found"));
        supplierStock.setIsActive(false);
        return supplierStockRepository.save(supplierStock);
    }

    public void delete(Long id) {
        supplierStockRepository.deleteById(id);
    }
}