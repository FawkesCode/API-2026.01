package com.fawkes.api.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fawkes.api.Entities.Departments;
import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.UserRepository;

import jakarta.transaction.Transactional;

import com.fawkes.api.Repositories.GroupRepository;
import com.fawkes.api.Repositories.DepartmentRepository;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
  

    public UserService (
        UserRepository userRepository,
        GroupRepository groupRepository,
        DepartmentRepository departmentRepository
    ){
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        
    }
    public boolean findExistentMail(String userMail) {

    return userRepository.existsByUserMail(userMail);

    }

    public boolean findExistentName(String userName){

        return userRepository.existsByUserName(userName);
    }

    @Transactional
    public Users InsertUser( String userName,
        String userMail,
        Integer groupID,
        Integer departamentID,
        String password){

        if(findExistentMail(userMail)){
            throw new RuntimeException("Este email já foi cadastrado");}

        if(findExistentName(userName)){

            throw new RuntimeException ("Este nome de usuário ja foi cadastrado!");}

        Group group = groupRepository.findById(Long.valueOf(groupID))
        .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        
        Departments dept = departmentRepository.findById(Long.valueOf(departamentID))
        .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));


       
        Users user = new Users();

        user.setUserName(userName);
        user.setUserMail(userMail);
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setDepartments(dept);
        user.setGroup(group);

            return userRepository.save(user);

    }
    }


