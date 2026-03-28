package com.fawkes.api.Repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fawkes.api.Entities.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
}
