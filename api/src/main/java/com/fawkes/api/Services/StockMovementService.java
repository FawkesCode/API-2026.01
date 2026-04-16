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

    private final ProductStockRepository productStockRepository;
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

        ProductStock productStock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Saldo do produto não encontrado: " + productId));

        int novoSaldo = calcularSaldoEntrada(productStock, quantity);

        productStock.setCurrentStockQuantity(novoSaldo);
        productStockRepository.save(productStock);

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

        ProductStock productStock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Saldo do produto não encontrado: " + productId));

        int novoSaldo = calcularSaldoSaida(productStock, quantity);

        productStock.setCurrentStockQuantity(novoSaldo);
        productStockRepository.save(productStock);

        ProductOutputs output = new ProductOutputs();
        output.setStock(stock);
        output.setProduct(product);
        output.setQuantity(quantity);
        output.setOrder(order);

        return productOutputsRepository.<ProductOutputs>save(output);
    }

    // --- Métodos adicionados para a tela de Atividade Recente ---

    public List<ProductInputs> listAllInputs() {
        return productInputsRepository.findAll();
    }

    public List<ProductOutputs> listAllOutputs() {
        return productOutputsRepository.findAll();
    }

    // --- Métodos privados de cálculo ---

    private int calcularSaldoEntrada(ProductStock productStock, int quantity) {
        int novoSaldo = productStock.getCurrentStockQuantity() + quantity;
        if (novoSaldo > productStock.getMaxStockQuantity()) {
            throw new IllegalStateException(
                    "Entrada excede o estoque máximo do produto. Máximo: "
                            + productStock.getMaxStockQuantity()
                            + ", Saldo atual: " + productStock.getCurrentStockQuantity()
                            + ", Tentativa de entrada: " + quantity
            );
        }
        return novoSaldo;
    }

    private int calcularSaldoSaida(ProductStock productStock, int quantity) {
        int novoSaldo = productStock.getCurrentStockQuantity() - quantity;
        if (novoSaldo < productStock.getMinStockQuantity()) {
            throw new IllegalStateException(
                    "Saída deixaria o estoque abaixo do mínimo do produto. Mínimo: "
                            + productStock.getMinStockQuantity()
                            + ", Saldo atual: " + productStock.getCurrentStockQuantity()
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

            // findAllWithProduct() usa LEFT JOIN FETCH — carrega product junto,
            // evitando N+1 queries (uma query para tudo, não uma por registro).
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