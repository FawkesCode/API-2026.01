package com.fawkes.api.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.error-messages")
public class ErrorMessagesConfig {
    
    // ===================== MENSAGENS GENÉRICAS =====================
    private String resourceNotFound;
    private String accessDenied;
    private String unauthorized;
    private String businessRuleError;
    private String internalServerError;
    
    // ===================== MENSAGENS ESPECÍFICAS DE AUTH =====================
    private String registerOnlyDirector;
    private String invalidCredentials;
    private String expiredToken;
    private String invalidToken;
    
    // ===================== MENSAGENS DE AUTORIZAÇÃO =====================
    private String insufficientPermissions;
    private String roleRequired;
    
    // ===================== MENSAGENS DE VALIDAÇÃO =====================
    private String invalidInput;
    private String duplicateEmail;
    private String duplicateUsername;
    private String departmentNotFound;
    private String groupNotFound;
}
