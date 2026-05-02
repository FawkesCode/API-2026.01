package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Suppliers, Long> {
    List<Suppliers> findByIsActive(Boolean isActive);
}
