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

    public Suppliers create(Suppliers supplier) {
        return supplierRepository.save(supplier);
    }

    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }
}