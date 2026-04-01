package com.fawkes.api.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fawkes.api.Entities.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByUserMail(String userMail);

    boolean existsByUserName(String userName);
}
