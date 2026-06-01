package com.fawkes.api.DTOs.Request;

import com.fawkes.api.Entities.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String userMail;
    @NotBlank
    private String password;
    @NotNull
    private Roles role;
    private String departamentName;
}