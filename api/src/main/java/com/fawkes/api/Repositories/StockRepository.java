package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // Usado no DataInitializer para buscar por nome em vez de ID fixo
    Optional<Stock> findByStockName(String stockName);
}