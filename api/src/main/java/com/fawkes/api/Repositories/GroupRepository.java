package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.Group;
import com.fawkes.api.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Roles> findByName (Roles nome);
}
