# Design: Correção dos CRUDs — Segurança e Erros Funcionais

**Data:** 2026-05-31
**Scope:** Backend (Spring Boot)
**Origem:** Auditoria geral dos CRUDs (Produtos, Fornecedores, Usuários, Estoque, Movimentos)

---

## Problema

A auditoria dos CRUDs revelou um padrão de fundo e bugs concretos que fazem operações
legítimas falharem como erro 500, além de duas falhas de segurança. Os services de
`Product`, `Supplier` e `User` lançam `RuntimeException` crua (mapeada para 500 genérico),
enquanto o `PurchaseOrderService` já usa exceções de domínio corretas. Este design corrige
os 7 itens classificados como graves; consistência menor e código morto (itens 8–10 da
auditoria) ficam fora deste escopo.

## Escopo

Itens 1–7 da auditoria. **Fora do escopo:** DTO/validação em `SupplierController`,
limpeza de código morto em `UserService`, validação de campos obrigatórios nos `*Request`,
correção do `pom.xml` (artefatos de teste inexistentes) e testes automatizados.

## Decisões

- **Perfis de usuário:** criar/editar/ativar usuário = `DIRECTOR`; listar usuários =
  `DIRECTOR` + `MANAGER`; `GET /api/users/me` permanece para qualquer autenticado.
- **FK em delete (item 5):** abordagem reativa via `@ExceptionHandler` global, em vez de
  checagens proativas em múltiplos repositórios.
- **Verificação:** não há JDK no ambiente de desenvolvimento atual; cada correção inclui
  um passo de verificação manual (compilar + testar endpoint) a ser executado pelo usuário.

---

## Correções

### 1. Vazamento de senha nas respostas de Usuário
**Problema:** `Users.password` não tem proteção de serialização e
`POST/PUT/PATCH /api/users` retornam a entidade `Users` crua → o hash bcrypt vai no JSON.

**Solução (defesa em profundidade):**
- `Users.java`: anotar `password` com `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`.
- `UserController.java`: `create`, `update`, `toggleStatus` retornam `UserDTO`
  (`UserDTO.fromEntity(...)`) em vez de `Users`.

**Arquivos:** `Entities/Users.java`, `Controllers/UserController.java`.

### 2. Escalonamento de privilégio via `/api/users`
**Problema:** apenas `/api/auth/register` é restrito a `DIRECTOR`. `/api/users/**` é só
`authenticated()`, permitindo que qualquer usuário crie contas `DIRECTOR`.

**Solução:** no `SecurityConfig`, adicionar matchers (padrões específicos para não capturar
`/me`):
- `POST /api/users` → `hasRole("DIRECTOR")`
- `PUT /api/users/*` → `hasRole("DIRECTOR")`
- `PATCH /api/users/*/status` → `hasRole("DIRECTOR")`
- `GET /api/users` → `hasAnyRole("DIRECTOR","MANAGER")`
- `GET /api/users/me` → permanece `authenticated()`

**Arquivos:** `Security/SecurityConfig.java`.

### 3. Erros 500 genéricos → status HTTP corretos
**Problema:** `ProductService`, `SupplierService`, `UserService` lançam `RuntimeException`,
mapeada para 500 "erro interno" — esconde validações legítimas.

**Solução:** substituir por exceções de domínio:
- "não encontrado" → `RecursoNaoEncontradoException` (404)
- regra de negócio (ex.: "Email já cadastrado", role/unidade inválida) → `RegraDeNegocioException` (422)

**Arquivos:** `Services/ProductService.java`, `Services/SupplierService.java`,
`Services/UserService.java`.

### 4. Criar usuário duplica departamentos
**Problema:** `insertUserSimple` sempre cria um `Department` novo, mesmo se já existir um
com o mesmo nome.

**Solução:** reusar via
`departmentRepository.findByDepartamentName(nome).orElseGet(() -> criarNovo)`, como o
`update(UserUpdateRequest)` já faz.

**Arquivos:** `Services/UserService.java`.

### 5. Deletar produto quebra por violação de FK
**Problema:** `ProductService.delete` limpa stock relacionado, mas referências em
`PurchaseOrderItem`, `ProductInputs`, `ProductOutputs` causam violação de FK → 500.

**Solução (reativa):** adicionar `@ExceptionHandler(DataIntegrityViolationException.class)`
no `GlobalExceptionHandler` retornando **409 CONFLICT** com mensagem amigável
("Não é possível excluir/alterar: registro vinculado a pedidos ou movimentações").
Mantém a limpeza manual existente.

**Arquivos:** `Exceptions/GlobalExceptionHandler.java`.

### 6. `updateLimits` zera o estoque máximo
**Problema:** `ProductStockController.updateLimits` usa `getOrDefault(..., 0)`; atualizar só
o mínimo zera o máximo, fazendo toda entrada futura falhar.

**Solução:** mover o default para `ProductStockService.updateLimits` — carregar o
`ProductStock` atual e preservar o valor existente de `min`/`max` quando o campo não vier
no body (passar `Integer` possivelmente `null` do controller e tratar no service).

**Arquivos:** `Controllers/ProductStockController.java`, `Services/ProductStockService.java`.

### 7. `registerOutput` ignora o `orderId`
**Problema:** o controller recebe `orderId` mas passa `null` ao service; o vínculo da saída
com o `Ticket` nunca é gravado.

**Solução:** injetar `TicketRepository` no `StockMovementController` (ou no service); se
`orderId != null`, buscar o `Ticket` (404 via `RecursoNaoEncontradoException` se não achar)
e passar ao `registerOutput`; senão `null`.

**Arquivos:** `Controllers/StockMovementController.java` (e injeção de `TicketRepository`).

---

## Verificação manual (a executar pelo usuário)

1. `cd api && ./mvnw.cmd compile` — confirmar compilação.
2. Subir a API (`./mvnw.cmd spring-boot:run`).
3. Item 2: logar como OPERATIONAL → `POST /api/users` deve retornar **403**.
4. Item 1: `POST/PUT /api/users` não deve conter `password` no JSON de resposta.
5. Item 3: criar usuário com email duplicado → **422** com mensagem clara (não 500).
6. Item 5: deletar um produto vinculado a um pedido → **409** com mensagem amigável.
7. Item 6: `PATCH /api/product-stock/{id}/limits` só com `minStockQuantity` → o
   `maxStockQuantity` permanece o valor anterior.

## Fora do escopo (registrado)

- Itens 8–10 da auditoria (DTO de Supplier, código morto, validação de `*Request`).
- Correção do `pom.xml` e testes automatizados (destrava o DoD de testes da US9).
- CA2/CA5/CA6 da US9 (timeline, dashboard de KPIs, indicador de urgência).
