package com.fawkes.api.Services;

import com.fawkes.api.DTOs.StockItemDTO;
import com.fawkes.api.Repositories.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductStockRepository productStockRepository;

    public List<StockItemDTO> listAllStock() {
        return productStockRepository.findAll()
                .stream()
                .map(StockItemDTO::fromEntity)
                .toList();
    }
}