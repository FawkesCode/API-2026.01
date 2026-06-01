package com.fawkes.api.Exceptions;

import com.fawkes.api.Config.ErrorMessagesConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorMessagesConfig errorMessages;

    public GlobalExceptionHandler(ErrorMessagesConfig errorMessages) {
        this.errorMessages = errorMessages;
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage() : errorMessages.getResourceNotFound();
        return buildResponse(404, "NOT_FOUND", msg);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AcessoNegadoException ex) {
        return buildResponse(403, "FORBIDDEN", errorMessages.getAccessDenied());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(403, "ACCESS_DENIED", errorMessages.getInsufficientPermissions());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse(401, "UNAUTHORIZED", errorMessages.getUnauthorized());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocio(RegraDeNegocioException ex) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage() : errorMessages.getBusinessRuleError();
        return buildResponse(422, "UNPROCESSABLE_ENTITY", msg);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(409, "CONFLICT",
                "Não é possível concluir a operação: este registro está vinculado a pedidos ou movimentações.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(500, "INTERNAL_SERVER_ERROR", errorMessages.getInternalServerError());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return buildResponse(500, "INTERNAL_SERVER_ERROR", errorMessages.getInternalServerError());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(int status, String erro, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("erro", erro);
        body.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(body);
    }

    // Parâmetro com tipo errado — ex: ?period=INVALIDO ou ?supplierId=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format(
                "Valor inválido para o parâmetro '%s': '%s'. Tipo esperado: %s.",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido"
        );
        return buildResponse(400, "BAD_REQUEST", msg);
    }

    // Parâmetro obrigatório ausente
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return buildResponse(400, "BAD_REQUEST",
                "Parâmetro obrigatório ausente: '" + ex.getParameterName() + "'.");
    }

}