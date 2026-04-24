package com.fawkes.api.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fawkes.api.Entities.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUserMail(String userMail);
    boolean existsByUserName(String userName);
    Optional<Users> findByUserMailAndIsActiveTrue(String userMail);
    Optional<Users> findByIdAndIsActiveTrue(Long id);
    Optional<Users> findByUserMail(String userMail);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.group LEFT JOIN FETCH u.departments")
    List<Users> findAllWithGroupAndDepartment();
}
