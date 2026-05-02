package com.fawkes.api.Config;

import com.fawkes.api.Entities.*;
import com.fawkes.api.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

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
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            System.out.println("🚀 Iniciando seed de dados de teste...");

            // ==================== GRUPO ====================
            Group adminGroup = groupRepository.findById(1L).orElseGet(() -> {
                Group g = new Group();
                g.setRole(Roles.DIRECTOR);
                g.setGroupDescription("Grupo de diretores do sistema");
                return groupRepository.save(g);
            });

            Group managerGroup = groupRepository.findById(2L).orElseGet(() -> {
                Group g = new Group();
                g.setRole(Roles.MANAGER);
                g.setGroupDescription("Grupo de gerentes do sistema");
                return groupRepository.save(g);
            });
            Group operationalGroup = groupRepository.findById(3L).orElseGet(() -> {
                Group g = new Group();
                g.setRole(Roles.OPERATIONAL);
                g.setGroupDescription("Grupo de operários do sistema");
                return groupRepository.save(g);
            });


            // ==================== DEPARTAMENTO ====================
            Department logisticaDept = departmentRepository.findById(1L).orElseGet(() -> {
                Department d = new Department();
                d.setDepartamentName("Logística");
                d.setText("Departamento de Logística");
                return departmentRepository.save(d);
            });

            // ==================== FORNECEDOR ====================
            Suppliers fornecedorTeste = supplierRepository.findByCnpj("12.345.678/0001-55")
                    .orElseGet(() -> {
                        Suppliers s = new Suppliers();
                        s.setSupplierName("Fornecedor Teste");
                        s.setCnpj("12.345.678/0001-55");
                        s.setPaymentMethods(Set.of(PaymentMethod.PIX));
                        s.setIsActive(Boolean.TRUE);
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
            Users user = userRepository.findByUserMail("teste@gmail.com")
                    .orElseGet(Users::new);

            user.setUserName("admin");
            user.setUserMail("teste@gmail.com");
            user.setPassword(passwordEncoder.encode("teste123"));
            user.setIsActive(true);
            user.setGroup(adminGroup);
            user.setDepartments(logisticaDept);

            if (user.getCreationDate() == null) {
                user.setCreationDate(LocalDateTime.now());
            }
            userRepository.save(user);
            System.out.println("✅ Usuário de teste → teste@gmail.com / teste123");

            System.out.println("✅ Seed de dados concluído com sucesso!");
        };
    }
}