package com.fawkes.api.Services;

import com.fawkes.api.Entities.Suppliers;
import com.fawkes.api.Repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Suppliers> listAll() {
        return supplierRepository.findAll();
    }

    public Suppliers findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    public Suppliers create(Suppliers supplier) {
        return supplierRepository.save(supplier);
    }

    public Suppliers update(Long id, Suppliers updatedData) {
        Suppliers supplier = findById(id);
        if (updatedData.getSupplierName() != null) {
            supplier.setSupplierName(updatedData.getSupplierName());
        }
        if (updatedData.getCnpj() != null) {
            supplier.setCnpj(updatedData.getCnpj());
        }
        if (updatedData.getPaymentMethods() != null) {
            supplier.setPaymentMethods(updatedData.getPaymentMethods());
        }
        if (updatedData.getIsActive() != null) {
            supplier.setIsActive(updatedData.getIsActive());
        }
        return supplierRepository.save(supplier);
    }

    public Suppliers toggleStatus(Long id) {
        Suppliers supplier = findById(id);
        supplier.setIsActive(!Boolean.TRUE.equals(supplier.getIsActive()));
        return supplierRepository.save(supplier);
    }
}