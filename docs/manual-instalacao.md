# Manual de Instalação — Sistema Fawkes

Este documento descreve o passo a passo para configurar e executar o sistema Fawkes.

---

## 1. Pré-requisitos

| Ferramenta | Versão Mínima |
|-----------|--------------|
| Java Development Kit (JDK) | 21 |
| Apache Maven | 3.9+ |
| MySQL Server | 8.0+ |
| Docker Desktop | Latest |
| Git | 2.40+ |

---

## 2. Clonando o Repositório

```bash
git clone https://github.com/Fawkes-API-2026-01/API-2026.01.git
cd API-2026.01
```

---

## 3. Configurando o Banco de Dados

### 3.1 Usando Docker Compose (Recomendado)

O projeto contém um arquivo `compose.yaml` na pasta `api/`:

```bash
cd api
docker compose up -d
```

Isso irá subir um container MySQL na porta 3367.

### 3.2 Configuração Manual

Se preferir usar uma instância local do MySQL:

1. Crie o banco de dados:
```sql
CREATE DATABASE FAWKES;
```

2. Crie o usuário (ou ajuste o arquivo de configuração):
```sql
CREATE USER 'james'@'%' IDENTIFIED BY 'EFN2026';
GRANT ALL PRIVILEGES ON FAWKES.* TO 'james'@'%';
FLUSH PRIVILEGES;
```

---

## 4. Configurando Variáveis de Ambiente

O projeto utiliza a biblioteca java-dotenv. Crie um arquivo `.env` na raiz do projeto `api/`:

```env
# Banco de Dados
DB_URL=jdbc:mysql://localhost:3367/FAWKES?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=james
DB_PASSWORD=EFN2026
DB_DRIVER=com.mysql.cj.jdbc.Driver

# Aplicação
SERVER_PORT=8080

# JWT
JWT_SECRET=FawkesSecretKey2026NeweLogAPI12345678901234567890
JWT_EXPIRATION=86400000
```

---

## 5. Compilando e Executando o Backend (API)

### 5.1 Compilando

```bash
cd api
./mvnw clean package -DskipTests
```

### 5.2 Executando

```bash
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## 6. Compilando e Executando o Frontend (Desktop)

### 6.1 Compilando

```bash
cd ../front
./mvnw clean package
```

### 6.2 Executando

```bash
./mvnw javafx:run
```

A aplicação desktop será aberta.

---

## 7. Credenciais de Acesso (Usuários Criados por Padrão)

O sistema cria automaticamente os seguintes usuários ao iniciar:

| Perfil | Usuário | Senha |
|-------|--------|------|
| Gerente | admin | admin123 |
| Operacional | oper1 | oper123 |
| Diretor | dir1 | dir123 |

---

## 8. Estrura do Projeto

```
API-2026.01/
├── api/                    # Backend (Spring Boot)
│   ├── src/main/java/     # Código fonte
│   ├── pom.xml           # Dependências Maven
│   ├── compose.yaml      # Docker Compose (MySQL)
│   └── .env            # Variáveis de ambiente
├── front/                   # Frontend (JavaFX)
│   ├── src/            # Código fonte
│   └── pom.xml        # Dependências Maven
├── docs/                   # Documentação
│   ├── dod&dor/       # User Stories (artefatos)
│   ├── sprints/       # Backlogs das Sprints
│   └── product-backlog.md
└── README.md           # Visão Geral do Projeto
```

---

## 9. Solução de Problemas

### Erro de Conexão com Banco de Dados

- Verifique se o MySQL está rodando: `docker ps`
- Confirme as credenciais no arquivo `.env`
- Teste a conexão: `mysql -h localhost -P 3367 -u james -pFAWKES`

### Erro de Porta em Uso

- Altere a porta no arquivo `application.properties` ou variables de ambiente

### Erro de Compilação Java 21

- Certifique-se que o JAVA_HOME aponta para JDK 21:
```bash
echo $JAVA_HOME
java -version
```

---

## 10. Próximos Passos

Após a instalação, consulte o [Manual de Usuário](./manual-usuario.md) para aprender a utilizar o sistema.