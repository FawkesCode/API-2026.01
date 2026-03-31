package com.fawkes.api.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fawkes.api.Repository.UsersRepository;


@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final String userMail;

    public UserService (UsersRepository usersRepository,String userMail){

        this.usersRepository = usersRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userMail = userMai
        
    }



}
