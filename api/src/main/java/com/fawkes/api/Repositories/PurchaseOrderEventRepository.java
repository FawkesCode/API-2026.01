package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.PurchaseOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderEventRepository extends JpaRepository<PurchaseOrderEvent, Long> {

    List<PurchaseOrderEvent> findByPurchaseOrderIdOrderByOccurredAtAsc(Long orderId);

    @Query("SELECT DISTINCT e FROM PurchaseOrderEvent e " +
           "JOIN FETCH e.purchaseOrder p " +
           "LEFT JOIN FETCH p.items i " +
           "LEFT JOIN FETCH i.product " +
           "ORDER BY e.occurredAt DESC")
    List<PurchaseOrderEvent> findAllByOrderByOccurredAtDesc();
}
