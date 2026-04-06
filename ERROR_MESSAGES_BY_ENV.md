# 🌍 Mensagens de Erro por Ambiente

## 📋 Visão Geral

O sistema agora retorna mensagens de erro diferentes dependendo do ambiente (DEV ou PROD):

- **DEV**: Mensagens detalhadas para facilitar debugging
- **PROD**: Mensagens genéricas por questões de segurança

---

## 🔧 Como Configurar

### Ativar DEV (padrão)
```bash
# application.properties
spring.profiles.active=dev
```

### Ativar PROD
```bash
# application.properties
spring.profiles.active=prod
```

Ou via variável de ambiente:
```bash
java -jar app.jar --spring.profiles.active=prod
```

---

## 📊 Comparação de Mensagens

| Erro | DEV | PROD |
|------|-----|------|
| **404 - Resource Not Found** | "Recurso não encontrado. Verifique o ID ou caminho da sua requisição." | "Recurso não encontrado." |
| **401 - Unauthorized** | "Não autorizado. Token inválido, expirado ou ausente. Faça login novamente." | "Não autorizado." |
| **403 - Access Denied** | "Acesso negado. Você não possui as permissões necessárias para acessar este recurso." | "Acesso negado." |
| **422 - Business Rule Error** | "Erro de regra de negócio. Verifique os dados enviados." | "Operação não permitida." |
| **500 - Internal Server Error** | "Erro interno no servidor. Entre em contato com o suporte." | "Erro interno. Tente novamente mais tarde." |

---

## 🔐 Mensagens Específicas de Autenticação

### DEV
```json
{
  "erro": "UNAUTHORIZED",
  "mensagem": "Não autorizado. Token inválido, expirado ou ausente. Faça login novamente."
}
```

### PROD
```json
{
  "erro": "UNAUTHORIZED",
  "mensagem": "Não autorizado."
}
```

---

## 🚫 Exemplos de Resposta

### Teste 1: Tentar criar usuário sem ser DIRECTOR

#### DEV
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Authorization: Bearer {MANAGER_TOKEN}" \
  -d '{...}'
```

**Resposta:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Apenas DIRECTORS podem criar novos usuários. Sua role atual não permite esta ação."
}
```

#### PROD
**Resposta:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Operação não permitida."
}
```

---

### Teste 2: Acessar endpoint sem token

#### DEV
**Resposta:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Permissões insuficientes para acessar este recurso. Verifique sua role."
}
```

#### PROD
**Resposta:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Acesso negado."
}
```

---

## 🛠️ Customizar Mensagens

Para alterar mensagens, edite os arquivos:

- **Development**: `/api/src/main/resources/application.properties`
- **Production**: `/api/src/main/resources/application-prod.properties`

Exemplo:
```properties
app.error-messages.resource-not-found=Minha mensagem customizada
```

---

## 📝 Todos os Códigos de Erro Disponíveis

Você pode customizar estas mensagens:

```properties
# Genéricas
app.error-messages.resource-not-found=...
app.error-messages.access-denied=...
app.error-messages.unauthorized=...
app.error-messages.business-rule-error=...
app.error-messages.internal-server-error=...

# Auth
app.error-messages.register-only-director=...
app.error-messages.invalid-credentials=...
app.error-messages.expired-token=...
app.error-messages.invalid-token=...

# Autorização
app.error-messages.insufficient-permissions=...
app.error-messages.role-required=...

# Validação
app.error-messages.invalid-input=...
app.error-messages.duplicate-email=...
app.error-messages.duplicate-username=...
app.error-messages.department-not-found=...
app.error-messages.group-not-found=...
```

---

## 🎯 Boas Práticas

### ✅ DO
- Use mensagens genéricas em PROD por segurança
- Use mensagens detalhadas em DEV para debugging
- Inclua informações acionáveis em DEV (ex: "verifique seu token")
- Mantenha mensagens curtas e claras

### ❌ DON'T
- Não revele detalhes internos em PROD (ex: stack traces, nomes de DB)
- Não use mensagens muito técnicas para usuários finais
- Não deixe hardcoded mensagens de erro nos controllers

---

## 🚀 Próximas Melhorias

- [ ] Adicionar i18n (internacionalização) para múltiplos idiomas
- [ ] Criar severity levels (INFO, WARNING, ERROR, CRITICAL)
- [ ] Adicionar error codes numéricos para cliente consumir
- [ ] Implementar logging estruturado com correlationId

---

**Sua API agora é segura e informativa! 🔐✨**
