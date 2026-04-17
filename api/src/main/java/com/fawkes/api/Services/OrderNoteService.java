/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fawkes.api.Services;

import com.fawkes.api.Repositories.OrderNoteRepository;
import jakarta.transaction.Transactional;
import com.fawkes.api.Entities.OrderNote;
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
    @Transactional
    public OrderNote insertOrderNote(String numberNote,
            String serie){
            OrderNote orderNote = new OrderNote();
            orderNote.setNumberNote(numberNote);
            orderNote.setSerie(serie);
            return orderNoteRepository.save(orderNote);
        
    }
}
