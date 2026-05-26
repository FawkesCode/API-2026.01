package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    Optional<Quotation> findByPurchaseOrderId(Long purchaseOrderId);
    List<Quotation> findByStatus(Quotation.Status status);
    boolean existsByPurchaseOrderId(Long purchaseOrderId);
}
