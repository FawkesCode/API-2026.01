package com.fawkes.api.Services;

import com.fawkes.api.Entities.Departments;
import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Roles;

import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.GroupRepository;
import com.fawkes.api.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            GroupRepository groupRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByUserMail(email);
    }

    public boolean findExistentMail(String userMail) {
        return userRepository.existsByUserMail(userMail);
    }

    public boolean findExistentName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Transactional
    public Users insertUser(String userName, String userMail,
                            Integer groupID, Integer departamentID, String password) {

        if (findExistentMail(userMail))
            throw new RuntimeException("Este email já foi cadastrado");
        if (findExistentName(userName))
            throw new RuntimeException("Este nome de usuário já foi cadastrado");

        Group group = groupRepository.findById(Long.valueOf(groupID))
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        Departments dept = departmentRepository.findById(Long.valueOf(departamentID))
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(dept);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    @Transactional
    public Users registerUser(String userName, String userMail, String password, Roles role) {
        if (findExistentMail(userMail))
            throw new RuntimeException("Este email já foi cadastrado");
        if (findExistentName(userName))
            throw new RuntimeException("Este nome de usuário já foi cadastrado");

        Group group = groupRepository.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado para a role: " + role));

        Departments defaultDept = departmentRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum departamento encontrado"));

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(defaultDept);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public List<Users> listAll() {
        return userRepository.findAll();
    }

    public Users create(Users user) {
        if (userRepository.existsByUserMail(user.getUserMail())) {
            throw new RuntimeException("Email já cadastrado.");
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("Username já cadastrado.");
        }
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
