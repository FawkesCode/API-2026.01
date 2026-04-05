package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.LoginRequest;
import com.fawkes.api.DTOs.Request.SignUpRequest;
import com.fawkes.api.DTOs.Response.LoginResponse;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Services.UserLoginService;
import com.fawkes.api.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

// Adicionar lógica do controller aqui (Login e Registro) verificando roles!
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
        Users user = userService.registerUser(
                signUpRequest.getUserName(),
                signUpRequest.getUserMail(),
                signUpRequest.getPassword(),
                signUpRequest.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso: " + user.getUserMail());
    }



}
