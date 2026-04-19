package com.fawkes.api.Services;

import com.fawkes.api.Entities.*;
import com.fawkes.api.Repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fawkes.api.DTOs.ActivityDTO;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {

    private final CompanyStockRepository companyStockRepository;
    private final ProductInputsRepository productInputsRepository;
    private final ProductOutputsRepository productOutputsRepository;
    private final ProductsRepository productsRepository;
    private final StockRepository stockRepository;

    @Transactional
    public ProductInputs registerInput(Long stockId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser maior que zero.");
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado: " + stockId));

        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

        CompanyStock companyStock = companyStockRepository.findByStockIdAndProductId(stockId, productId)
                .orElse(null);

        int novoSaldo;
        if (companyStock != null) {
            novoSaldo = calcularSaldoEntrada(companyStock, quantity);
            companyStock.setCurrentQuantity(novoSaldo);
            companyStock.setLastInputDate(java.time.LocalDateTime.now());
        } else {
            novoSaldo = quantity;
            companyStock = new CompanyStock();
            companyStock.setStock(stock);
            companyStock.setProduct(product);
            companyStock.setCurrentQuantity(novoSaldo);
            companyStock.setLastInputDate(java.time.LocalDateTime.now());
        }

        companyStockRepository.save(companyStock);

        ProductInputs input = new ProductInputs();
        input.setStock(stock);
        input.setProduct(product);
        input.setQuantity(quantity);

        return productInputsRepository.save(input);
    }

    @Transactional
    public ProductOutputs registerOutput(Long stockId, Long productId, Integer quantity, Ticket order) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade de saída deve ser maior que zero.");
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado: " + stockId));

        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

        CompanyStock companyStock = companyStockRepository.findByStockIdAndProductId(stockId, productId)
                .orElseThrow(() -> new RuntimeException("Saldo do produto não encontrado: " + productId));

        int novoSaldo = calcularSaldoSaida(companyStock, quantity);

        companyStock.setCurrentQuantity(novoSaldo);
        companyStock.setLastOutputDate(java.time.LocalDateTime.now());
        companyStockRepository.save(companyStock);

        ProductOutputs output = new ProductOutputs();
        output.setStock(stock);
        output.setProduct(product);
        output.setQuantity(quantity);
        output.setOrder(order);

        return productOutputsRepository.<ProductOutputs>save(output);
    }

    public List<ProductInputs> listAllInputs() {
        return productInputsRepository.findAll();
    }

    public List<ProductOutputs> listAllOutputs() {
        return productOutputsRepository.findAll();
    }

    private int calcularSaldoEntrada(CompanyStock companyStock, int quantity) {
        int novoSaldo = companyStock.getCurrentQuantity() + quantity;
        if (companyStock.getMaxStockQuantity() != null && novoSaldo > companyStock.getMaxStockQuantity()) {
            throw new IllegalStateException(
                    "Entrada excede o estoque máximo do produto. Máximo: "
                            + companyStock.getMaxStockQuantity()
                            + ", Saldo atual: " + companyStock.getCurrentQuantity()
                            + ", Tentativa de entrada: " + quantity
            );
        }
        return novoSaldo;
    }

    private int calcularSaldoSaida(CompanyStock companyStock, int quantity) {
        int novoSaldo = companyStock.getCurrentQuantity() - quantity;
        if (companyStock.getMinStockQuantity() != null && novoSaldo < companyStock.getMinStockQuantity()) {
            throw new IllegalStateException(
                    "Saída deixaria o estoque abaixo do mínimo do produto. Mínimo: "
                            + companyStock.getMinStockQuantity()
                            + ", Saldo atual: " + companyStock.getCurrentQuantity()
                            + ", Tentativa de saída: " + quantity
            );
        }
        return novoSaldo;
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listActivity() {
        try {
            log.info("Iniciando listActivity()");
            List<ActivityDTO> activities = new ArrayList<>();

            log.info("Buscando ProductInputs com LEFT JOIN FETCH");
            List<ProductInputs> inputs = productInputsRepository.findAllWithProduct();
            log.info("Encontrados {} ProductInputs", inputs.size());

            inputs.forEach(i -> {
                try {
                    ActivityDTO dto = ActivityDTO.fromInput(i);
                    activities.add(dto);
                    log.debug("Convertido input {} para ActivityDTO", i.getId());
                } catch (Exception e) {
                    log.error("Erro ao converter ProductInputs ID {} para ActivityDTO", i.getId(), e);
                    throw e;
                }
            });

            log.info("Buscando ProductOutputs com LEFT JOIN FETCH");
            List<ProductOutputs> outputs = productOutputsRepository.findAllWithProduct();
            log.info("Encontrados {} ProductOutputs", outputs.size());

            outputs.forEach(o -> {
                try {
                    ActivityDTO dto = ActivityDTO.fromOutput(o);
                    activities.add(dto);
                    log.debug("Convertido output {} para ActivityDTO", o.getId());
                } catch (Exception e) {
                    log.error("Erro ao converter ProductOutputs ID {} para ActivityDTO", o.getId(), e);
                    throw e;
                }
            });

            activities.sort(Comparator.comparing(ActivityDTO::date).reversed());
            log.info("listActivity() retornando {} atividades", activities.size());
            return activities;
        } catch (Exception e) {
            log.error("Erro em listActivity()", e);
            throw e;
        }
    }
}