package com.fawkes.api.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
}

