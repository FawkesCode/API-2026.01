package com.fawkes.api.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOutputRepository extends JpaRepository<ProductOutputRepository, Long> {
}
