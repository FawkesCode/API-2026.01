package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplierId(Long supplierId);

    List<PurchaseOrder> findByCreatedById(Long userId);

    List<PurchaseOrder> findByStatus(PurchaseOrder.Status status);

    Optional<PurchaseOrder> findByIdAndSupplierId(Long id, Long supplierId);
}