package com.fawkes.api.Services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.fawkes.api.Entities.Departments;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Repositories.OrderNoteRepository;

import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Entities.TicketEnum;
import com.fawkes.api.Repositories.TicketRepository;



@Service
public class TicketCreateService {
    private final DepartmentRepository departmentRepository;
    private final TicketRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderNoteRepository orderNoteRepository;
    //

    public TicketCreateService( DepartmentRepository departmentRepository,
                            UserRepository userRepository,
                            TicketRepository orderRepository,
                            OrderNoteRepository orderNoteRepository){
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.orderNoteRepository = orderNoteRepository;
                                                            }
    @Transactional
    public Ticket createOrder(Integer userID, 
        Integer departmentID, 
        BigDecimal value){
        
        Departments dept = departmentRepository.findById(Long.valueOf(departmentID))
        .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));

        Users userIdNumber = userRepository.findByIdAndIsActiveTrue(Long.valueOf(userID))
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou não está ativo."));

        if(value.compareTo(BigDecimal.ZERO)==1){
            throw new RuntimeException("O valor é nulo ou negativo");
        }
        
        Ticket order = new Ticket();
        order.setDepartamento(dept);
        order.setUsuario(userIdNumber);
        order.setValor(value);
        order.setStatus(TicketEnum.pendente);
        
        return orderRepository.save(order);

        }


 }


