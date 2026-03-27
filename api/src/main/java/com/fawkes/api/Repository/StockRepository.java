package com.fawkes.api.Repository;

import com.fawkes.api.Models.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findByProdutoId(Integer produtoId);
    boolean existsByProdutoId(Integer produtoId);
}