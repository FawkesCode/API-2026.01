package com.fawkes.api.Services;

import com.fawkes.api.Entities.Users;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userService.findByEmail(email)  // ← nome correto agora
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        String role = "ROLE_" + user.getGroup().getRole();
        return new User(
                user.getUserMail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}