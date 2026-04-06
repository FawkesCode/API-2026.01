package com.fawkes.api.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 403);
        body.put("erro", "ACCESS_DENIED");
        body.put("mensagem", "Acesso negado: Você não possui as permissões necessárias para acessar este recurso. " +
                "Verifique seu token JWT e certifique-se de que possui a role correta (DIRECTOR, MANAGER ou OPERATIONAL).");

        response.getWriter().write(convertMapToJson(body));
    }

    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        map.forEach((key, value) -> {
            sb.append("\"").append(key).append("\":");
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            sb.append(",");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
}
