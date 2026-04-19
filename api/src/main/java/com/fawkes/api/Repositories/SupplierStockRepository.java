package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.SupplierStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierStockRepository extends JpaRepository<SupplierStock, Long> {

    List<SupplierStock> findBySupplierId(Long supplierId);

    List<SupplierStock> findBySupplierIdAndIsActiveTrue(Long supplierId);

    Optional<SupplierStock> findBySupplierIdAndProductId(Long supplierId, Long productId);

    List<SupplierStock> findByProductId(Long productId);
}