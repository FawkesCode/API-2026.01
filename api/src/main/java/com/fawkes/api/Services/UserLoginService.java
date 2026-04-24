package com.fawkes.api.Services;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.UserRepository;

@Service
public class UserLoginService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserLoginService(JwtUtils jwtUtils, UserService userService, 
            PasswordEncoder passwordEncoder,
           UserRepository userRepository ) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        
    }
    public String loginUser(String email, String password) {
     
        Users user = userService.findByEmail(email)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        
         if (!user.getIsActive()) {
             throw new RuntimeException("O usuário não está ativo!");
        }
         
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RecursoNaoEncontradoException("Senha inválida");
        }
        return jwtUtils.generateToken(user.getUserMail());
    }
    }
