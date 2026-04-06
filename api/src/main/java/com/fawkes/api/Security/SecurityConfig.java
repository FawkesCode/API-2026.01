package com.fawkes.api.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthTokenFilter authTokenFilter;

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").hasRole("DIRECTOR")
                        // Health check
                        .requestMatchers("/actuator/health").permitAll()
                        // TUDO que foi listado acima (tirando o register) → NÃO exige autenticação
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/operational/**").hasRole("OPERATIONAL")
                        .requestMatchers("/director/**").hasRole("DIRECTOR")
                        .anyRequest().authenticated()
                )
                // Pra quem sabe ExpressJS, isso aqui é basicamente o Middleware
                .addFilterBefore(
                        authTokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}