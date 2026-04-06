package com.fawkes.api.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return buildResponse(404, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AcessoNegadoException ex) {
        return buildResponse(403, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        String mensagem = "Acesso negado: Você não possui as permissões necessárias para acessar este recurso. " +
                "Verifique seu token JWT e certifique-se de que possui a role correta (DIRECTOR, MANAGER ou OPERATIONAL).";
        return buildResponse(403, "ACCESS_DENIED", mensagem);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        String mensagem = "Erro de autenticação: Token inválido, expirado ou ausente. " +
                "Por favor, faça login novamente e inclua o token JWT no header 'Authorization: Bearer {token}'.";
        return buildResponse(401, "UNAUTHORIZED", mensagem);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return buildResponse(422, "UNPROCESSABLE_ENTITY", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return buildResponse(500, "INTERNAL_SERVER_ERROR", "Erro interno no servidor.");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(int status, String erro, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("erro", erro);
        body.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(body);
    }
}