package com.fawkes.api.Services;

import com.fawkes.api.Entities.ProductStock;
import com.fawkes.api.Entities.Products;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Repositories.ProductStockRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;
    private final ProductsRepository productsRepository;

    @Transactional
    public ProductStock updateLimits(Long productId, Integer min, Integer max) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado: " + productId));

        ProductStock ps = productStockRepository.findByProductId(productId)
                .orElseGet(() -> {
                    ProductStock novo = new ProductStock();
                    novo.setProduct(product);
                    novo.setCurrentStockQuantity(0);
                    return novo;
                });

        int effectiveMin = (min != null) ? min
                : (ps.getMinStockQuantity() != null ? ps.getMinStockQuantity() : 0);
        int effectiveMax = (max != null) ? max
                : (ps.getMaxStockQuantity() != null ? ps.getMaxStockQuantity() : 0);

        if (effectiveMin < 0 || effectiveMax < 0)
            throw new RegraDeNegocioException("Quantidades não podem ser negativas.");
        if (effectiveMax > 0 && effectiveMin > effectiveMax)
            throw new RegraDeNegocioException("Mínimo não pode ser maior que o máximo.");

        ps.setMinStockQuantity(effectiveMin);
        ps.setMaxStockQuantity(effectiveMax);
        return productStockRepository.save(ps);
    }

    public List<ProductStock> getCriticos() {
        return productStockRepository.findAll().stream()
                .filter(ps -> ps.getMinStockQuantity() != null
                        && ps.getMinStockQuantity() > 0
                        && ps.getCurrentStockQuantity() < ps.getMinStockQuantity())
                .toList();
    }
}