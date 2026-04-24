package com.fawkes.api.Services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.fawkes.api.Entities.Department;
import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Repositories.OrderNoteRepository;

import jakarta.transaction.Transactional;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Entities.TicketEnum;
import com.fawkes.api.Repositories.TicketRepository;



@Service
public class TicketCreateService {
    private final DepartmentRepository departmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final OrderNoteRepository orderNoteRepository;
    //

    public TicketCreateService( DepartmentRepository departmentRepository,
                            UserRepository userRepository,
                            TicketRepository ticketRepository,
                            OrderNoteRepository orderNoteRepository){
    this.ticketRepository = ticketRepository;
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.orderNoteRepository = orderNoteRepository;
                                                            }
    @Transactional
    public Ticket createOrder(Long userID, 
        Long departmentID, 
        BigDecimal value){
        
        Department dept = departmentRepository.findById(Long.valueOf(departmentID))
        .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
        
        if(value.compareTo(BigDecimal.ZERO)<=0){
            throw new RuntimeException("O valor é nulo ou negativo");
        }
        
        Ticket order = new Ticket();
        order.setDepartamento(dept);
        order.setUsuario(userIdNumber);
        order.setValor(value);
        order.setStatus(TicketEnum.pendente);
        
        return ticketRepository.save(order);

        }
    
        @Transactional
        public Ticket updateTicketStatus(Ticket ticket, TicketEnum status){
            
        Ticket ticketToChange = ticketRepository.findById(ticket.getId())
                .orElseThrow(() -> new RuntimeException("Ticket não encotrado."));
        
        if(ticketToChange.getStatus() == status){
            throw new RuntimeException("O status do pedido já é: "+status.toString()+".");
        }
        
        ticketToChange.setStatus(status);
        return ticketRepository.save(ticketToChange);
        
    
    }
    

 }


