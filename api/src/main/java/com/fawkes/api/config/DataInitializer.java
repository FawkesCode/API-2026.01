package com.fawkes.api.config;

import com.fawkes.api.Entities.*;
import com.fawkes.api.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("dev")
    public CommandLineRunner initData(
            GroupRepository groupRepository,
            DepartmentRepository departmentRepository,
            StockRepository stockRepository,
            SupplierRepository supplierRepository,
            ProductsRepository productsRepository,
            ProductStockRepository productStockRepository,
            UserRepository userRepository) {

        return args -> {
            System.out.println("🚀 Iniciando seed de dados de teste...");

            // ==================== GRUPO ====================
            Group adminGroup = groupRepository.findById(1L).orElseGet(() -> {
                Group g = new Group();
                g.setGroupName("Administradores");
                g.setGroupDescription("Grupo de administradores do sistema");
                return groupRepository.save(g);
            });

            // ==================== DEPARTAMENTO (CORRIGIDO) ====================
            Departments logisticaDept = departmentRepository.findById(1L).orElseGet(() -> {
                Departments d = new Departments();
                d.setDepartamentName("Logística");           // nome exato do campo
                d.setText("Departamento de Logística");      // ← CAMPO OBRIGATÓRIO QUE FALTAVA
                return departmentRepository.save(d);
            });

            // ==================== FORNECEDOR ====================
            Suppliers fornecedorTeste = supplierRepository.findById(1L)
                    .orElseGet(() -> {
                        Suppliers s = new Suppliers();
                        s.setNomeFornecedor("Fornecedor Teste");
                        s.setCnpjFornecedor("12.345.678/0001-99");
                        s.setMeioPagamento(Suppliers.MeioPagamento.PIX);
                        return supplierRepository.save(s);
                    });

            // ==================== ESTOQUE ====================
            Stock estoquePrincipal = stockRepository.findById(1L)
                    .orElseGet(() -> {
                        Stock s = new Stock();
                        s.setStockName("Estoque Principal");
                        return stockRepository.save(s);
                    });

            // ==================== PRODUTO ====================
            Products produtoTeste = productsRepository.findById(2L)
                    .orElseGet(() -> {
                        Products p = new Products();
                        p.setProductName("Produto Teste");
                        p.setProductType("Eletrônico");
                        p.setMeasurementUnit(Products.MeasurementUnit.NAO_DEFINIDO);
                        p.setUnitValue(new BigDecimal("29.90"));
                        p.setDescription("Produto de teste");
                        p.setSuppliers(fornecedorTeste);
                        p.setStock(estoquePrincipal);
                        return productsRepository.save(p);
                    });

            // ==================== PRODUCT STOCK ====================
            productStockRepository.findById(1L)
                    .orElseGet(() -> {
                        ProductStock ps = new ProductStock();
                        ps.setProduct(produtoTeste);
                        ps.setCurrentStockQuantity(0);
                        ps.setMinStockQuantity(10);
                        ps.setMaxStockQuantity(100);
                        return productStockRepository.save(ps);
                    });

            // ==================== USUÁRIO DE TESTE ====================
            if (!userRepository.existsByUserMail("teste.123@gmail.com")) {
                Users user = new Users();
                user.setUserName("admin");
                user.setUserMail("teste.123@gmail.com");
                user.setPassword("$2a$10$8K7z7z7z7z7z7z7z7z7z7u7z7z7z7z7z7z7z7z7z7z7z7z7z7z7z7");
                user.setIsActive(true);
                user.setGroup(adminGroup);
                user.setDepartments(logisticaDept);
                user.setCreationDate(LocalDateTime.now());
                userRepository.save(user);
                System.out.println("✅ Usuário de teste criado → teste.123@gmail.com / teste123");
            }

            System.out.println("✅ Seed de dados concluído com sucesso!");
        };
    }
}