package com.fawkes.api.DTOs.Request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String userName;
    private String userMail;
    private String roleName;
    private String departmentName;
    private Boolean isActive;
}