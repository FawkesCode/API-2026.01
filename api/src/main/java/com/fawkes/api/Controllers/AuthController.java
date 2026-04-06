package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.LoginRequest;
import com.fawkes.api.DTOs.Request.SignUpRequest;
import com.fawkes.api.DTOs.Response.LoginResponse;
import com.fawkes.api.Entities.Roles;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Exceptions.AcessoNegadoException;
import com.fawkes.api.Services.UserLoginService;
import com.fawkes.api.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserLoginService userLoginService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userLoginService.loginUser(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        // ✅ Verificação explícita de role DIRECTOR
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isDirector = authentication != null && 
                            authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_DIRECTOR"));
        
        if (!isDirector) {
            throw new AcessoNegadoException(
                "Você não tem permissão para criar usuários. Apenas DIRECTORS podem registrar novos usuários. " +
                "Sua role atual não permite esta ação."
            );
        }

        Users user = userService.registerUser(
                signUpRequest.getUserName(),
                signUpRequest.getUserMail(),
                signUpRequest.getPassword(),
                signUpRequest.getRole()
        );
        if (signUpRequest.getRole() != null) {
            user.setRoles(Set.of(signUpRequest.getRole()));
        } else {
            user.setRoles(Set.of(Roles.MANAGER));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso: " + user.getUserMail());
    }
}
