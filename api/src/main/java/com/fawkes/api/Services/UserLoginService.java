package com.fawkes.api.Services;
import com.fawkes.api.Security.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fawkes.api.Entities.Users;

@Service
public class UserLoginService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserLoginService(JwtUtils jwtUtils, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    public String loginUser(String email, String password) {
        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return jwtUtils.generateToken(user.getUserMail());
    }
    }
