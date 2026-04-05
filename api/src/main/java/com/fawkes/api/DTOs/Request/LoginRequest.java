package com.fawkes.api.DTOs.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public record LoginRequest(@NotEmpty(message = "Email é Obrigatório") String email,
                           @NotEmpty(message = "Senha é obrigatória") String password){}

