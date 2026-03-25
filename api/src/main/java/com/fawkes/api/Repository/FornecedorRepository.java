package com.fawkes.api.Repository;

import com.fawkes.api.Models.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
    // Métodos prontos: findAll(), save(), deleteById(), etc.
}