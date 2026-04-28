package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Entities.TicketEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Busca todos os pedidos de um usuário específico
    List<Ticket> findByUsuarioId(Long userId);

    // Busca todos os pedidos de um departamento específico
    List<Ticket> findByDepartamentoId(Long departmentId);

    // Busca pedidos por status
    List<Ticket> findByStatus(TicketEnum status);

    // Busca pedidos de um usuário filtrado por status
    List<Ticket> findByUsuarioIdAndStatus(Long userId, TicketEnum status);

    // Busca pedidos de um departamento filtrado por status
    List<Ticket> findByDepartamentoIdAndStatus(Long departmentId, TicketEnum status);

    // Busca todos ordenados por data de criação decrescente
    @Query("SELECT t FROM Ticket t ORDER BY t.createdAt DESC")
    List<Ticket> findAllOrderByCreatedAtDesc();
}

