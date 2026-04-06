# 🧪 TESTE: Response 403 com Mensagem Estruturada

## Cenário: Manager tentando criar usuário (requer DIRECTOR)

### 1️⃣ Primeiro, faça login como MANAGER

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "manager@gmail.com",
    "password": "senha123"
  }'
```

Salve o token retornado.

### 2️⃣ Tente criar um usuário COM o token de MANAGER

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {SEU_TOKEN_AQUI}" \
  -d '{
    "userName": "Whigga1!",
    "userMail": "whigga.1234@gmail.com",
    "password": "oiWhigga123",
    "role": "OPERATIONAL"
  }'
```

### 3️⃣ Resposta esperada (NÃO mais vazia!)

```json
{
  "timestamp": "2026-04-06T18:57:28.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Acesso negado: Você não possui as permissões necessárias para acessar este recurso. Verifique seu token JWT e certifique-se de que possui a role correta (DIRECTOR, MANAGER ou OPERATIONAL)."
}
```

---

## ✅ O que foi corrigido

1. ✅ **CustomAccessDeniedHandler**: Intercepta `AccessDeniedException` do Spring Security
2. ✅ **SecurityConfig**: Adicionado handler para exceções de acesso
3. ✅ **AuthController**: Validação explícita de role DIRECTOR
4. ✅ **Response estruturado**: JSON com timestamp, status, erro e mensagem

---

## 🎯 Resultado

Agora você receberá:
- ✅ HTTP 403 com body JSON completo
- ✅ Mensagem clara sobre o motivo da negação
- ✅ Informações úteis para debugging
- ✅ Sem response vazio!

---

## 🔐 Como testar com sucesso (DIRECTOR criando usuário)

### 1️⃣ Login como DIRECTOR (usando token dev)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@gmail.com",
    "password": "niggasForever"
  }'
```

### 2️⃣ Criar usuário COM token de DIRECTOR

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN_DIRECTOR}" \
  -d '{
    "userName": "Whigga1!",
    "userMail": "whigga.1234@gmail.com",
    "password": "oiWhigga123",
    "role": "OPERATIONAL"
  }'
```

### 3️⃣ Resposta esperada (sucesso!)

```json
"Usuário criado com sucesso: whigga.1234@gmail.com"
```

---

**Agora você tem tratamento robusto de 403! 🚀**
