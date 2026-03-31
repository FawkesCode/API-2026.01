package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.ProductInputs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInputsRepository extends JpaRepository<ProductInputs, Long> {
    List<ProductInputs> findByProductId(Long productId);
    List<ProductInputs> findByStockId(Long stockId);
}
