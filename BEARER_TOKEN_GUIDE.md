# 🔐 Guia de Integração Bearer Token - API Fawkes

## 📋 Sumário

1. [Fluxo de Autenticação](#fluxo-de-autenticação)
2. [Exemplos cURL](#exemplos-curl)
3. [Exemplos JavaScript/Fetch](#exemplos-javascriptfetch)
4. [Exemplos Axios](#exemplos-axios)
5. [Tratamento de Erros](#tratamento-de-erros)
6. [Endpoints Protegidos](#endpoints-protegidos)

---

## 🔄 Fluxo de Autenticação

```
1. LOGIN (POST /auth/login)
   ↓
2. RECEBER JWT TOKEN
   ↓
3. USAR TOKEN EM REQUISIÇÕES (Authorization: Bearer {token})
   ↓
4. API VALIDA E AUTORIZA A AÇÃO
```

---

## 💻 Exemplos cURL

### 1️⃣ Login e obter token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@gmail.com",
    "password": "niggasForever"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0ZUBnbWFpbC5jb20iLCJpYXQiOjE3NDQ3NDIxMzUsImV4cCI6MTc0NDgyODUzNX0.ABC123..."
}
```

### 2️⃣ Acessar endpoint protegido COM token

```bash
curl -X GET http://localhost:8080/director/usuarios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Resposta esperada:**
```json
[
  {
    "id": 1,
    "userName": "admin",
    "userMail": "teste@gmail.com",
    "isActive": true,
    ...
  }
]
```

### 3️⃣ Tentativa SEM token (receberá 403)

```bash
curl -X GET http://localhost:8080/director/usuarios
```

**Resposta esperada:**
```json
{
  "timestamp": "2026-04-06T18:53:00.000000000",
  "status": 403,
  "erro": "ACCESS_DENIED",
  "mensagem": "Acesso negado: Você não possui as permissões necessárias para acessar este recurso. Verifique seu token JWT e certifique-se de que possui a role correta (DIRECTOR, MANAGER ou OPERATIONAL)."
}
```

---

## 🚀 Exemplos JavaScript/Fetch

### Login e salvar token

```javascript
async function login(email, password) {
  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(`Login falhou: ${error.mensagem}`);
    }

    const data = await response.json();
    
    // ✅ Salvar token no localStorage
    localStorage.setItem('token', data.token);
    console.log('✅ Login bem-sucedido! Token salvo.');
    
    return data.token;
  } catch (error) {
    console.error('❌ Erro no login:', error.message);
  }
}
```

### Fazer requisições COM token

```javascript
async function fetchProtectedResource(endpoint) {
  try {
    // ✅ Recuperar token do localStorage
    const token = localStorage.getItem('token');
    
    if (!token) {
      throw new Error('Token não encontrado. Faça login primeiro.');
    }

    const response = await fetch(`http://localhost:8080${endpoint}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const error = await response.json();
      
      if (response.status === 403) {
        console.error('❌ Acesso Negado:', error.mensagem);
      } else if (response.status === 401) {
        console.error('❌ Token Inválido:', error.mensagem);
        localStorage.removeItem('token');
      }
      
      throw new Error(error.mensagem);
    }

    return await response.json();
  } catch (error) {
    console.error('❌ Erro na requisição:', error.message);
  }
}
```

### Exemplo de uso

```javascript
// Login
await login('teste@gmail.com', 'niggasForever');

// Acessar recurso protegido
const users = await fetchProtectedResource('/director/usuarios');
console.log('Usuários:', users);
```

---

## 📦 Exemplos Axios

### Setup com interceptor

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// ✅ Interceptor para adicionar token automaticamente
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ❌ Interceptor para tratamento de erro
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.error('❌ Token expirado. Fazendo logout...');
      localStorage.removeItem('token');
      // Redirecionar para login
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Login com Axios

```javascript
async function login(email, password) {
  try {
    const response = await api.post('/auth/login', {
      email,
      password
    });
    
    localStorage.setItem('token', response.data.token);
    console.log('✅ Login bem-sucedido!');
    return response.data.token;
  } catch (error) {
    console.error('❌ Erro no login:', error.response?.data?.mensagem);
  }
}
```

### Requisição protegida com Axios

```javascript
async function getUsers() {
  try {
    const response = await api.get('/director/usuarios');
    console.log('✅ Usuários obtidos:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Erro:', error.response?.data?.mensagem);
  }
}
```

---

## ⚠️ Tratamento de Erros

### Códigos de Status

| Código | Erro | Significado | O que fazer |
|--------|------|-------------|-----------|
| **200** | - | Sucesso | Continue a usar o token |
| **401** | UNAUTHORIZED | Token inválido/expirado | Faça login novamente |
| **403** | ACCESS_DENIED | Role insuficiente | Token pertence a outro usuário/role |
| **404** | NOT_FOUND | Recurso não encontrado | Verifique o endpoint |
| **422** | UNPROCESSABLE_ENTITY | Dados inválidos | Verifique os campos enviados |
| **500** | INTERNAL_SERVER_ERROR | Erro no servidor | Tente novamente mais tarde |

### Exemplo completo de tratamento

```javascript
async function handleRequest(method, endpoint, data = null) {
  try {
    const token = localStorage.getItem('token');
    
    if (!token && endpoint !== '/auth/login') {
      throw new Error('Não autenticado. Faça login primeiro.');
    }

    const options = {
      method,
      headers: {
        'Content-Type': 'application/json'
      }
    };

    if (token) {
      options.headers['Authorization'] = `Bearer ${token}`;
    }

    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(`http://localhost:8080${endpoint}`, options);
    const responseData = await response.json();

    if (!response.ok) {
      switch (response.status) {
        case 401:
          console.error('🔓 Sessão expirada. Redirecionando para login...');
          localStorage.removeItem('token');
          // window.location.href = '/login';
          break;
        case 403:
          console.error('🔒 Você não tem permissão para acessar este recurso.');
          break;
        case 404:
          console.error('❌ Recurso não encontrado.');
          break;
        case 422:
          console.error('⚠️ Dados inválidos:', responseData);
          break;
        default:
          console.error('❌ Erro:', responseData.mensagem);
      }
      throw new Error(responseData.mensagem);
    }

    return responseData;
  } catch (error) {
    console.error('Erro na requisição:', error.message);
    throw error;
  }
}
```

---

## 🔒 Endpoints Protegidos

### Públicos (sem token necessário)

```
POST   /auth/login         - Fazer login
POST   /auth/register      - Criar novo usuário (requer role DIRECTOR)
GET    /actuator/health    - Health check
```

### Protegidos por Role

```
/director/**     - Apenas usuários com role DIRECTOR
/manager/**      - Apenas usuários com role MANAGER  
/operational/**  - Apenas usuários com role OPERATIONAL
```

### Exemplo de estrutura esperada

```
GET  /director/usuarios
GET  /manager/produtos
GET  /operational/estoque
POST /director/criar-usuario
PUT  /manager/atualizar-produto
DELETE /director/remover-usuario
```

---

## 🛠️ Troubleshooting

### Problema: "Token inválido"

**Causa:** Token está expirado ou corrompido

**Solução:**
```javascript
// Limpar e fazer login novamente
localStorage.removeItem('token');
await login(email, password);
```

### Problema: "Acesso Negado"

**Causa:** Sua role não tem permissão para este endpoint

**Solução:**
```javascript
// Verificar role do usuário
const token = localStorage.getItem('token');
// Decodificar token (use jwt-decode library)
// import jwtDecode from 'jwt-decode';
// const decoded = jwtDecode(token);
// console.log('Sua role:', decoded.authorities);
```

### Problema: "Objeto está vazio"

**Causa:** Token não está sendo enviado corretamente

**Solução:**
```javascript
// Verificar formato do header
const token = localStorage.getItem('token');
console.log('Token:', token);
console.log('Header:', `Bearer ${token}`);
```

---

## 📝 Resumo Rápido

1. **Login:** `POST /auth/login` com email e password
2. **Receber token** e salvar em `localStorage`
3. **Usar em requisições:** Header `Authorization: Bearer {token}`
4. **Token expira em:** 24 horas (86400000ms)
5. **Se expirar:** Faça login novamente
6. **Se erro 403:** Verifique sua role

---

## 🚀 Próximos Passos

Agora você está pronto para:

- ✅ Fazer login
- ✅ Usar o token em requisições
- ✅ Lidar com erros corretamente
- ✅ Integrar com seu front-end

**Bora pimp like Slick Back!** 🎯
