# 🧪 TESTE: Mensagens de Erro por Ambiente

## 📋 Cenário: Testar mensagens em DEV vs PROD

---

## 1️⃣ TESTAR EM DEV (padrão)

### Setup
Certifique-se que está rodando em DEV:
```bash
# application.properties
spring.profiles.active=dev
```

### Teste 1: Erro 403 - Sem permissão
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {MANAGER_TOKEN}" \
  -d '{
    "userName": "test",
    "userMail": "test@gmail.com",
    "password": "test123",
    "role": "OPERATIONAL"
  }'
```

**Resposta DEV:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Apenas DIRECTORS podem criar novos usuários. Sua role atual não permite esta ação."
}
```

---

### Teste 2: Erro 401 - Token expirado/inválido
```bash
curl -X GET http://localhost:8080/director/usuarios \
  -H "Authorization: Bearer invalid_token_xxx"
```

**Resposta DEV:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Permissões insuficientes para acessar este recurso. Verifique sua role."
}
```

---

### Teste 3: Erro 404 - Recurso não encontrado
```bash
curl -X GET http://localhost:8080/director/usuario/999
```

**Resposta DEV:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 404,
  "erro": "NOT_FOUND",
  "mensagem": "Recurso não encontrado. Verifique o ID ou caminho da sua requisição."
}
```

---

## 2️⃣ TESTAR EM PROD

### Setup
Mudar para PROD:
```bash
# application.properties
spring.profiles.active=prod
```

Ou na linha de comando:
```bash
java -jar target/api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Teste 1: Erro 403 - Sem permissão (PROD)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {MANAGER_TOKEN}" \
  -d '{...}'
```

**Resposta PROD:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Operação não permitida."
}
```

⚠️ **Note:** Mensagem genérica, não revela que era restrição de DIRECTOR!

---

### Teste 2: Erro 401 - Token inválido (PROD)
```bash
curl -X GET http://localhost:8080/director/usuarios \
  -H "Authorization: Bearer invalid_token_xxx"
```

**Resposta PROD:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Acesso negado."
}
```

---

### Teste 3: Erro 404 - Recurso não encontrado (PROD)
```bash
curl -X GET http://localhost:8080/director/usuario/999
```

**Resposta PROD:**
```json
{
  "timestamp": "2026-04-06T19:03:00.000000000",
  "status": 404,
  "erro": "NOT_FOUND",
  "mensagem": "Recurso não encontrado."
}
```

⚠️ **Note:** Sem dica sobre verificar ID ou caminho

---

## 📊 Tabela Comparativa

| Cenário | DEV | PROD |
|---------|-----|------|
| Sem permissão DIRECTOR | "Apenas DIRECTORS podem criar..." | "Operação não permitida." |
| Token inválido | "Token inválido, expirado ou ausente..." | "Acesso negado." |
| Recurso não encontrado | "Verifique o ID ou caminho..." | "Recurso não encontrado." |
| Erro interno (500) | "Erro interno. Entre em contato com suporte." | "Erro interno. Tente novamente." |

---

## ✅ Checklist

- [ ] Rodei em DEV e recebi mensagens detalhadas
- [ ] Rodei em PROD e recebi mensagens genéricas
- [ ] Confirmei que as mensagens são diferentes
- [ ] Testei pelo menos 3 endpoints
- [ ] Comparei as diferenças

---

## 🔄 Para Customizar Mensagens

### DEV: Editar
```properties
# /api/src/main/resources/application.properties

app.error-messages.resource-not-found=Minha mensagem custom
app.error-messages.access-denied=Minha mensagem custom
# ... etc
```

### PROD: Editar
```properties
# /api/src/main/resources/application-prod.properties

app.error-messages.resource-not-found=Minha mensagem generic
app.error-messages.access-denied=Minha mensagem generic
# ... etc
```

---

**Agora sua API é segura em PROD e informativa em DEV! 🔐✨**
