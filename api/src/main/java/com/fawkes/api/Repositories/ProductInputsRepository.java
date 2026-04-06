package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.ProductInputs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInputsRepository extends JpaRepository<ProductInputs, Long> {

    List<ProductInputs> findByProductId(Long productId);
    List<ProductInputs> findByStockId(Long stockId);

    // JOIN FETCH carrega product e stock junto numa única query SQL,
    // eliminando o problema N+1 que causava lentidão no histórico
    @Query("SELECT i FROM ProductInputs i LEFT JOIN FETCH i.product LEFT JOIN FETCH i.stock")
    List<ProductInputs> findAllWithProduct();
}