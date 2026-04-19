/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.Services;

import com.fawkes.api.Repositories.OrderNoteRepository;
import jakarta.transaction.Transactional;
import com.fawkes.api.Entities.OrderNote;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

/**
 *
 * @author vitor
 */
@Service
public class OrderNoteService {
    private final OrderNoteRepository orderNoteRepository;
    
    
    public OrderNoteService(OrderNoteRepository orderNoteRepository){
    this.orderNoteRepository = orderNoteRepository;
    }
    
    public boolean findExistentNote(String numberNote,String serie){
     boolean ExistentOrderNote = orderNoteRepository.existsByNumberNoteAndSerie(numberNote,serie);
     return ExistentOrderNote;
             }
     
   
    @Transactional
    public OrderNote insertOrderNote(String numberNote,
            String serie, LocalDate dataNota){
        LocalDate now = LocalDate.now();
 
        if ((findExistentNote(numberNote,serie)== true)){
            throw new RuntimeException("Esta nota já existe! insira um valor diferente ou modifique a atual!");
        }
        if ((dataNota.isAfter(now))){
            throw new RuntimeException("A data da nota é inválida");    
        }
        
      
        
            OrderNote orderNote = new OrderNote();
            orderNote.setNumberNote(numberNote);
            orderNote.setSerie(serie);
            return orderNoteRepository.save(orderNote);
        
    }
}
