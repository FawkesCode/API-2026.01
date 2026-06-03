# Correção do fluxo de Pedidos de Compra (US9) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Corrigir 7 defeitos do fluxo de pedidos (observação não persistida, papéis errados, preço na abertura, "marcar como comprado" com data de entrega, filtros faltando, estilo da cotação), preservando a observação do solicitante separada da justificativa do diretor.

**Architecture:** Backend Spring Boot (MySQL, `ddl-auto=update`) — novo campo `decisionReason` no `PurchaseOrder`, separado de `notes`; transições `/confirm` e `/cancel` passam a aceitar payload e exigem papel DIRECTOR via `SecurityConfig`. Front JavaFX/FXML — abertura passa a persistir a observação e enviar preço 0; aprovação/negação enviam justificativa; "Marcar como Comprado" captura data de entrega (default +3 dias); filtros novos e ajuste de estilo da cotação.

**Tech Stack:** Java 21, Spring Boot 4.0.4, Spring Security, JPA/MySQL, JUnit 5 + Mockito 5.20 (testes de service); JavaFX + JFoenix, Jackson (front).

**Spec:** `docs/superpowers/specs/2026-06-01-pedidos-fix-flow-design.md`

---

## Notas de teste e ambiente (ler antes de começar)

- **Testes de service (backend):** usam Mockito puro (`@ExtendWith(MockitoExtension.class)`), **sem** contexto Spring e **sem** banco. Rodam com:
  `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
  A primeira execução baixa os JARs de teste (precisa de rede uma vez).
- **`SecurityConfig` (papéis por URL):** não há `spring-security-test` no projeto; a verificação é **manual** via `curl` com tokens de MANAGER e DIRECTOR (esperando 403/200). Não inventar infra de teste de segurança.
- **Front (JavaFX):** não há infra de teste de UI. A verificação automatizada é `mvn -f front/pom.xml -q -DskipTests compile` (compila) + verificação **manual** rodando o app (precisa de MySQL + a API no ar).
- **`ddl-auto=update`** cria a coluna `decision_reason` automaticamente no primeiro start da API após a Task 1. Não há scripts de migração (Flyway/Liquibase) no projeto.
- Não usar `cd` nos comandos (usar `-f <pom>`), para evitar prompts de permissão.

---

## File Structure

**API (modificar):**
- `api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java` — campo `decisionReason`.
- `api/src/main/java/com/fawkes/api/DTOs/Request/ConfirmOrderRequest.java` — **criar**.
- `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java` — `confirmOrder`, `cancelOrder`, `markAsProblem`.
- `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java` — payloads `/confirm` e `/cancel`.
- `api/src/main/java/com/fawkes/api/Security/SecurityConfig.java` — DIRECTOR em confirm/cancel.
- `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java` — **criar**.

**Front (modificar):**
- `controller/NewRequestForm.java` — observação (PUT) + `unitPrice` 0.
- `controller/ShoppingRequestForm.java` + `components/ProductShopCard.java` — remover preço.
- `controller/AproveRequestForm.java`, `controller/DeclineRequestForm.java` — reason + DatePicker.
- `view/forms/director-request-form.fxml` — DatePicker.
- `controller/PendingRequestForm.java` — papéis + exibir `decisionReason`.
- `models/Order.java` — campo `decisionReason`.
- `controller/OrdersPageController.java` + `view/orders-page.fxml` — filtros overdue/problem.
- `controller/QuoteRequestForm.java` + `view/forms/quote-request-form.fxml` — estilo.

---

## Task 1: Campo `decisionReason` + DTO `ConfirmOrderRequest`

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java:46-47`
- Create: `api/src/main/java/com/fawkes/api/DTOs/Request/ConfirmOrderRequest.java`

- [ ] **Step 1: Adicionar o campo `decisionReason` ao `PurchaseOrder`**

No `PurchaseOrder.java`, logo após o campo `notes` (linha 46-47):

```java
        @Column(name = "notes", columnDefinition = "TEXT")
        private String notes;

        @Column(name = "decision_reason", columnDefinition = "TEXT")
        private String decisionReason;
```

(A anotação `@Data` do Lombok gera getter/setter automaticamente.)

- [ ] **Step 2: Criar o DTO `ConfirmOrderRequest`**

```java
package com.fawkes.api.DTOs.Request;

import java.time.LocalDateTime;

public record ConfirmOrderRequest(
        LocalDateTime expectedDeliveryDate,
        String reason
) {}
```

- [ ] **Step 3: Compilar para garantir que está válido**

Run: `mvn -f api/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS (sem erros de compilação).

- [ ] **Step 4: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java api/src/main/java/com/fawkes/api/DTOs/Request/ConfirmOrderRequest.java
git commit -m "feat(api): campo decisionReason e DTO ConfirmOrderRequest"
```

---

## Task 2: `confirmOrder` persiste data de entrega + justificativa

**Files:**
- Create: `api/src/main/java/com/fawkes/api/test/.../PurchaseOrderServiceTest.java` → caminho real: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java:113-125`
- Modify: `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java:115-118`

- [ ] **Step 1: Escrever o teste que falha (cria a classe de teste)**

Criar `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`:

```java
package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Repositories.OrderNoteRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import com.fawkes.api.Repositories.PurchaseOrderRepository;
import com.fawkes.api.Repositories.SupplierRepository;
import com.fawkes.api.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock PurchaseOrderRepository purchaseOrderRepository;
    @Mock SupplierRepository supplierRepository;
    @Mock UserRepository userRepository;
    @Mock ProductsRepository productsRepository;
    @Mock StockMovementService stockMovementService;
    @Mock OrderNoteRepository orderNoteRepository;

    @InjectMocks PurchaseOrderService service;

    @Test
    void confirmOrder_persisteDataEntregaEJustificativa_eMarcaConfirmed() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LocalDateTime date = LocalDateTime.of(2026, 6, 4, 23, 59, 59);
        ConfirmOrderRequest req = new ConfirmOrderRequest(date, "Comprado no fornecedor X");

        PurchaseOrder result = service.confirmOrder(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.confirmed);
        assertThat(result.getExpectedDeliveryDate()).isEqualTo(date);
        assertThat(result.getDecisionReason()).isEqualTo("Comprado no fornecedor X");
    }

    @Test
    void confirmOrder_rejeitaQuandoNaoEstaQuoted() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.pending);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.confirmOrder(1L, new ConfirmOrderRequest(null, null)))
                .isInstanceOf(RegraDeNegocioException.class);
    }
}
```

- [ ] **Step 2: Rodar e confirmar que NÃO compila / falha**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: falha de compilação — `confirmOrder(long, ConfirmOrderRequest)` não existe (hoje é `confirmOrder(Long)`).

- [ ] **Step 3: Alterar `confirmOrder` no service para a nova assinatura**

Em `PurchaseOrderService.java`, substituir o método `confirmOrder` (linhas 113-125) por:

```java
    @Transactional
    public PurchaseOrder confirmOrder(Long orderId, ConfirmOrderRequest request) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (order.getStatus() != PurchaseOrder.Status.quoted) {
            throw new RegraDeNegocioException(
                    "Só é possível aprovar pedidos com cotação registrada (status 'Em Cotação')");
        }

        if (request != null) {
            if (request.expectedDeliveryDate() != null) {
                order.setExpectedDeliveryDate(request.expectedDeliveryDate());
            }
            if (request.reason() != null && !request.reason().isBlank()) {
                order.setDecisionReason(request.reason());
            }
        }

        order.setStatus(PurchaseOrder.Status.confirmed);
        return purchaseOrderRepository.save(order);
    }
```

Adicionar o import no topo do service (junto aos outros imports de DTO):

```java
import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
```

- [ ] **Step 4: Atualizar o controller `/confirm` (mesma task, para compilar)**

Em `PurchaseOrderController.java`, substituir o método `confirm` (linhas 115-118) por:

```java
    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrder> confirm(
            @PathVariable Long id,
            @RequestBody(required = false) ConfirmOrderRequest request) {
        return ResponseEntity.ok(purchaseOrderService.confirmOrder(id, request));
    }
```

Adicionar o import:

```java
import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
```

- [ ] **Step 5: Rodar o teste e confirmar que passa**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: PASS (2 testes verdes).

- [ ] **Step 6: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "feat(api): confirmOrder persiste expectedDeliveryDate e decisionReason"
```

---

## Task 3: `cancelOrder` persiste a justificativa

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java:184-195`
- Modify: `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java:132-135`
- Modify: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Adicionar teste que falha**

Adicionar este método à classe `PurchaseOrderServiceTest`:

```java
    @Test
    void cancelOrder_persisteReasonEmDecisionReason() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.cancelOrder(1L, "Fora do orçamento");

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.cancelled);
        assertThat(result.getDecisionReason()).isEqualTo("Fora do orçamento");
    }
```

- [ ] **Step 2: Rodar e confirmar falha de compilação**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: falha — `cancelOrder(long, String)` não existe (hoje é `cancelOrder(Long)`).

- [ ] **Step 3: Alterar `cancelOrder` no service**

Em `PurchaseOrderService.java`, substituir o método `cancelOrder` (linhas 184-195) por:

```java
    @Transactional
    public PurchaseOrder cancelOrder(Long orderId, String reason) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (order.getStatus() == PurchaseOrder.Status.received || order.getStatus() == PurchaseOrder.Status.cancelled) {
            throw new RegraDeNegocioException("Não é possível cancelar um pedido com status: " + order.getStatus());
        }

        if (reason != null && !reason.isBlank()) {
            order.setDecisionReason(reason);
        }
        order.setStatus(PurchaseOrder.Status.cancelled);
        return purchaseOrderRepository.save(order);
    }
```

- [ ] **Step 4: Atualizar o controller `/cancel`**

Em `PurchaseOrderController.java`, substituir o método `cancel` (linhas 132-135) por:

```java
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrder> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(purchaseOrderService.cancelOrder(id, reason));
    }
```

(`Map` já está importado no controller.)

- [ ] **Step 5: Rodar o teste e confirmar que passa**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: PASS (3 testes verdes).

- [ ] **Step 6: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "feat(api): cancelOrder persiste justificativa em decisionReason"
```

---

## Task 4: `markAsProblem` grava em `decisionReason` (preserva `notes`)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java:281-292`
- Modify: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Adicionar teste que falha**

Adicionar à classe `PurchaseOrderServiceTest`:

```java
    @Test
    void markAsProblem_gravaDecisionReason_ePreservaNotes() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.received);
        order.setNotes("Observação do solicitante");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.markAsProblem(1L, "Item veio quebrado");

        assertThat(result.getDecisionReason()).isEqualTo("Item veio quebrado");
        assertThat(result.getNotes()).isEqualTo("Observação do solicitante");
    }
```

- [ ] **Step 2: Rodar e confirmar que falha**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: FAIL — hoje `markAsProblem` faz `order.setNotes(reason)`, então `notes` vira "Item veio quebrado" e `decisionReason` fica null.

- [ ] **Step 3: Alterar `markAsProblem`**

Em `PurchaseOrderService.java`, na linha 290, trocar:

```java
        if (reason != null && !reason.isBlank()) order.setNotes(reason);
```

por:

```java
        if (reason != null && !reason.isBlank()) order.setDecisionReason(reason);
```

- [ ] **Step 4: Rodar o teste e confirmar que passa**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: PASS (4 testes verdes).

- [ ] **Step 5: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "fix(api): markAsProblem grava em decisionReason, preservando notes"
```

---

## Task 5: Travar comportamento do `addItem` com preço 0 (teste de caracterização)

**Files:**
- Modify: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

> O `addItem` já aceita preço 0 (não há checagem `> 0`). Este teste trava esse contrato para que ninguém o quebre ao mexer no item 2 do front.

- [ ] **Step 1: Adicionar o teste**

Adicionar à classe `PurchaseOrderServiceTest` (e o import `import com.fawkes.api.Entities.Products;`, `import java.math.BigDecimal;`, `import java.util.ArrayList;`, `import static org.mockito.Mockito.mock;` no topo):

```java
    @Test
    void addItem_aceitaPrecoZero() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.draft);
        order.setItems(new ArrayList<>());
        Products product = mock(Products.class);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productsRepository.findById(10L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.addItem(1L, 10L, 5, BigDecimal.ZERO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getUnitPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
```

- [ ] **Step 2: Rodar o teste e confirmar que passa (já é o comportamento atual)**

Run: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test`
Expected: PASS (5 testes verdes).

- [ ] **Step 3: Commit**

```bash
git add api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "test(api): trava aceitação de unitPrice 0 no addItem"
```

---

## Task 6: `SecurityConfig` — confirm/cancel só para DIRECTOR

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Security/SecurityConfig.java:52-57`

- [ ] **Step 1: Separar os matchers de confirm/cancel**

Em `SecurityConfig.java`, substituir o bloco (linhas 52-57):

```java
                        // Aprovação, negação e demais transições de estado: somente gerente/diretor
                        .requestMatchers(HttpMethod.POST,
                                "/api/purchase-orders/*/confirm",
                                "/api/purchase-orders/*/cancel",
                                "/api/purchase-orders/*/ship",
                                "/api/purchase-orders/*/problem",
                                "/api/purchase-orders/*/return").hasAnyRole("MANAGER", "DIRECTOR")
```

por:

```java
                        // Marcar como comprado (confirm) e negar (cancel): somente diretor
                        .requestMatchers(HttpMethod.POST,
                                "/api/purchase-orders/*/confirm",
                                "/api/purchase-orders/*/cancel").hasRole("DIRECTOR")
                        // Envio, problema e devolução: gerente ou diretor
                        .requestMatchers(HttpMethod.POST,
                                "/api/purchase-orders/*/ship",
                                "/api/purchase-orders/*/problem",
                                "/api/purchase-orders/*/return").hasAnyRole("MANAGER", "DIRECTOR")
```

- [ ] **Step 2: Compilar**

Run: `mvn -f api/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Verificação manual (precisa de MySQL + API no ar)**

Subir a API. Obter um token de um usuário MANAGER (login via `/api/auth/login`) e um de DIRECTOR. Com um pedido em status `quoted` (id `<ID>`):

```bash
# MANAGER tentando confirmar -> deve dar 403
curl -i -X POST http://localhost:8080/api/purchase-orders/<ID>/confirm \
  -H "Authorization: Bearer <TOKEN_MANAGER>" -H "Content-Type: application/json" -d '{}'
# Esperado: HTTP/1.1 403

# DIRECTOR confirmando -> deve dar 200
curl -i -X POST http://localhost:8080/api/purchase-orders/<ID>/confirm \
  -H "Authorization: Bearer <TOKEN_DIRECTOR>" -H "Content-Type: application/json" \
  -d '{"expectedDeliveryDate":"2026-06-10T23:59:59","reason":"ok"}'
# Esperado: HTTP/1.1 200
```

Expected: MANAGER → 403; DIRECTOR → 200.

- [ ] **Step 4: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Security/SecurityConfig.java
git commit -m "feat(api): confirm e cancel exigem papel DIRECTOR"
```

---

## Task 7: Front — persistir observação na abertura + preço 0

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/controller/NewRequestForm.java:113-150`

- [ ] **Step 1: Enviar a observação (PUT notes) e preço 0**

Primeiro, **capturar a observação na thread do JavaFX** (não dentro da `Task`, pois ler um controle fora da FX thread não é seguro). No início de `handleSubmmit()`, logo após o `Map<Integer, List<FormProducts>> productsPerSupplier = ...` (linha 114), adicionar:

```java
        final String observacao = descriptionField.getText() != null ? descriptionField.getText().trim() : "";
```

Depois, dentro do `Task<Void>.call()` (substituir o corpo do laço por fornecedor, linhas ~124-147) para: (a) gravar `notes` via PUT logo após criar o draft, (b) enviar `unitPrice: 0` em cada item:

```java
                String userId = UserInfoManager.getInstance().getUserId();
                ObjectMapper mapper = new ObjectMapper();

                for (Map.Entry<Integer, List<FormProducts>> entry : productsPerSupplier.entrySet()) {
                    int supplierId = entry.getKey();
                    List<FormProducts> supplierItems = entry.getValue();

                    ObjectNode draftNode = mapper.createObjectNode();
                    draftNode.put("supplierId", supplierId);
                    draftNode.put("userId", Long.parseLong(userId));

                    JsonNode draft = ApiClient.post("/api/purchase-orders/draft",
                            mapper.writeValueAsString(draftNode));
                    Long orderId = draft.path("id").asLong();

                    // Persiste a observação do solicitante enquanto o pedido ainda é draft
                    if (!observacao.isEmpty()) {
                        ObjectNode notesNode = mapper.createObjectNode();
                        notesNode.put("notes", observacao);
                        ApiClient.put("/api/purchase-orders/" + orderId,
                                mapper.writeValueAsString(notesNode));
                    }

                    for (FormProducts p : supplierItems) {
                        ObjectNode itemNode = mapper.createObjectNode();
                        itemNode.put("productId", p.getId());
                        itemNode.put("quantity", p.getQuantity());
                        itemNode.put("unitPrice", 0); // preço entra só na cotação
                        ApiClient.post("/api/purchase-orders/" + orderId + "/items",
                                mapper.writeValueAsString(itemNode));
                    }

                    ApiClient.post("/api/purchase-orders/" + orderId + "/submit", "{}");
                }
                return null;
```

- [ ] **Step 2: Compilar o front**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Verificação manual**

Abrir um pedido com texto na observação. No detalhe do pedido (modal), a observação deve aparecer (o front já lê `notes` como descrição). Conferir no banco/`GET /api/purchase-orders/{id}` que `notes` está preenchido e os itens têm `unitPrice = 0`.

- [ ] **Step 4: Commit**

```bash
git add front/src/main/java/com/fawkes/front/controller/NewRequestForm.java
git commit -m "fix(front): abertura persiste observacao e envia unitPrice 0"
```

---

## Task 8: Front — remover exibição de preço no carrinho

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/controller/ShoppingRequestForm.java:124-148`
- Modify: `front/src/main/java/com/fawkes/front/components/ProductShopCard.java:19,31-32`

- [ ] **Step 1: Remover o label de preço do carrinho**

Em `ShoppingRequestForm.renderProductsView()`, remover as linhas que criam/usam o label de preço (linhas 127-128 e 141). O bloco resultante:

```java
        for (FormProducts p : productsView) {
            Label qtd = new Label("(x " + p.getQuantity() + ")");
            qtd.getStyleClass().add("input__label--info");

            Label name = new Label(p.getName());
            name.getStyleClass().add("input__label--info");

            HBox productsLineContainer = new HBox(5);
            productsLineContainer.setAlignment(Pos.CENTER);

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            productsLineContainer.getChildren().addAll(name, spacer, qtd);
            totalQtd = totalQtd + p.getQuantity();

            productsViewContainer.getChildren().add(productsLineContainer);
        }
        requestTotalItens.setText("Qtd. de itens: " + totalQtd);
```

(O campo `totalPrice` continua existindo e sendo passado a `NewRequestForm.setData(...)`; agora vale 0. Não removê-lo para não quebrar a assinatura.)

- [ ] **Step 2: Remover o campo de preço não usado do `ProductShopCard`**

Em `ProductShopCard.java` remover a linha 19 (`@FXML private Label productPrice;`) e o bloco `CURRENCY` (linhas 31-32) — eles não têm `fx:id` correspondente no FXML nem são usados. Remover também o import `java.text.NumberFormat` e `java.util.Locale` se ficarem sem uso.

- [ ] **Step 3: Compilar**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Verificação manual**

Montar o carrinho e abrir a tela de revisão: nenhum valor em R$ aparece nas linhas de produto.

- [ ] **Step 5: Commit**

```bash
git add front/src/main/java/com/fawkes/front/controller/ShoppingRequestForm.java front/src/main/java/com/fawkes/front/components/ProductShopCard.java
git commit -m "fix(front): remove exibicao de preco no carrinho e card de produto"
```

---

## Task 9: Front — "Marcar como Comprado" com DatePicker + justificativa

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/forms/director-request-form.fxml:16-26`
- Modify: `front/src/main/java/com/fawkes/front/controller/AproveRequestForm.java`
- Modify: `front/src/main/java/com/fawkes/front/controller/DeclineRequestForm.java`

- [ ] **Step 1: Adicionar DatePicker ao FXML compartilhado**

Em `director-request-form.fxml`, dentro do `AnchorPane` que hoje contém o `noteTitle` + `descriptionField` (linhas 16-26), adicionar acima do `noteTitle` um label + DatePicker. Também adicionar o import `<?import javafx.scene.control.DatePicker?>` no topo. Bloco resultante do `AnchorPane`:

```xml
            <AnchorPane prefHeight="152.0" prefWidth="674.0">
               <children>
                  <Label fx:id="deliveryLabel" layoutX="10.0" layoutY="10.0" styleClass="input__label" text="Data de entrega prevista:" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="6.0" />
                  <DatePicker fx:id="deliveryDatePicker" layoutX="10.0" layoutY="30.0" prefWidth="220.0" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="28.0" />
                  <Label fx:id="noteTitle" styleClass="input__label" text="Gostaria de deixar uma observaçao?" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="62.0" />
                  <TextArea fx:id="descriptionField" prefHeight="60.0" promptText="Digite alguma observação ou justificativa para a sua escolha..." AnchorPane.bottomAnchor="10.0" AnchorPane.leftAnchor="10.0" AnchorPane.rightAnchor="10.0" AnchorPane.topAnchor="84.0">
                     <styleClass>
                        <String fx:value="input__text" />
                        <String fx:value="input__text--form" />
                     </styleClass>
                  </TextArea>
               </children>
            </AnchorPane>
```

- [ ] **Step 2: `AproveRequestForm` — rótulo, default +3 dias, enviar reason + data**

Substituir `AproveRequestForm.java` por (mantendo o pacote e o restante da lógica de `setData`):

```java
package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AproveRequestForm {
    @FXML private Label detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private Label deliveryLabel;
    @FXML private Label errorLabel;
    UserInfoManager loggedUser = UserInfoManager.getInstance();

    private Runnable onSaveSuccess;
    public void setOnSaveSuccess(Runnable onSaveSuccess) { this.onSaveSuccess = onSaveSuccess; }
    private Order order;

    public void initialize() {
        btnCommand.setText("Marcar como Comprado");
        deliveryDatePicker.setValue(LocalDate.now().plusDays(3));
    }

    public void setData(Order order) {
        StringBuilder productsList = new StringBuilder();
        for (RequestItem pro : order.getItemsList()) {
            productsList.append(pro.getProduct().getName());
            productsList.append(",");
        }
        if (productsList.length() > 2) {
            productsList.setLength(productsList.length() - 2);
        }

        String text = "Pedido de " + order.getQuantity() + " itens, sendo eles: " + productsList
                + "; Total de " + order.getTotalValue() + ". Compra pedida pelo " + order.getRequesterName()
                + " para o setor " + order.getSector().toUpperCase()
                + ". Marcando como comprado por " + loggedUser.getUserName() + ".";

        detailsLabel.setText(text);
        this.order = order;
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        try {
            LocalDate date = deliveryDatePicker.getValue();
            if (date == null) {
                errorLabel.setText("Informe a data de entrega prevista.");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            // fim do dia, para não marcar "em atraso" no próprio dia da entrega
            body.put("expectedDeliveryDate", date.atTime(23, 59, 59).toString());
            String reason = descriptionField.getText();
            if (reason != null && !reason.isBlank()) body.put("reason", reason.trim());

            JsonNode response = ApiClient.post("/api/purchase-orders/" + order.getId() + "/confirm",
                    mapper.writeValueAsString(body));
            System.out.println("RETORNO DO BACKEND: " + response.toPrettyString());

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 3: `DeclineRequestForm` — esconder DatePicker e enviar reason**

Em `DeclineRequestForm.java`: adicionar os campos `@FXML private DatePicker deliveryDatePicker;` e `@FXML private Label deliveryLabel;` (e o import `javafx.scene.control.DatePicker`). No fim de `setData(...)`, esconder o DatePicker (negar não tem data de entrega):

```java
        // Negar não usa data de entrega
        if (deliveryDatePicker != null) { deliveryDatePicker.setVisible(false); deliveryDatePicker.setManaged(false); }
        if (deliveryLabel != null) { deliveryLabel.setVisible(false); deliveryLabel.setManaged(false); }
```

E substituir o corpo de `handleSubmit()` para enviar a justificativa:

```java
    @FXML
    private void handleSubmit() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode body = mapper.createObjectNode();
            String reason = descriptionField.getText();
            if (reason != null && !reason.isBlank()) body.put("reason", reason.trim());

            JsonNode response = ApiClient.post("/api/purchase-orders/" + order.getId() + "/cancel",
                    mapper.writeValueAsString(body));
            System.out.println("RETORNO DO BACKEND: " + response.toPrettyString());

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
```

- [ ] **Step 4: Compilar**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Verificação manual**

Como DIRECTOR, em um pedido `quoted`: o botão diz "Marcar como Comprado"; o DatePicker vem com hoje+3 dias; ao confirmar com uma justificativa, conferir via `GET /api/purchase-orders/{id}` que `expectedDeliveryDate` e `decisionReason` foram gravados e `notes` (observação) permaneceu. No fluxo de negar, o DatePicker não aparece e `decisionReason` recebe a justificativa.

- [ ] **Step 6: Commit**

```bash
git add front/src/main/resources/com/fawkes/front/view/forms/director-request-form.fxml front/src/main/java/com/fawkes/front/controller/AproveRequestForm.java front/src/main/java/com/fawkes/front/controller/DeclineRequestForm.java
git commit -m "feat(front): Marcar como Comprado com DatePicker e justificativa persistida"
```

---

## Task 10: Front — papéis corretos + exibir justificativa no detalhe

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/models/Order.java` (campo `decisionReason`)
- Modify: `front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java:81-108`

- [ ] **Step 1: `Order` — ler e expor `decisionReason`**

Em `Order.java`: adicionar o campo `private String decisionReason;` junto aos demais; em `fromJson`, após o parse de `notes`/`desc` (perto da linha 145), adicionar:

```java
        String decisionReason = node.path("decisionReason").asText(null);
        if ("null".equals(decisionReason)) decisionReason = null;
```

e antes do `return ord;`:

```java
        ord.setDecisionReason(decisionReason);
```

Adicionar getter/setter:

```java
    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String v) { this.decisionReason = v; }
```

- [ ] **Step 2: `PendingRequestForm.renderActions` — papéis corretos**

Em `PendingRequestForm.java`, substituir os ramos `pending` e `quoted` do `switch` (linhas 88-108) por:

```java
            case "pending" -> {
                if (isDirectorManager) {
                    btnActionContainer.getChildren().add(
                            makeBtn("📋  Registrar Cotação", "btn--info", this::handleQuote)
                    );
                } else {
                    btnActionContainer.getChildren().add(infoLabel("⏳  Sob revisão — aguardando cotação"));
                }
            }
            case "quoted" -> {
                if ("DIRECTOR".equals(role)) {
                    btnActionContainer.getChildren().addAll(
                            makeBtn("✓  Marcar como Comprado", "btn--submit", this::handleAproved),
                            makeBtn("✗  Negar Pedido", "btn--danger", this::handleDeclined)
                    );
                } else {
                    btnActionContainer.getChildren().add(infoLabel("📋  Cotação registrada — aguardando decisão do diretor"));
                }
            }
```

(`role` e `isDirectorManager` já estão declarados no início de `renderActions`.)

- [ ] **Step 3: Exibir a justificativa no detalhe (quando houver)**

Em `PendingRequestForm.setData(...)`, após `description.setText(order.getDescription());` (linha 62), adicionar:

```java
        if (order.getDecisionReason() != null && !order.getDecisionReason().isBlank()) {
            description.setText(order.getDescription() + "\n\nJustificativa: " + order.getDecisionReason());
        }
```

- [ ] **Step 4: Compilar**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Verificação manual**

Logado como MANAGER: em `pending` só aparece "Registrar Cotação"; em `quoted` aparece a mensagem de aguardando o diretor (sem botões de decisão). Logado como DIRECTOR: em `quoted` aparecem "Marcar como Comprado" e "Negar Pedido". A justificativa gravada aparece no detalhe do pedido.

- [ ] **Step 6: Commit**

```bash
git add front/src/main/java/com/fawkes/front/models/Order.java front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java
git commit -m "feat(front): papeis corretos (cotacao=gerente, decisao=diretor) e exibe justificativa"
```

---

## Task 11: Front — filtros "Em Atraso" e "Problema"

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/orders-page.fxml:50-58`
- Modify: `front/src/main/java/com/fawkes/front/controller/OrdersPageController.java:227-243`

- [ ] **Step 1: Adicionar os botões de filtro**

Em `orders-page.fxml`, dentro do `HBox` de filtros, após o botão "Negado" (linha 58), adicionar:

```xml
         <JFXButton onAction="#filterByStatus" text="Em Atraso"  styleClass="btn--filter" userData="overdue" />
         <JFXButton onAction="#filterByStatus" text="Problema"   styleClass="btn--filter" userData="problem" />
```

- [ ] **Step 2: Tratar `overdue` (status efetivo) e `problem` no controller**

Em `OrdersPageController.filterByStatus`, substituir o método (linhas 227-243) por:

```java
    @FXML
    private void filterByStatus(ActionEvent event) {
        String statusKey = (String) ((com.jfoenix.controls.JFXButton) event.getSource()).getUserData();
        if ("all".equals(statusKey)) {
            renderOrders(allRequests);
            return;
        }

        List<Order> filtered;
        if ("overdue".equals(statusKey)) {
            filtered = allRequests.stream()
                    .filter(o -> "overdue".equals(o.getEffectiveStatus()))
                    .toList();
        } else {
            filtered = allRequests.stream()
                    .filter(o -> statusKey.equals(o.getStatus()))
                    .toList();
        }

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum pedido com status \""
                    + StringUtils.requestStatusTranslation(statusKey) + "\".");
        } else {
            renderOrders(filtered);
        }
    }
```

- [ ] **Step 3: Compilar**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Verificação manual**

Clicar em "Em Atraso" lista apenas pedidos `shipped` com `expectedDeliveryDate` vencida; "Problema" lista pedidos em `problem`. (Se `StringUtils.requestStatusTranslation` não tiver entrada para "overdue"/"problem", a mensagem de "nenhum pedido" mostra a chave crua — aceitável; opcionalmente adicionar as traduções em `StringUtils`.)

- [ ] **Step 5: Commit**

```bash
git add front/src/main/resources/com/fawkes/front/view/orders-page.fxml front/src/main/java/com/fawkes/front/controller/OrdersPageController.java
git commit -m "feat(front): filtros Em Atraso e Problema na tela de pedidos"
```

---

## Task 12: Front — estilo do modal de cotação

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java:59-66`
- Modify: `front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml:20`

- [ ] **Step 1: Padronizar largura/alinhamento do campo de preço nas linhas**

Em `QuoteRequestForm.java`, no trecho que cria cada linha (linhas ~59-66), dar largura fixa e prompt ao `priceField` e alinhar a linha:

```java
            TextField priceField = new TextField(
                    item.getUnitPrice() != null ? item.getUnitPrice().toPlainString() : "");
            priceField.setPromptText("R$ 0,00");
            priceField.setPrefWidth(120);
            priceField.setMaxWidth(120);

            HBox row = new HBox(8, name, qty, spacer, priceField);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
```

(Manter o restante da lógica de coleta dos preços inalterado. Ajustar apenas a criação visual — se as variáveis `name`/`qty`/`spacer` já existem como mostrado nas linhas 45-56, reutilizá-las.)

- [ ] **Step 2: Refinar o header do FXML**

Em `quote-request-form.fxml`, na linha 20, dar um pouco de respiro ao título:

```xml
            <Label styleClass="input__label" text="Preencha o preço unitário para cada item:">
               <VBox.margin>
                  <javafx.geometry.Insets bottom="4.0" top="2.0" />
               </VBox.margin>
            </Label>
```

Adicionar o import `<?import javafx.geometry.Insets?>` no topo do FXML.

- [ ] **Step 3: Compilar**

Run: `mvn -f front/pom.xml -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Verificação manual**

Abrir "Registrar Cotação": os campos de preço ficam alinhados à direita com largura uniforme; header com espaçamento adequado; a lógica de confirmar cotação continua funcionando.

- [ ] **Step 5: Commit**

```bash
git add front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml
git commit -m "style(front): alinhamento e espacamento do modal de cotacao"
```

---

## Verificação final (end-to-end)

Rodar com MySQL + API + front no ar, percorrendo o roteiro do spec:

1. **Observação** — abrir pedido com observação → `notes` persistido e visível no detalhe.
2. **Justificativa** — aprovar/negar com texto → `decisionReason` gravado e visível; `notes` original intacto.
3. **Preço** — abertura sem preço; itens com `unitPrice 0`; preço só após cotação.
4. **Papéis** — MANAGER só vê "Registrar Cotação"; DIRECTOR vê "Marcar como Comprado"/"Negar"; MANAGER via API em `/confirm` → 403.
5. **Marcar como comprado** — data default hoje+3 (editável) → `expectedDeliveryDate` persistido; estado vira `confirmed`.
6. **Filtros** — "Em Atraso" lista `shipped` vencidos; "Problema" lista `problem`.
7. **Cotação (UI)** — modal alinhado/estilizado.

Suite de testes de service verde: `mvn -f api/pom.xml -Dtest=PurchaseOrderServiceTest test` (5 testes).
