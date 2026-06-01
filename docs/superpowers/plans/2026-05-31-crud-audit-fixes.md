# Correção dos CRUDs (Segurança e Erros Funcionais) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Corrigir os 7 itens graves da auditoria dos CRUDs — 2 falhas de segurança e 5 bugs funcionais — no backend Spring Boot.

**Architecture:** Mudanças pontuais em controllers, services, entidade `Users`, `SecurityConfig` e `GlobalExceptionHandler`. Sem novas tabelas nem migrações. A correção de erro tem duas frentes: trocar `RuntimeException` por exceções de domínio nos services E fazer o handler repassar a mensagem da exceção.

**Tech Stack:** Java 17, Spring Boot 3, Spring Security, Jakarta Persistence, Jackson, Lombok.

> **Nota sobre verificação:** Não há JDK no ambiente atual e a infra de testes do projeto está quebrada (artefatos `spring-boot-starter-webmvc-test`/`spring-boot-starter-restclient-test` no `pom.xml` não existem) — corrigir isso está **fora do escopo**. Portanto cada task usa **verificação manual** (compilar + testar endpoint), a ser executada pelo usuário no ambiente dele. Não há passos de teste automatizado.

**Spec:** `docs/superpowers/specs/2026-05-31-crud-audit-fixes-design.md`

---

## Mapa de Arquivos

| Arquivo | Ação | Item |
|---|---|---|
| `Entities/Users.java` | Modificar — `@JsonProperty(WRITE_ONLY)` em `password` | 1 |
| `Controllers/UserController.java` | Modificar — retornar `UserDTO` nas mutações | 1 |
| `Security/SecurityConfig.java` | Modificar — gatear `/api/users` | 2 |
| `Exceptions/GlobalExceptionHandler.java` | Modificar — repassar mensagem + handler de FK | 3, 5 |
| `Services/ProductService.java` | Modificar — exceções de domínio | 3 |
| `Services/SupplierService.java` | Modificar — exceções de domínio | 3 |
| `Services/UserService.java` | Modificar — exceções de domínio + reuso de departamento | 3, 4 |
| `Controllers/ProductStockController.java` | Modificar — passar valores nuláveis | 6 |
| `Services/ProductStockService.java` | Modificar — preservar valor existente | 6 |
| `Controllers/StockMovementController.java` | Modificar — ligar `orderId` ao `Ticket` | 7 |

---

## Task 1: Fechar vazamento de senha (item 1)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Entities/Users.java`
- Modify: `api/src/main/java/com/fawkes/api/Controllers/UserController.java`

- [ ] **Step 1: Anotar `password` como write-only em `Users.java`**

Adicionar o import no topo (após os imports de `lombok`):
```java
import com.fasterxml.jackson.annotation.JsonProperty;
```

Alterar o campo `password` (linhas 43-44) de:
```java
    @Column(name = "password")
    private String password;
```
para:
```java
    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
```

- [ ] **Step 2: Retornar `UserDTO` nas mutações de `UserController.java`**

Substituir os três métodos `create`, `update`, `toggleStatus` (linhas 31-51) por:
```java
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody SignUpRequest request) {
        Users newUser = userService.insertUserSimple(
                request.getUserName(),
                request.getUserMail(),
                request.getPassword(),
                request.getRole().name(),
                request.getDepartamentName()
        );
        return ResponseEntity.ok(UserDTO.fromEntity(newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(UserDTO.fromEntity(userService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(UserDTO.fromEntity(userService.toggleStatus(id)));
    }
```
(`UserDTO` e `Users` já estão importados no arquivo.)

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Verificação manual (no ambiente do usuário, API no ar)**

Logar como DIRECTOR, criar um usuário via `POST /api/users` e confirmar que o JSON de resposta **não contém** o campo `password`.

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Entities/Users.java api/src/main/java/com/fawkes/api/Controllers/UserController.java
git commit -m "fix: remove vazamento de senha nas respostas de usuario"
```

---

## Task 2: Autorização em `/api/users` (item 2)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Security/SecurityConfig.java`

- [ ] **Step 1: Adicionar matchers de `/api/users`**

Em `SecurityConfig.java`, localizar o bloco de `purchase-orders` (que termina nos matchers PUT adicionados anteriormente) e, **logo antes** de `.anyRequest().authenticated()`, inserir:
```java
                        // Gestão de usuários: criar/editar/ativar = DIRECTOR
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/status").hasRole("DIRECTOR")
                        // Listagem de usuários: diretor ou gerente (note: /api/users/me continua liberado)
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("DIRECTOR", "MANAGER")
```

> O padrão `"/api/users"` casa apenas a rota exata, então `GET /api/users/me` cai em `.anyRequest().authenticated()` e permanece acessível a qualquer autenticado.

- [ ] **Step 2: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Verificação manual**

Logar como OPERATIONAL e chamar `POST /api/users` → esperar **403**. Logar como DIRECTOR e chamar o mesmo → esperar **200**. `GET /api/users/me` como OPERATIONAL → esperar **200**.

- [ ] **Step 4: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Security/SecurityConfig.java
git commit -m "fix: restringe gestao de usuarios a DIRECTOR e listagem a DIRECTOR/MANAGER"
```

---

## Task 3: Handler repassa mensagem + trata violação de FK (itens 3 e 5)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Exceptions/GlobalExceptionHandler.java`

- [ ] **Step 1: Repassar a mensagem das exceções de domínio**

Em `GlobalExceptionHandler.java`, substituir os métodos `handleRecursoNaoEncontrado` (linhas 26-29) e `handleRegraDeNegocio` (linhas 46-49) por:
```java
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage() : errorMessages.getResourceNotFound();
        return buildResponse(404, "NOT_FOUND", msg);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocio(RegraDeNegocioException ex) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage() : errorMessages.getBusinessRuleError();
        return buildResponse(422, "UNPROCESSABLE_ENTITY", msg);
    }
```

- [ ] **Step 2: Adicionar handler de `DataIntegrityViolationException`**

Adicionar o import no topo do arquivo (junto aos outros imports):
```java
import org.springframework.dao.DataIntegrityViolationException;
```

Adicionar o método novo antes do `handleRuntimeException` (linha 51):
```java
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(409, "CONFLICT",
                "Não é possível concluir a operação: este registro está vinculado a pedidos ou movimentações.");
    }
```

> Importante: este handler deve ficar **antes** de `handleRuntimeException` e `handleException` na ordem do arquivo, mas o Spring resolve por tipo mais específico independente da ordem — manter junto dos demais por legibilidade.

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Exceptions/GlobalExceptionHandler.java
git commit -m "fix: handler repassa mensagem de dominio e trata violacao de FK como 409"
```

---

## Task 4: Exceções de domínio em `ProductService` (item 3)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/ProductService.java`

- [ ] **Step 1: Adicionar import**

No topo de `ProductService.java`, adicionar:
```java
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
```

- [ ] **Step 2: Trocar os `RuntimeException` de "não encontrado"**

Substituir as 5 ocorrências de `new RuntimeException(...)` por `new RecursoNaoEncontradoException(...)` mantendo a mesma mensagem:

- `create`: `"Fornecedor não encontrado"` (linha 34) e `"Estoque não encontrado"` (linha 37)
- `delete`: `"Produto não encontrado: " + id` (linha 61)
- `update`: `"Produto não encontrado: " + id` (linha 78) e `"Fornecedor não encontrado"` (linha 97)

Exemplo (aplicar o mesmo padrão a todas):
```java
        Suppliers supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
```

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Verificação manual**

`POST /api/products` com `supplierId` inexistente → esperar **404** com mensagem `"Fornecedor não encontrado"` (não 500).

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Services/ProductService.java
git commit -m "fix: ProductService usa RecursoNaoEncontradoException (404) em vez de 500"
```

---

## Task 5: Exceções de domínio em `SupplierService` (item 3)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/SupplierService.java`

- [ ] **Step 1: Adicionar import**

No topo de `SupplierService.java`, adicionar:
```java
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
```

- [ ] **Step 2: Trocar o `RuntimeException` em `findById`**

Em `findById` (linha 21-22), substituir:
```java
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
```

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Verificação manual**

`PUT /api/suppliers/{id}` com id inexistente → esperar **404** (não 500).

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Services/SupplierService.java
git commit -m "fix: SupplierService usa RecursoNaoEncontradoException (404)"
```

---

## Task 6: `UserService` — exceções de domínio + reuso de departamento (itens 3 e 4)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/UserService.java`

- [ ] **Step 1: Adicionar imports**

No topo de `UserService.java`, adicionar:
```java
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
```

- [ ] **Step 2: Corrigir `insertUserSimple` — exceções + reuso de departamento (itens 3 e 4)**

Substituir o corpo de `insertUserSimple` (linhas 77-110) por:
```java
    @Transactional
    public Users insertUserSimple(String userName, String userMail, String password,
                                   String roleName, String departamentName) {
        if (findExistentMail(userMail))
            throw new RegraDeNegocioException("Este email já foi cadastrado");
        if (findExistentName(userName))
            throw new RegraDeNegocioException("Este nome de usuário já foi cadastrado");

        Roles role;
        try {
            role = Roles.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Role inválida. Use: DIRECTOR, MANAGER ou OPERATIONAL");
        }

        Group group = groupRepository.findByRole(role)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo não encontrado para a role: " + role));

        Department dept = departmentRepository.findByDepartamentName(departamentName)
                .orElseGet(() -> {
                    Department novo = new Department();
                    novo.setDepartamentName(departamentName);
                    novo.setText("Departamento " + departamentName);
                    return departmentRepository.save(novo);
                });

        Users user = new Users();
        user.setUserName(userName);
        user.setUserMail(userMail);
        user.setPassword(passwordEncoder.encode(password));
        user.setGroup(group);
        user.setDepartments(dept);
        user.setIsActive(true);
        user.setRoles(Set.of(role));

        return userRepository.save(user);
    }
```

- [ ] **Step 3: Corrigir `findById` (item 3)**

Substituir `findById` (linhas 146-149) por:
```java
    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
```

- [ ] **Step 4: Corrigir as exceções no `update(UserUpdateRequest)` (item 3)**

No método `update(Long id, UserUpdateRequest request)` (linhas 184-218), trocar as duas `RuntimeException`:
- `"Grupo não encontrado para a role: " + role` → `new RecursoNaoEncontradoException(...)`
- `"Role inválida. Use: DIRECTOR, MANAGER ou OPERATIONAL"` → `new RegraDeNegocioException(...)`

Trecho resultante:
```java
        if (request.getRoleName() != null) {
            try {
                Roles role = Roles.valueOf(request.getRoleName());
                Group group = groupRepository.findByRole(role)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo não encontrado para a role: " + role));
                user.setGroup(group);
                user.setRoles(Set.of(role));
            } catch (IllegalArgumentException e) {
                throw new RegraDeNegocioException("Role inválida. Use: DIRECTOR, MANAGER ou OPERATIONAL");
            }
        }
```
> Atenção: manter o `catch (IllegalArgumentException e)` — ele captura o `Roles.valueOf` inválido. `RegraDeNegocioException` não é `IllegalArgumentException`, então o `throw` dentro do `try` não é recapturado.

- [ ] **Step 5: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Verificação manual**

1. `POST /api/users` (como DIRECTOR) com email já existente → esperar **422** com `"Este email já foi cadastrado"`.
2. Criar dois usuários com o mesmo `departamentName` → confirmar no banco que existe **apenas um** registro em `Department` com aquele nome.

- [ ] **Step 7: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Services/UserService.java
git commit -m "fix: UserService usa excecoes de dominio e reusa departamento existente"
```

---

## Task 7: `updateLimits` preserva valor existente (item 6)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Controllers/ProductStockController.java`
- Modify: `api/src/main/java/com/fawkes/api/Services/ProductStockService.java`

- [ ] **Step 1: Passar valores nuláveis no controller**

Em `ProductStockController.java`, substituir o corpo de `updateLimits` (linhas 19-28) por:
```java
    @PatchMapping("/{productId}/limits")
    public ResponseEntity<ProductStock> updateLimits(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {

        Integer min = body.get("minStockQuantity");
        Integer max = body.get("maxStockQuantity");

        return ResponseEntity.ok(productStockService.updateLimits(productId, min, max));
    }
```

- [ ] **Step 2: Preservar valores existentes no service**

Em `ProductStockService.java`, adicionar o import:
```java
import com.fawkes.api.Exceptions.RegraDeNegocioException;
```

Substituir o bloco de validação/atribuição em `updateLimits` (linhas 35-42) por:
```java
        int effectiveMin = (min != null) ? min
                : (ps.getMinStockQuantity() != null ? ps.getMinStockQuantity() : 0);
        int effectiveMax = (max != null) ? max
                : (ps.getMaxStockQuantity() != null ? ps.getMaxStockQuantity() : 0);

        if (effectiveMin < 0 || effectiveMax < 0)
            throw new RegraDeNegocioException("Quantidades não podem ser negativas.");
        if (effectiveMax > 0 && effectiveMin > effectiveMax)
            throw new RegraDeNegocioException("Mínimo não pode ser maior que o máximo.");

        ps.setMinStockQuantity(effectiveMin);
        ps.setMaxStockQuantity(effectiveMax);
        return productStockRepository.save(ps);
```

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Verificação manual**

`PATCH /api/product-stock/{id}/limits` com `{"maxStockQuantity": 100}` e depois com `{"minStockQuantity": 10}` → confirmar que o `maxStockQuantity` permanece **100** (não volta a 0).

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Controllers/ProductStockController.java api/src/main/java/com/fawkes/api/Services/ProductStockService.java
git commit -m "fix: updateLimits preserva min/max existente quando campo ausente"
```

---

## Task 8: Ligar `orderId` ao `Ticket` em `registerOutput` (item 7)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Controllers/StockMovementController.java`

- [ ] **Step 1: Verificar `TicketRepository`**

Confirmar que `api/src/main/java/com/fawkes/api/Repositories/TicketRepository.java` existe e estende `JpaRepository<Ticket, Long>` (fornece `findById(Long)`). Se a entidade `Ticket` usar outro tipo de id, ajustar o `orElseThrow` de acordo.

- [ ] **Step 2: Injetar `TicketRepository` e usar o `orderId`**

Em `StockMovementController.java`, adicionar os imports:
```java
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Repositories.TicketRepository;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
```

Adicionar o campo injetado (o controller usa `@RequiredArgsConstructor`, então basta um `final`), logo após `private final StockMovementService stockMovementService;`:
```java
    private final TicketRepository ticketRepository;
```

Substituir o método `registerOutput` (linhas 48-56) por:
```java
    @PostMapping("/output")
    public ResponseEntity<ProductOutputs> registerOutput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long orderId) {
        Ticket ticket = null;
        if (orderId != null) {
            ticket = ticketRepository.findById(orderId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado: " + orderId));
        }
        ProductOutputs output = stockMovementService.registerOutput(stockId, productId, quantity, ticket);
        return ResponseEntity.ok(output);
    }
```

- [ ] **Step 3: Verificar compilação**

Run: `cd api && ./mvnw.cmd compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Verificação manual**

`POST /api/stock/movements/output?stockId=1&productId=1&quantity=1&orderId=<id_valido>` → confirmar que a saída gravada tem `order` preenchido. Sem `orderId` → continua funcionando com `order` nulo.

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Controllers/StockMovementController.java
git commit -m "fix: registerOutput vincula a saida ao Ticket quando orderId informado"
```

---

## Verificação final

- [ ] **Step 1: Compilar tudo**

Run: `cd api && ./mvnw.cmd clean compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 2: Subir a API e rodar os checks manuais dos itens 1–7** (resumidos na seção "Verificação manual" do spec).

- [ ] **Step 3: Confirmar que nenhuma resposta de usuário expõe `password`** e que OPERATIONAL recebe 403 em `POST /api/users`.
