package com.fawkes.api.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthTokenFilter authTokenFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(AuthTokenFilter authTokenFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.authTokenFilter = authTokenFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
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
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").hasRole("DIRECTOR")
                        // Health check
                        .requestMatchers("/actuator/health").permitAll()
                        // TUDO que foi listado acima (tirando o register) → NÃO exige autenticação
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/operational/**").hasRole("OPERATIONAL")
                        .requestMatchers("/director/**").hasRole("DIRECTOR")
                        // Recebimento: operacional, gerente ou diretor
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/*/receive").hasAnyRole("MANAGER", "DIRECTOR", "OPERATIONAL")
                        // Marcar como comprado (confirm) e negar (cancel): somente diretor
                        .requestMatchers(HttpMethod.POST,
                                "/api/purchase-orders/*/confirm",
                                "/api/purchase-orders/*/cancel").hasRole("DIRECTOR")
                        // Envio, problema e devolução: gerente ou diretor
                        .requestMatchers(HttpMethod.POST,
                                "/api/purchase-orders/*/ship",
                                "/api/purchase-orders/*/problem",
                                "/api/purchase-orders/*/return").hasAnyRole("MANAGER", "DIRECTOR")
                        // Registro de cotação e edição de metadados do pedido: somente gerente/diretor
                        .requestMatchers(HttpMethod.PUT,
                                "/api/purchase-orders/*/items/prices",
                                "/api/purchase-orders/*").hasAnyRole("MANAGER", "DIRECTOR")
                        // Gestão de usuários: criar/editar/ativar = DIRECTOR
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/status").hasRole("DIRECTOR")
                        // Listagem de usuários: diretor ou gerente (note: /api/users/me continua liberado)
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("DIRECTOR", "MANAGER")
                        // Histórico de eventos de pedidos: gerente ou diretor
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/events").hasAnyRole("MANAGER", "DIRECTOR")
                        .anyRequest().authenticated()
                )
                // ✅ Adicionar handler customizado para 403
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(customAccessDeniedHandler)
                )
                // Pra quem sabe ExpressJS, isso aqui é basicamente o Middleware
                .addFilterBefore(
                        authTokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}