package com.fawkes.api.Services;

import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
