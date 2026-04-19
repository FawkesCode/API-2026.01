package com.fawkes.api.Repositories;

import com.fawkes.api.DTOs.CheaperProductDTO;
import com.fawkes.api.Entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    @Query("""
        SELECT new com.fawkes.api.DTOs.CheaperProductDTO(
            p.productName,
            p.productType,
            p.suppliers.nomeFornecedor,
            p.unitValue,
            ps.currentStockQuantity
        )
        FROM Products p
        JOIN ProductStock ps ON ps.product = p
        WHERE p.unitValue = (
            SELECT MIN(p2.unitValue)
            FROM Products p2
            WHERE p2.productName = p.productName
            AND p2.productType = p.productType
        )
        ORDER BY p.productName, p.productType
    """)
    List<CheaperProductDTO> findCheaperProduct();
}
