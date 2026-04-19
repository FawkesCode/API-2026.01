package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.CompanyStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyStockRepository extends JpaRepository<CompanyStock, Long> {

    List<CompanyStock> findByStockId(Long stockId);

    Optional<CompanyStock> findByStockIdAndProductId(Long stockId, Long productId);

    List<CompanyStock> findByProductId(Long productId);

    List<CompanyStock> findByCurrentQuantityLessThanEqual(Integer quantity);
}