# 🔐 Sistema de Autenticação e Autorização - API Fawkes

## 🎯 Visão Geral

Implementação completa de autenticação JWT com autorização por role (DIRECTOR, MANAGER, OPERATIONAL) e tratamento robusto de erros com mensagens dinâmicas por ambiente.

---

## 📚 Documentação Rápida

| Documento | Descrição |
|-----------|-----------|
| `BEARER_TOKEN_GUIDE.md` | 📖 Como integrar JWT no front-end (Fetch, Axios, cURL) |
| `ERROR_MESSAGES_BY_ENV.md` | 🌍 Mensagens de erro por ambiente (DEV vs PROD) |
| `TEST_403_RESPONSE.md` | 🧪 Teste de resposta 403 estruturada |
| `TEST_ENV_SPECIFIC_ERRORS.md` | 🧪 Teste de mensagens por ambiente |
| `SUMMARY_DYNAMIC_ERROR_MESSAGES.md` | 📋 Resumo da implementação |

---

## 🔄 Fluxo de Autenticação

```
1. LOGIN
   POST /auth/login
   { "email": "user@gmail.com", "password": "senha123" }
   ↓
2. RECEBE TOKEN
   { "token": "eyJ..." }
   ↓
3. SALVA TOKEN
   localStorage.setItem('token', token)
   ↓
4. USA EM REQUISIÇÕES
   GET /director/usuarios
   Header: Authorization: Bearer eyJ...
   ↓
5. API VALIDA
   - Verifica assinatura do token
   - Extrai email do token
   - Busca usuário no banco
   - Valida role/permissões
   ↓
6. RETORNA RESULTADO
   - ✅ 200 OK com dados
   - ❌ 403 Acesso Negado
   - ❌ 401 Token Inválido
```

---

## 🛡️ Roles Disponíveis

| Role | Permissões | Pode Criar Usuários |
|------|-----------|---------------------|
| **DIRECTOR** | Criar usuários, gerenciar todos | ✅ SIM |
| **MANAGER** | Gerenciar produtos, estoque | ❌ NÃO |
| **OPERATIONAL** | Visualizar dados | ❌ NÃO |

---

## 🔐 Endpoints Protegidos

### Públicos (sem autenticação)
```
POST   /auth/login              - Fazer login
GET    /actuator/health         - Health check
```

### DIRECTOR
```
POST   /auth/register           - Criar novo usuário
GET    /director/**             - Todos endpoints director
```

### MANAGER
```
GET    /manager/**              - Todos endpoints manager
```

### OPERATIONAL
```
GET    /operational/**          - Todos endpoints operational
```

---

## 💻 Exemplo: Login com Fetch

```javascript
// 1. Login
const response = await fetch('/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'teste@gmail.com',
    password: 'niggasForever'
  })
});

const { token } = await response.json();

// 2. Salvar token
localStorage.setItem('token', token);

// 3. Usar em requisição protegida
const dataResponse = await fetch('/director/usuarios', {
  headers: { 'Authorization': `Bearer ${token}` }
});

const data = await dataResponse.json();
```

---

## 🌍 Mensagens de Erro

### DEV (Desenvolvimento)
```json
{
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Apenas DIRECTORS podem criar novos usuários. Sua role atual não permite esta ação."
}
```

### PROD (Produção)
```json
{
  "status": 403,
  "erro": "FORBIDDEN",
  "mensagem": "Operação não permitida."
}
```

---

## ⚙️ Configuração

### DEV (application.properties)
```properties
spring.profiles.active=dev
# Mensagens detalhadas no terminal
# Hibernate SQL visível
# Endpoints sem restrição adicional
```

### PROD (application-prod.properties)
```properties
spring.profiles.active=prod
# Mensagens genéricas
# SQL oculto
# Validações rigorosas
```

---

## 📊 Estrutura do Token JWT

```javascript
{
  "sub": "teste@gmail.com",      // Email do usuário
  "iat": 1744742135,             // Emitido em (timestamp)
  "exp": 1744828535              // Expira em (timestamp)
}
```

**Duração:** 24 horas (86400000ms)

---

## 🚨 Tratamento de Erros

| Status | Erro | Significado | Ação |
|--------|------|-----------|------|
| 401 | UNAUTHORIZED | Token inválido/expirado | Fazer login novamente |
| 403 | ACCESS_DENIED | Sem permissão | Verificar role |
| 403 | FORBIDDEN | Negado pelo negócio | Ler mensagem |
| 404 | NOT_FOUND | Recurso não existe | Verificar ID |
| 422 | UNPROCESSABLE_ENTITY | Dados inválidos | Validar entrada |
| 500 | INTERNAL_SERVER_ERROR | Erro no servidor | Contactar suporte |

---

## ✅ Checklist de Implementação

- ✅ JWT Token gerado e validado
- ✅ Roles: DIRECTOR, MANAGER, OPERATIONAL
- ✅ Endpoints protegidos por role
- ✅ Handler de AccessDeniedException
- ✅ Mensagens dinâmicas por ambiente
- ✅ Tratamento robusto de erros
- ✅ Documentação completa
- ✅ Testes de exemplo

---

## 🔧 Próximos Passos

1. **Front-end**
   - [ ] Implementar login
   - [ ] Salvar token no localStorage
   - [ ] Adicionar token em requisições
   - [ ] Tratamento de token expirado

2. **Back-end**
   - [ ] Adicionar refresh token
   - [ ] Implementar logout
   - [ ] Adicionar 2FA (two-factor auth)
   - [ ] Logging de tentativas de acesso

3. **DevOps**
   - [ ] Configurar PROD
   - [ ] SSL/TLS
   - [ ] Rate limiting

---

## 📞 Suporte

Veja a documentação específica:
- Integração Front-end → `BEARER_TOKEN_GUIDE.md`
- Erros por Ambiente → `ERROR_MESSAGES_BY_ENV.md`
- Testes → `TEST_*.md`

---

**API Fawkes - Segura, Robusta, Pronta para Produção! 🚀**
