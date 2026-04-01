package com.fawkes.api.Service;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fawkes.api.Entities.Departments;
import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Repositories.GroupRepository;
import com.fawkes.api.Repositories.DepartmentRepository;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private String userMail;


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
    public boolean findExistentMail(){
        
    Optional<Users> userMailConfirm = userRepository.findByUserMail(this.userMail);

    if (userMailConfirm.isPresent()){
        return true;
    }
    return false;
        
    }

    public Users InsertUser( String userName,
        String userMail,
        Integer groupID,
        Integer departamentID,
        String password){

        this.userMail = userMail;

        Group group = groupRepository.findById(Long.valueOf(groupID))
        .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        
        Departments dept = departmentRepository.findById(Long.valueOf(departamentID))
        .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

       



        if(findExistentMail()){
            throw new RuntimeException("Este email já exite");
        }
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


