package com.fawkes.api.Services;

import com.fawkes.api.DTOs.CheaperProductDTO;
import com.fawkes.api.Repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductComparisonService {

    private final ProductsRepository productsRepository;

    public List<CheaperProductDTO> getMostCheaperByProduct() {
        return productsRepository.findCheaperProduct();
    }
}