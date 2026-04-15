package com.fawkes.api.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fawkes.api.Entities.Orders;


@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}

    

