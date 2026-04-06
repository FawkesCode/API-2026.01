package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.SignUpRequest;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Users>> listAll() {
        return ResponseEntity.ok(userService.listAll());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}