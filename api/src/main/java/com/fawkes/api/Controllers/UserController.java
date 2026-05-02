package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.SignUpRequest;
import com.fawkes.api.DTOs.Request.UserUpdateRequest;
import com.fawkes.api.DTOs.UserDTO;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> users = userService.listAllWithDetails().stream()
                .map(UserDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<Users> create(@RequestBody SignUpRequest request) {
        Users newUser = userService.insertUserSimple(
                request.getUserName(),
                request.getUserMail(),
                request.getPassword(),
                request.getRole().name(),
                request.getDepartamentName()
        );
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Users> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleStatus(id));
    }

    /**
     * Get current logged-in user information
     * Used by frontend to fetch user details after login
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        String userEmail = authentication.getName();
        Users user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }
}