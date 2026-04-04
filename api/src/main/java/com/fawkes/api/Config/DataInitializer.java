package com.fawkes.api.Config;

import com.fawkes.api.Entities.Departments;
import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Roles;
import com.fawkes.api.Repositories.DepartmentRepository;
import com.fawkes.api.Repositories.GroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;

    public DataInitializer(GroupRepository groupRepository, DepartmentRepository departmentRepository) {
        this.groupRepository = groupRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(String... args) {
        for (Roles role : Roles.values()) {
            groupRepository.findByRole(role).orElseGet(() -> {
                Group group = new Group();
                group.setRole(role);
                group.setGroupDescription("Grupo " + role.name());
                return groupRepository.save(group);
            });
        }

        if (departmentRepository.findAll().isEmpty()) {
            Departments dept = new Departments();
            dept.setDepartamentName("Padrao");
            dept.setText("Departamento padrao");
            departmentRepository.save(dept);
        }
    }
}
