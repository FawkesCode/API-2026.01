package com.fawkes.api.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fawkes.api.Entities.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
}

    

