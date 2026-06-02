package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.PurchaseOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderEventRepository extends JpaRepository<PurchaseOrderEvent, Long> {

    List<PurchaseOrderEvent> findByPurchaseOrderIdOrderByOccurredAtAsc(Long orderId);
}
