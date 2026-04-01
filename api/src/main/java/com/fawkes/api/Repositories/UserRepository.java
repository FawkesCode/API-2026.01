package com.fawkes.api.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fawkes.api.Entities.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // Busca um usuário exatamente pelo nome de usuário
    Optional<Users> findByUserName(String userName);

    // Busca pelo e-mail (muito usado no Login)
    Optional<Users> findByUserMail(String userMail);
}
