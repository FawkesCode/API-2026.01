package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.UserUpdateRequest;
import com.fawkes.api.Entities.Department;
import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Roles;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.GroupRepository;
import com.fawkes.api.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
                            Integer groupID, Integer departamentID, String password, Roles role) {

        if (findExistentMail(userMail))
            throw new RuntimeException("Este email já foi cadastrado");
        if (findExistentName(userName))
            throw new RuntimeException("Este nome de usuário já foi cadastrado");

        Group group = groupRepository.findById(Long.valueOf(groupID))
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        Department dept = departmentRepository.findById(Long.valueOf(departamentID))
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(dept);
        user.setIsActive(true);
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    @Transactional
    public Users insertUserSimple(String userName, String userMail, String password, 
                                   String roleName, String departamentName) {
        if (findExistentMail(userMail))
            throw new RuntimeException("Este email já foi cadastrado");
        if (findExistentName(userName))
            throw new RuntimeException("Este nome de usuário já foi cadastrado");

        Roles role;
        try {
            role = Roles.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role inválida. Use: DIRECTOR, MANAGER ou OPERATIONAL");
        }

        Group group = groupRepository.findByRole(role)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado para a role: " + role));

        Department dept = new Department();
        dept.setDepartamentName(departamentName);
        dept.setText("Departamento " + departamentName);
        dept = departmentRepository.save(dept);

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(dept);
        user.setIsActive(true);
        user.setRoles(Set.of(role));

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

        Department defaultDept = departmentRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum departamento encontrado"));

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(defaultDept);
        user.setIsActive(true);
        user.setRoles(Set.of(role));

        return userRepository.save(user);
    }

    public List<Users> listAll() {
        return userRepository.findAll();
    }

    @Transactional
    public List<Users> listAllWithDetails() {
        return userRepository.findAllWithGroupAndDepartment();
    }

    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
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

    public Users update(Long id, Users updatedData) {
        Users user = findById(id);
        if (updatedData.getUserName() != null) {
            user.setUserName(updatedData.getUserName());
        }
        if (updatedData.getUserMail() != null) {
            user.setUserMail(updatedData.getUserMail());
        }
        if (updatedData.getGroup() != null) {
            user.setGroup(updatedData.getGroup());
        }
        if (updatedData.getDepartments() != null) {
            user.setDepartments(updatedData.getDepartments());
        }
        if (updatedData.getIsActive() != null) {
            user.setIsActive(updatedData.getIsActive());
        }
        if (updatedData.getRoles() != null && !updatedData.getRoles().isEmpty()) {
            user.setRoles(updatedData.getRoles());
        }
        return userRepository.save(user);
    }

    @Transactional
    public Users update(Long id, UserUpdateRequest request) {
        Users user = findById(id);
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getUserMail() != null) {
            user.setUserMail(request.getUserMail());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        if (request.getRoleName() != null) {
            try {
                Roles role = Roles.valueOf(request.getRoleName());
                Group group = groupRepository.findByRole(role)
                        .orElseThrow(() -> new RuntimeException("Grupo não encontrado para a role: " + role));
                user.setGroup(group);
                user.setRoles(Set.of(role));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Role inválida. Use: DIRECTOR, MANAGER ou OPERATIONAL");
            }
        }
        if (request.getDepartmentName() != null) {
            Department dept = departmentRepository.findByDepartamentName(request.getDepartmentName())
                    .orElseGet(() -> {
                        Department newDept = new Department();
                        newDept.setDepartamentName(request.getDepartmentName());
                        newDept.setText("Departamento " + request.getDepartmentName());
                        return departmentRepository.save(newDept);
                    });
            user.setDepartments(dept);
        }
        return userRepository.save(user);
    }

    public Users toggleStatus(Long id) {
        Users user = findById(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        return userRepository.save(user);
    }
}