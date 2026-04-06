package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.Users;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String userName,
        String userMail,
        Boolean isActive,
        LocalDateTime creationDate,
        String groupName,
        String departamentName
) {
    public static UserDTO fromEntity(Users user) {
        String groupName = (user.getGroup() != null && user.getGroup().getRole() != null)
                ? user.getGroup().getRole().name() : "N/A";
        String deptName = (user.getDepartments() != null)
                ? user.getDepartments().getDepartamentName() : "N/A";
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getUserMail(),
                user.getIsActive(),
                user.getCreationDate(),
                groupName,
                deptName
        );
    }
}
