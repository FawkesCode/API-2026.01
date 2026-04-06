package com.fawkes.api.DTOs.Request;

import com.fawkes.api.Entities.Roles;
import lombok.Data;

@Data
public class SignUpRequest {
    private String userName;
    private String userMail;
    private String password;
    private Roles role;
    private String departamentName;
}