package com.fawkes.api.Services;

import org.springframework.stereotype.Service;

import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.OrderRepository;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Repositories.OrderNoteRepository;


@Service
public class OrderCreateService {
    private final DepartmentRepository departmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderNoteRepository orderNoteRepository;
    

 public OrderCreateService( DepartmentRepository departmentRepository,
                            UserRepository userRepository,
                            OrderRepository orderRepository,
                            OrderNoteRepository orderNoteRepository){
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.orderNoteRepository = orderNoteRepository;
                                                            }

 public createOrder(){

 }

}
