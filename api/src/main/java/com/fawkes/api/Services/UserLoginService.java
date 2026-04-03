package com.fawkes.api.Services;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Entities.Users;

@Service
public class UserLoginService {
    private final UserRepository UserRepository;

    public UserLoginService(UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }
    public Optional<Users> FindByEmail(String email){
        return UserRepository.findByUserMail(email);
    }
}
