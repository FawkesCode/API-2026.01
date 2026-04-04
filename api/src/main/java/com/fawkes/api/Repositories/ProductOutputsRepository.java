package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.ProductOutputs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOutputsRepository extends JpaRepository<ProductOutputs, Long> {

    // JOIN FETCH carrega product e stock junto numa única query SQL,
    // eliminando o problema N+1 que causava lentidão no histórico
    @Query("SELECT o FROM ProductOutputs o JOIN FETCH o.product JOIN FETCH o.stock")
    List<ProductOutputs> findAllWithProduct();
}