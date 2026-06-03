# Correção do fluxo de Pedidos (rodada 2) + histórico de status — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Corrigir os 7 defeitos remanescentes do fluxo de Pedidos (cotação editável, botões colapsando, DatePicker/justificativa/NF nas etapas erradas, data de entrega não exibida) e exibir o histórico de status dos pedidos na página Histórico.

**Architecture:** Backend Spring Boot (JPA + Lombok) com testes JUnit5/Mockito; front JavaFX (FXML + JFoenix). A separação de telas de transição em FXMLs dedicados (Aprovar / simples / Receber) elimina o vazamento de campos. As justificativas viram três campos distintos na entidade. O histórico de pedidos reusa `PurchaseOrderEvent` (já gravado) via um endpoint agregado novo e o `HistoryLogCard` existente.

**Tech Stack:** Java 17+, Spring Boot, JPA/Hibernate, Lombok, JUnit5, Mockito, AssertJ, JavaFX 25, JFoenix, Jackson.

**Convenções de teste/build:**
- API (testes): `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest`
- API (compilar): `cd api && ./mvnw -q -DskipTests compile`
- Front (compilar — não há testes de UI): `cd front && mvn -q -DskipTests compile`
- Front: verificação é **manual** (rodar o app). Cada task de front termina em compilar + commit; a verificação manual consolidada está na Task 13.

---

## File Structure

**API:**
- `Entities/PurchaseOrder.java` — `decisionReason`→`purchaseJustification` (mesma coluna `decision_reason`) + novo `problemJustification` (coluna `problem_justification`).
- `Services/PurchaseOrderService.java` — `confirmOrder`/`cancelOrder`→`purchaseJustification`; `markAsProblem`→`problemJustification`; `fillItemPrices` aceita `quoted` (silencioso); `listAllEvents()`.
- `Controllers/PurchaseOrderController.java` — `GET /api/purchase-orders/events`.
- `Repositories/PurchaseOrderEventRepository.java` — `findAllByOrderByOccurredAtDesc()`.
- `DTOs/Response/OrderEventActivityDTO.java` — **novo** DTO agregado (id/rótulo do pedido + dados do evento).
- `src/test/.../PurchaseOrderServiceTest.java` — atualizar getters renomeados + novos testes.

**Front:**
- `models/Order.java` — ler `purchaseJustification`/`problemJustification`; helper de data formatada.
- `view/forms/simple-request-form.fxml` — **novo** (detalhe + TextArea + botões, sem DatePicker).
- `view/forms/receive-request-form.fxml` — **novo** (detalhe + TextField numérico + botões).
- `controller/DeclineRequestForm.java`, `ShipRequestForm.java`, `ProblemRequestForm.java` — usar `simple-request-form.fxml`.
- `controller/ReceiveRequestForm.java` — usar `receive-request-form.fxml` + NF numérica.
- `controller/PendingRequestForm.java` — "Editar Cotação"; `makeBtn` por conteúdo; data prevista; duas justificativas; seleção de FXML.
- `view/forms/pending-request-form.fxml` — largura do `btnActionContainer` + label de entrega.
- `components/OrdersCard.java` + `view/components/OrdersCard.fxml` — label de entrega prevista.
- `view/forms/quote-request-form.fxml` — estilo do modal.
- `controller/HistoryPageController.java` + `view/history-page.fxml` — abas Estoque/Pedidos.
- `components/HistoryLogCard.java` — método para renderizar evento de pedido.

---

## Task 1: Entidade — três campos de justificativa

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java:49-50`

- [ ] **Step 1: Renomear o campo e adicionar o novo**

Em `PurchaseOrder.java`, substituir o bloco do campo `decisionReason` (linhas 49-50):

```java
        @Column(name = "decision_reason", columnDefinition = "TEXT")
        private String purchaseJustification;

        @Column(name = "problem_justification", columnDefinition = "TEXT")
        private String problemJustification;
```

(Mantém a coluna física `decision_reason` para preservar dados; Lombok `@Data` gera `getPurchaseJustification`/`setPurchaseJustification` e `getProblemJustification`/`setProblemJustification`.)

- [ ] **Step 2: Compilar (vai falhar — usos antigos de decisionReason)**

Run: `cd api && ./mvnw -q -DskipTests compile`
Expected: FALHA com erros "cannot find symbol: method getDecisionReason()/setDecisionReason()" em `PurchaseOrderService.java` e nos testes. Isso é esperado; corrigido nas Tasks 2-3 e 4.

- [ ] **Step 3: Não commitar ainda** — a entidade sozinha quebra o build. Seguir para a Task 2.

---

## Task 2: markAsProblem grava em problemJustification (sem tocar na compra)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java:341-353`
- Test: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Atualizar o teste existente de markAsProblem**

Substituir o método `markAsProblem_gravaDecisionReason_ePreservaNotes` por:

```java
    @Test
    void markAsProblem_gravaProblemJustification_ePreservaCompraENotes() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.received);
        order.setNotes("Observação do solicitante");
        order.setPurchaseJustification("Comprado no fornecedor X");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.markAsProblem(1L, "Item veio quebrado");

        assertThat(result.getProblemJustification()).isEqualTo("Item veio quebrado");
        assertThat(result.getPurchaseJustification()).isEqualTo("Comprado no fornecedor X");
        assertThat(result.getNotes()).isEqualTo("Observação do solicitante");
    }
```

- [ ] **Step 2: Atualizar os outros testes que usam getDecisionReason**

Em `confirmOrder_persisteDataEntregaEJustificativa_eMarcaConfirmed`, trocar a última asserção:

```java
        assertThat(result.getPurchaseJustification()).isEqualTo("Comprado no fornecedor X");
```

Em `cancelOrder_persisteReasonEmDecisionReason`, renomear e trocar a asserção:

```java
    @Test
    void cancelOrder_persisteReasonEmPurchaseJustification() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.cancelOrder(1L, "Fora do orçamento");

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.cancelled);
        assertThat(result.getPurchaseJustification()).isEqualTo("Fora do orçamento");
    }
```

- [ ] **Step 3: Atualizar o service (confirm, cancel, problem)**

Em `PurchaseOrderService.java`:

`confirmOrder` (linha ~138) — trocar `order.setDecisionReason(request.reason());` por:

```java
                order.setPurchaseJustification(request.reason());
```

`cancelOrder` (linha ~221) — trocar `order.setDecisionReason(reason);` por:

```java
            order.setPurchaseJustification(reason);
```

`markAsProblem` (linha ~349) — trocar `if (reason != null && !reason.isBlank()) order.setDecisionReason(reason);` por:

```java
        if (reason != null && !reason.isBlank()) order.setProblemJustification(reason);
```

- [ ] **Step 4: Rodar os testes**

Run: `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest`
Expected: PASS (todos os testes, incluindo o novo de problema).

- [ ] **Step 5: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java \
        api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java \
        api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "feat(api): separa justificativa de compra e de problema no recebimento

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 3: fillItemPrices aceita status quoted (edição silenciosa)

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java:288-326`
- Test: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Escrever os testes (falhando)**

Adicionar ao `PurchaseOrderServiceTest`:

```java
    @Test
    void fillItemPrices_dePendingVaiParaQuoted_eGravaEvento() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.pending);
        var item = new com.fawkes.api.Entities.PurchaseOrderItem();
        item.setId(7L);
        item.setQuantity(2);
        order.setItems(new java.util.ArrayList<>(java.util.List.of(item)));
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(
                java.util.List.of(new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest
                        .ItemPriceEntry(7L, new BigDecimal("10.00"))));

        PurchaseOrder result = service.fillItemPrices(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        org.mockito.Mockito.verify(purchaseOrderEventRepository).save(any());
    }

    @Test
    void fillItemPrices_emQuoted_mantemQuoted_eNaoGravaEvento() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        var item = new com.fawkes.api.Entities.PurchaseOrderItem();
        item.setId(7L);
        item.setQuantity(2);
        order.setItems(new java.util.ArrayList<>(java.util.List.of(item)));
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(
                java.util.List.of(new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest
                        .ItemPriceEntry(7L, new BigDecimal("12.50"))));

        PurchaseOrder result = service.fillItemPrices(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        assertThat(result.getItems().get(0).getUnitPrice()).isEqualByComparingTo("12.50");
        org.mockito.Mockito.verifyNoInteractions(purchaseOrderEventRepository);
    }

    @Test
    void fillItemPrices_rejeitaQuandoNaoEhPendingNemQuoted() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.confirmed);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(java.util.List.of());

        assertThatThrownBy(() -> service.fillItemPrices(1L, req))
                .isInstanceOf(RegraDeNegocioException.class);
    }
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest#fillItemPrices_emQuoted_mantemQuoted_eNaoGravaEvento`
Expected: FAIL — hoje `fillItemPrices` lança exceção quando status ≠ `pending`.

- [ ] **Step 3: Implementar**

Substituir, em `fillItemPrices`, o guard de status (linhas ~293-296):

```java
        if (order.getStatus() != PurchaseOrder.Status.pending
                && order.getStatus() != PurchaseOrder.Status.quoted) {
            throw new RegraDeNegocioException(
                    "Só é possível registrar/editar cotação em pedidos 'Sob Revisão' ou 'Em Cotação'");
        }
        boolean wasPending = order.getStatus() == PurchaseOrder.Status.pending;
```

E substituir o bloco final (linhas ~321-325, a partir de `recalculateTotal(order);`) por:

```java
        recalculateTotal(order);
        if (wasPending) {
            order.setStatus(PurchaseOrder.Status.quoted);
            PurchaseOrder saved = purchaseOrderRepository.save(order);
            recordEvent(saved, PurchaseOrder.Status.pending, PurchaseOrder.Status.quoted, null);
            return saved;
        }
        // edição silenciosa de cotação já registrada: mantém 'quoted', sem evento
        return purchaseOrderRepository.save(order);
```

- [ ] **Step 4: Rodar todos os testes da classe**

Run: `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java \
        api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "feat(api): cotacao editavel em status quoted (edicao silenciosa)

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 4: Endpoint agregado de eventos de pedido

**Files:**
- Create: `api/src/main/java/com/fawkes/api/DTOs/Response/OrderEventActivityDTO.java`
- Modify: `api/src/main/java/com/fawkes/api/Repositories/PurchaseOrderEventRepository.java`
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java`
- Modify: `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java`
- Test: `api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java`

- [ ] **Step 1: Criar o DTO**

`OrderEventActivityDTO.java`:

```java
package com.fawkes.api.DTOs.Response;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderEvent;
import com.fawkes.api.Entities.PurchaseOrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderEventActivityDTO(
        Long eventId,
        Long orderId,
        String orderLabel,
        PurchaseOrder.Status fromStatus,
        PurchaseOrder.Status toStatus,
        String performedBy,
        String reason,
        LocalDateTime occurredAt
) {
    public static OrderEventActivityDTO from(PurchaseOrderEvent e) {
        PurchaseOrder order = e.getPurchaseOrder();
        Long orderId = order != null ? order.getId() : null;
        String label = "Pedido #" + orderId;
        if (order != null) {
            List<PurchaseOrderItem> items = order.getItems();
            if (items != null && !items.isEmpty() && items.get(0).getProduct() != null) {
                String prod = items.get(0).getProduct().getProductName();
                label = "Pedido #" + orderId + " — " + prod;
                if (items.size() > 1) label += " (+" + (items.size() - 1) + ")";
            }
        }
        return new OrderEventActivityDTO(
                e.getId(), orderId, label,
                e.getFromStatus(), e.getToStatus(),
                e.getPerformedBy(), e.getReason(), e.getOccurredAt());
    }
}
```

> `Products` tem `private String productName` (Lombok gera `getProductName()`), já verificado.

- [ ] **Step 2: Adicionar o método ao repository**

Em `PurchaseOrderEventRepository.java`, adicionar:

```java
    java.util.List<PurchaseOrderEvent> findAllByOrderByOccurredAtDesc();
```

- [ ] **Step 3: Escrever o teste do service (falhando)**

Adicionar ao `PurchaseOrderServiceTest`:

```java
    @Test
    void listAllEvents_mapeiaEventosParaDTO_maisRecentesPrimeiro() {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(5L);
        order.setItems(new java.util.ArrayList<>());
        var event = new com.fawkes.api.Entities.PurchaseOrderEvent();
        event.setId(99L);
        event.setPurchaseOrder(order);
        event.setFromStatus(PurchaseOrder.Status.pending);
        event.setToStatus(PurchaseOrder.Status.quoted);
        event.setPerformedBy("gerente@x");
        event.setOccurredAt(LocalDateTime.of(2026, 6, 2, 10, 0));
        when(purchaseOrderEventRepository.findAllByOrderByOccurredAtDesc())
                .thenReturn(java.util.List.of(event));

        var result = service.listAllEvents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).orderId()).isEqualTo(5L);
        assertThat(result.get(0).toStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        assertThat(result.get(0).performedBy()).isEqualTo("gerente@x");
    }
```

- [ ] **Step 4: Rodar e ver falhar**

Run: `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest#listAllEvents_mapeiaEventosParaDTO_maisRecentesPrimeiro`
Expected: FAIL — método `listAllEvents` não existe.

- [ ] **Step 5: Implementar o service**

Em `PurchaseOrderService.java`, adicionar o import e o método (perto de `listEvents`):

```java
import com.fawkes.api.DTOs.Response.OrderEventActivityDTO;
```

```java
    @Transactional(readOnly = true)
    public List<OrderEventActivityDTO> listAllEvents() {
        return purchaseOrderEventRepository.findAllByOrderByOccurredAtDesc()
                .stream()
                .map(OrderEventActivityDTO::from)
                .toList();
    }
```

- [ ] **Step 6: Expor no controller**

Em `PurchaseOrderController.java`, adicionar o import e o endpoint (acima de `listEvents` por `/{id}`):

```java
import com.fawkes.api.DTOs.Response.OrderEventActivityDTO;
```

```java
    @GetMapping("/events")
    public ResponseEntity<List<OrderEventActivityDTO>> listAllEvents() {
        return ResponseEntity.ok(purchaseOrderService.listAllEvents());
    }
```

> Importante: registrar `@GetMapping("/events")` **antes** do `@GetMapping("/{id}/events")` não é necessário (paths diferentes), mas garanta que não há um `@GetMapping("/{id}")` capturando `events` — não há, pois `events` casa com `/events`, não `/{id}`.

- [ ] **Step 7: Rodar testes + compilar**

Run: `cd api && ./mvnw -q test -Dtest=PurchaseOrderServiceTest`
Expected: PASS.
Run: `cd api && ./mvnw -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add api/src/main/java/com/fawkes/api/DTOs/Response/OrderEventActivityDTO.java \
        api/src/main/java/com/fawkes/api/Repositories/PurchaseOrderEventRepository.java \
        api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java \
        api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java \
        api/src/test/java/com/fawkes/api/Services/PurchaseOrderServiceTest.java
git commit -m "feat(api): endpoint agregado GET /purchase-orders/events

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 5: Front — Order lê as novas justificativas

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/models/Order.java`

- [ ] **Step 1: Trocar o campo decisionReason por dois campos**

Em `Order.java`, substituir a linha `private String decisionReason;` (linha ~25) por:

```java
    private String purchaseJustification;
    private String problemJustification;
```

- [ ] **Step 2: Atualizar a leitura do JSON**

Em `fromJson`, substituir o bloco de `decisionReason` (linhas ~144-145):

```java
        String purchaseJustification = node.path("purchaseJustification").asText(null);
        if ("null".equals(purchaseJustification)) purchaseJustification = null;
        String problemJustification = node.path("problemJustification").asText(null);
        if ("null".equals(problemJustification)) problemJustification = null;
```

E, perto do final (onde está `ord.setDecisionReason(decisionReason);`, linha ~159), substituir por:

```java
        ord.setPurchaseJustification(purchaseJustification);
        ord.setProblemJustification(problemJustification);
```

- [ ] **Step 3: Trocar getters/setters**

Substituir `getDecisionReason`/`setDecisionReason` (linhas ~192-193) por:

```java
    public String getPurchaseJustification() { return purchaseJustification; }
    public void setPurchaseJustification(String v) { this.purchaseJustification = v; }
    public String getProblemJustification() { return problemJustification; }
    public void setProblemJustification(String v) { this.problemJustification = v; }
```

- [ ] **Step 4: Adicionar helper de data prevista formatada (usado nas Tasks 9 e 10)**

Adicionar ao `Order.java` (junto aos getters):

```java
    /** Retorna a data prevista como "dd/MM/aaaa", ou null se não houver. */
    public String getExpectedDeliveryDateFormatted() {
        if (expectedDeliveryDate == null) return null;
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(expectedDeliveryDate);
            return dt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }
```

- [ ] **Step 5: Compilar (vai falhar em PendingRequestForm — usa getDecisionReason)**

Run: `cd front && mvn -q -DskipTests compile`
Expected: FALHA em `PendingRequestForm.java` (usa `getDecisionReason`). Corrigido na Task 8. Não commitar ainda; seguir.

---

## Task 6: simple-request-form.fxml (Recusar / Enviar / Problema)

**Files:**
- Create: `front/src/main/resources/com/fawkes/front/view/forms/simple-request-form.fxml`
- Modify: `front/src/main/java/com/fawkes/front/controller/DeclineRequestForm.java`
- Modify: `front/src/main/java/com/fawkes/front/controller/ShipRequestForm.java`
- Modify: `front/src/main/java/com/fawkes/front/controller/ProblemRequestForm.java`
- Modify: `front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java` (seleção de FXML)

- [ ] **Step 1: Criar o FXML simples (sem DatePicker)**

`simple-request-form.fxml` (baseado no director-form, mas sem o DatePicker/label de entrega):

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import com.jfoenix.controls.JFXButton?>
<?import java.lang.String?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TextArea?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<AnchorPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="324.0" prefWidth="673.0" xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1">
    <children>
      <VBox alignment="TOP_CENTER" prefHeight="267.0" prefWidth="642.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
         <children>
            <Label fx:id="detailsLabel" prefHeight="82.0" prefWidth="640.0" styleClass="input__label--product-desc" text="Detalhes do pedido." wrapText="true" />
            <AnchorPane prefHeight="152.0" prefWidth="674.0">
               <children>
                  <Label fx:id="noteTitle" styleClass="input__label" text="Gostaria de deixar uma observação?" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="6.0" />
                  <TextArea fx:id="descriptionField" prefHeight="80.0" promptText="Digite alguma observação ou justificativa..." AnchorPane.bottomAnchor="10.0" AnchorPane.leftAnchor="10.0" AnchorPane.rightAnchor="10.0" AnchorPane.topAnchor="28.0">
                     <styleClass>
                        <String fx:value="input__text" />
                        <String fx:value="input__text--form" />
                     </styleClass>
                  </TextArea>
               </children>
            </AnchorPane>
            <Label fx:id="errorLabel" prefHeight="18.0" prefWidth="687.0" />
         </children>
      </VBox>
      <HBox alignment="CENTER_RIGHT" prefHeight="42.0" prefWidth="674.0" spacing="15.0" AnchorPane.bottomAnchor="20.0">
         <children>
            <JFXButton fx:id="btnCommand" onAction="#handleSubmit" prefHeight="26.0" prefWidth="160.0" styleClass="btn--submit" text="Confirmar" />
            <JFXButton fx:id="btnCancel" onAction="#handleCloseModal" prefHeight="26.0" prefWidth="101.0" styleClass="btn--cancel" text="Cancelar" />
         </children>
      </HBox>
    </children>
</AnchorPane>
```

- [ ] **Step 2: Limpar o DeclineRequestForm**

Em `DeclineRequestForm.java`, remover os campos `deliveryDatePicker` e `deliveryLabel` (linhas ~24-25) e as importações não usadas (`DatePicker`, `TextField`, `Button`, `ActionEvent`). Remover, dentro de `setData`, o bloco que esconde o DatePicker (linhas ~56-58):

```java
        // (remover o bloco "Negar não usa data de entrega" — o FXML simples não tem DatePicker)
```

- [ ] **Step 3: Compilar para garantir que Decline está coerente** — adiado para o Step 5 (precisa do seletor de FXML).

- [ ] **Step 4: Ajustar a seleção de FXML no PendingRequestForm**

Em `PendingRequestForm.abrirSubModal` (linhas ~201-203), substituir a escolha do `fxmlPath` por:

```java
            String fxmlPath;
            if (controller instanceof QuoteRequestForm) {
                fxmlPath = "/com/fawkes/front/view/forms/quote-request-form.fxml";
            } else if (controller instanceof AproveRequestForm) {
                fxmlPath = "/com/fawkes/front/view/forms/director-request-form.fxml";
            } else if (controller instanceof ReceiveRequestForm) {
                fxmlPath = "/com/fawkes/front/view/forms/receive-request-form.fxml";
            } else {
                // Recusar, Enviar, Problema
                fxmlPath = "/com/fawkes/front/view/forms/simple-request-form.fxml";
            }
```

> A Task 7 cria `receive-request-form.fxml`. Se executar esta task antes da 7, o branch de Receive aponta para um arquivo ainda inexistente — só falha em runtime ao abrir Receber, não na compilação. Recomenda-se executar a Task 7 logo em seguida (ou na mesma leva) antes de testar Receber.

- [ ] **Step 5: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: ainda FALHA em `PendingRequestForm` por causa do `getDecisionReason` (Task 8) — mas **não** por `simple-request-form`/Decline. Se aparecer erro ligado a `deliveryDatePicker`/`DatePicker` em DeclineRequestForm, corrija os imports/campos removidos.

- [ ] **Step 6: Não commitar ainda** (build quebrado até a Task 8). Seguir para a Task 7.

---

## Task 7: receive-request-form.fxml (NF numérica)

**Files:**
- Create: `front/src/main/resources/com/fawkes/front/view/forms/receive-request-form.fxml`
- Modify: `front/src/main/java/com/fawkes/front/controller/ReceiveRequestForm.java`

- [ ] **Step 1: Criar o FXML de recebimento (TextField numérico, sem DatePicker)**

`receive-request-form.fxml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import com.jfoenix.controls.JFXButton?>
<?import java.lang.String?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<AnchorPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="324.0" prefWidth="673.0" xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1">
    <children>
      <VBox alignment="TOP_CENTER" prefHeight="267.0" prefWidth="642.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
         <children>
            <Label fx:id="detailsLabel" prefHeight="82.0" prefWidth="640.0" styleClass="input__label--product-desc" text="Detalhes do recebimento." wrapText="true" />
            <AnchorPane prefHeight="152.0" prefWidth="674.0">
               <children>
                  <Label styleClass="input__label" text="Número da nota fiscal:" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="6.0" />
                  <TextField fx:id="invoiceField" prefWidth="240.0" promptText="Somente números" AnchorPane.leftAnchor="10.0" AnchorPane.topAnchor="28.0">
                     <styleClass>
                        <String fx:value="input__text" />
                        <String fx:value="input__text--form" />
                     </styleClass>
                  </TextField>
               </children>
            </AnchorPane>
            <Label fx:id="errorLabel" prefHeight="18.0" prefWidth="687.0" />
         </children>
      </VBox>
      <HBox alignment="CENTER_RIGHT" prefHeight="42.0" prefWidth="674.0" spacing="15.0" AnchorPane.bottomAnchor="20.0">
         <children>
            <JFXButton fx:id="btnCommand" onAction="#handleSubmit" prefHeight="26.0" prefWidth="200.0" styleClass="btn--submit" text="Confirmar Recebimento" />
            <JFXButton fx:id="btnCancel" onAction="#handleCloseModal" prefHeight="26.0" prefWidth="101.0" styleClass="btn--cancel" text="Cancelar" />
         </children>
      </HBox>
    </children>
</AnchorPane>
```

- [ ] **Step 2: Atualizar o ReceiveRequestForm**

Substituir o conteúdo de `ReceiveRequestForm.java` por (TextField numérico no lugar do TextArea):

```java
package com.fawkes.front.controller;

import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class ReceiveRequestForm {

    @FXML private Label     detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextField invoiceField;
    @FXML private Label     errorLabel;

    private Order    order;
    private Runnable onSaveSuccess;
    private final UserInfoManager loggedUser = UserInfoManager.getInstance();
    private final ObjectMapper    mapper     = new ObjectMapper();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void initialize() {
        btnCommand.setText("Confirmar Recebimento");
        if (invoiceField != null) {
            // aceita apenas dígitos
            invoiceField.setTextFormatter(new TextFormatter<>(change ->
                    change.getControlNewText().matches("\\d*") ? change : null));
        }
    }

    public void setData(Order order) {
        this.order = order;
        detailsLabel.setText("Registrar o recebimento do pedido #" + order.getId()
                + ". Informe o número da nota fiscal abaixo para concluir o processo."
                + " Recebimento registrado por " + loggedUser.getUserName() + ".");
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        String nf = invoiceField != null ? invoiceField.getText().trim() : "";
        if (nf.isEmpty()) {
            if (errorLabel != null) errorLabel.setText("Informe o número da nota fiscal.");
            return;
        }
        if (!nf.matches("\\d+")) {
            if (errorLabel != null) errorLabel.setText("A nota fiscal deve conter apenas números.");
            return;
        }
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("invoiceNumber", nf);
            body.put("invoiceSerie", "1");

            ArrayNode itemsArray = mapper.createArrayNode();
            for (RequestItem item : order.getItemsList()) {
                ObjectNode itemNode = mapper.createObjectNode();
                itemNode.put("productId",        item.getProduct().getId());
                itemNode.put("receivedQuantity", item.getQuantity());
                itemsArray.add(itemNode);
            }
            body.set("items", itemsArray);

            ApiClient.post("/api/purchase-orders/" + order.getId() + "/receive",
                    mapper.writeValueAsString(body));

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            if (errorLabel != null) errorLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

- [ ] **Step 3: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: ainda FALHA só por `PendingRequestForm.getDecisionReason` (Task 8). Sem erros novos em ReceiveRequestForm.

- [ ] **Step 4: Não commitar ainda.** Seguir para a Task 8 (que destrava o build).

---

## Task 8: PendingRequestForm — Editar Cotação, botões, justificativas

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java`

- [ ] **Step 1: Exibir as duas justificativas no detalhe**

Em `setData` (linhas ~62-65), substituir o bloco que usa `getDecisionReason` por:

```java
        StringBuilder desc = new StringBuilder(order.getDescription() != null ? order.getDescription() : "");
        if (order.getPurchaseJustification() != null && !order.getPurchaseJustification().isBlank()) {
            desc.append("\n\nJustificativa da compra: ").append(order.getPurchaseJustification());
        }
        if (order.getProblemJustification() != null && !order.getProblemJustification().isBlank()) {
            desc.append("\n\nJustificativa do problema: ").append(order.getProblemJustification());
        }
        description.setText(desc.toString());
```

(Remover a chamada `description.setText(order.getDescription());` anterior que ficou redundante na linha ~62.)

- [ ] **Step 2: Botão "Editar Cotação" em quoted**

No `renderActions`, no `case "quoted"` (linhas ~101-110), substituir por:

```java
            case "quoted" -> {
                boolean canEditQuote = "DIRECTOR".equals(role) || "MANAGER".equals(role);
                if (canEditQuote) {
                    btnActionContainer.getChildren().add(
                            makeBtn("✏  Editar Cotação", "btn--info", this::handleQuote));
                }
                if ("DIRECTOR".equals(role)) {
                    btnActionContainer.getChildren().addAll(
                            makeBtn("✓  Marcar como Comprado", "btn--submit", this::handleAproved),
                            makeBtn("✗  Negar Pedido", "btn--danger", this::handleDeclined)
                    );
                } else {
                    btnActionContainer.getChildren().add(
                            infoLabel("📋  Cotação registrada — aguardando decisão do diretor"));
                }
            }
```

- [ ] **Step 3: makeBtn com largura por conteúdo**

Substituir `makeBtn` (linhas ~237-244) por:

```java
    private JFXButton makeBtn(String text, String style, Runnable action) {
        JFXButton btn = new JFXButton(text);
        btn.getStyleClass().add(style);
        btn.setPrefHeight(26);
        btn.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        btn.setPadding(new javafx.geometry.Insets(2, 14, 2, 14));
        btn.setOnAction(e -> action.run());
        return btn;
    }
```

(Sem `prefWidth` fixo: o botão dimensiona pelo texto; `minWidth=USE_PREF_SIZE` impede que o HBox o encolha cortando o texto.)

- [ ] **Step 4: Exibir a data de entrega prevista no detalhe**

No fim de `setData` (após o bloco da NF, ~linha 80), adicionar:

```java
        // Data de entrega prevista (existe a partir de 'confirmed')
        if (order.getExpectedDeliveryDateFormatted() != null) {
            deliveryInfo.setText("Entrega prevista: " + order.getExpectedDeliveryDateFormatted());
            deliveryInfo.setVisible(true);
            deliveryInfo.setManaged(true);
        }
```

E declarar o campo no topo da classe (junto aos outros `@FXML`):

```java
    @FXML private Label deliveryInfo;
```

> O `fx:id="deliveryInfo"` é adicionado ao FXML na Task 9.

- [ ] **Step 5: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: BUILD SUCCESS (este passo destrava o build das Tasks 5-7).

- [ ] **Step 6: Commit (backend + front até aqui já coerentes)**

```bash
git add front/src/main/java/com/fawkes/front/models/Order.java \
        front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java \
        front/src/main/java/com/fawkes/front/controller/DeclineRequestForm.java \
        front/src/main/java/com/fawkes/front/controller/ShipRequestForm.java \
        front/src/main/java/com/fawkes/front/controller/ProblemRequestForm.java \
        front/src/main/java/com/fawkes/front/controller/ReceiveRequestForm.java \
        front/src/main/resources/com/fawkes/front/view/forms/simple-request-form.fxml \
        front/src/main/resources/com/fawkes/front/view/forms/receive-request-form.fxml
git commit -m "feat(front): cotacao editavel, FXMLs por etapa, NF numerica, justificativas

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

> Nota: `ShipRequestForm`/`ProblemRequestForm` não precisam de mudança de código (já leem `descriptionField`/`detailsLabel`/`errorLabel`, todos presentes no `simple-request-form.fxml`). Estão no commit por passarem a usar o novo FXML via `abrirSubModal`.

---

## Task 9: pending-request-form.fxml — botões e label de entrega

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/forms/pending-request-form.fxml`

- [ ] **Step 1: Alargar e reposicionar o btnActionContainer**

Substituir o `HBox fx:id="btnActionContainer"` (linhas ~94-99) por uma versão mais larga, ancorada à direita, alinhada à direita:

```xml
      <HBox fx:id="btnActionContainer" alignment="CENTER_RIGHT" layoutX="120.0" layoutY="304.0" prefHeight="40.0" prefWidth="560.0" spacing="12.0">
         <children>
         </children>
      </HBox>
```

(Os botões são adicionados programaticamente; largura 560 + `CENTER_RIGHT` acomoda "Editar Cotação" + "Marcar como Comprado" + "Negar Pedido".)

- [ ] **Step 2: Adicionar o label de entrega prevista**

Dentro do `VBox fx:id="invoiceContainer"` (linhas ~88-93), ou logo abaixo dele, adicionar um label próprio. Inserir, após o fechamento do `invoiceContainer` (`</VBox>` da linha ~93):

```xml
       <Label fx:id="deliveryInfo" layoutX="14.0" layoutY="332.0" managed="false" styleClass="input__label--info--dark" text="" visible="false" />
```

- [ ] **Step 3: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: BUILD SUCCESS (FXML é validado em runtime, mas a compilação Java deve passar; o `fx:id` casa com o campo `deliveryInfo` da Task 8).

- [ ] **Step 4: Commit**

```bash
git add front/src/main/resources/com/fawkes/front/view/forms/pending-request-form.fxml
git commit -m "fix(front): botoes de acao sem colapsar e data de entrega no detalhe

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 10: OrdersCard — data de entrega prevista no card

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/components/OrdersCard.fxml`
- Modify: `front/src/main/java/com/fawkes/front/components/OrdersCard.java`

- [ ] **Step 1: Inspecionar o FXML do card**

Run: `sed -n '1,80p' front/src/main/resources/com/fawkes/front/view/components/OrdersCard.fxml`
Expected: ver os labels existentes (status, solicitorName, department, paymentMethod, quantityValue, priceValue) e escolher onde encaixar um novo label.

- [ ] **Step 2: Adicionar um Label de entrega ao FXML**

Adicionar, próximo a `priceValue`/`quantityValue`, um label:

```xml
                <Label fx:id="deliveryValue" managed="false" styleClass="input__label--info" text="" visible="false" />
```

(Posicione conforme o layout existente — mesmo container dos demais labels de info.)

- [ ] **Step 3: Preencher no controller**

Em `OrdersCard.java`, adicionar o campo `@FXML private Label deliveryValue;` (junto aos outros) e, no fim de `setData` (após `this.order = order;`), adicionar:

```java
        String prevista = order.getExpectedDeliveryDateFormatted();
        if (deliveryValue != null && prevista != null) {
            deliveryValue.setText("Entrega prevista: " + prevista);
            deliveryValue.setVisible(true);
            deliveryValue.setManaged(true);
        }
```

- [ ] **Step 4: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add front/src/main/java/com/fawkes/front/components/OrdersCard.java \
        front/src/main/resources/com/fawkes/front/view/components/OrdersCard.fxml
git commit -m "feat(front): exibe data de entrega prevista no card do pedido

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 11: Estilo do modal de cotação

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml`
- Modify: `front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java` (alinhamento das linhas)

- [ ] **Step 1: Alinhar as linhas de item**

Em `QuoteRequestForm.setData` (linhas ~44-69), ajustar a montagem da linha para alinhar nome à esquerda (largura fixa), quantidade com largura fixa e o campo de preço à direita:

```java
        for (RequestItem item : order.getItemsList()) {
            Label name = new Label(item.getProduct().getName());
            name.getStyleClass().add("input__label--info");
            name.setPrefWidth(280);
            name.setMinWidth(280);

            Label qty = new Label("x " + item.getQuantity());
            qty.getStyleClass().add("input__label--info");
            qty.setPrefWidth(50);
            qty.setMinWidth(50);

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            double existingPrice = item.getUnitPrice();
            TextField priceField = new TextField(
                    existingPrice > 0
                            ? String.format("%.2f", existingPrice).replace(",", ".")
                            : "");
            priceField.setPromptText("Preço unitário (R$)");
            priceField.setPrefWidth(140);
            priceField.setMaxWidth(140);

            HBox row = new HBox(10, name, qty, spacer, priceField);
            row.setAlignment(Pos.CENTER_LEFT);
            itemsContainer.getChildren().add(row);

            itemIds.add(new long[]{item.getId()});
            priceFields.add(priceField);
        }
```

- [ ] **Step 2: Refinar o cabeçalho/espaçamento do FXML**

Em `quote-request-form.fxml`, ajustar o `Label` de título e o `VBox` interno para mais respiro (título à esquerda, padding consistente). Substituir o `<Label styleClass="input__label" ...>` de título (linhas ~21-25) por:

```xml
            <Label styleClass="input__label" text="Preencha o preço unitário de cada item:" maxWidth="Infinity">
               <VBox.margin>
                  <Insets bottom="8.0" left="4.0" top="2.0" />
               </VBox.margin>
            </Label>
```

E trocar o `style="-fx-padding: 8;"` do `itemsContainer` por `style="-fx-padding: 12;"`.

- [ ] **Step 3: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml \
        front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java
git commit -m "style(front): alinhamento e espacamento do modal de cotacao

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 12: HistoryPage — abas Estoque/Pedidos

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/history-page.fxml`
- Modify: `front/src/main/java/com/fawkes/front/controller/HistoryPageController.java`
- Modify: `front/src/main/java/com/fawkes/front/components/HistoryLogCard.java`

- [ ] **Step 1: Adicionar o seletor de abas ao FXML**

Substituir o conteúdo de `history-page.fxml` por:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import com.jfoenix.controls.JFXButton?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<VBox fx:id="pageContent" maxHeight="-Infinity" maxWidth="1200.0" minWidth="800.0" prefHeight="720.0" spacing="16.0" styleClass="pageContent" xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.fawkes.front.controller.HistoryPageController">
   <children>
      <HBox spacing="8.0">
         <children>
            <JFXButton fx:id="tabStock" onAction="#showStock" styleClass="btn--filter" text="Estoque" />
            <JFXButton fx:id="tabOrders" onAction="#showOrders" styleClass="btn--filter" text="Pedidos" />
         </children>
      </HBox>
      <VBox fx:id="historyContainer" alignment="TOP_CENTER" prefHeight="302.0" prefWidth="800.0" VBox.vgrow="ALWAYS" />
   </children>
</VBox>
```

- [ ] **Step 2: Adicionar render de evento de pedido no HistoryLogCard**

Em `HistoryLogCard.java`, adicionar um método novo (sem mexer no `setData` de estoque):

```java
    /** Renderiza um evento de pedido vindo de /api/purchase-orders/events. */
    public void setOrderEvent(com.fasterxml.jackson.databind.JsonNode node) {
        String toStatus = node.path("toStatus").asText("");
        String label    = node.path("orderLabel").asText("Pedido");
        String by       = node.path("performedBy").asText("-");
        String reason   = node.path("reason").asText(null);
        String when     = com.fawkes.front.models.HistoryLog
                .fromJson(node, com.fawkes.front.models.HistoryLog.MovementType.COMPRA).getDate();
        // 'date' do evento vem em occurredAt; reusa o parser de data via campo "date"

        if (status != null) status.setText(when);

        if (typeLabel != null) {
            typeLabel.setText("Pedido — "
                    + com.fawkes.front.utils.StringUtils.requestStatusTranslation(toStatus));
            typeLabel.getStyleClass().add(orderStatusStyle(toStatus));
        }

        Label ownerLabel = (Label) this.lookup(".log__owner");
        if (ownerLabel != null) ownerLabel.setText(label);

        Label descLabel = (Label) this.lookup(".log__description");
        if (descLabel != null) {
            String txt = "Por: " + by;
            if (reason != null && !reason.isBlank() && !"null".equals(reason)) {
                txt += "  —  " + reason;
            }
            descLabel.setText(txt);
        }
    }

    private String orderStatusStyle(String toStatus) {
        return switch (toStatus) {
            case "pending"   -> "log__status--3";
            case "quoted"    -> "log__status--3";
            case "confirmed" -> "log__status--6";
            case "shipped"   -> "log__status--7";
            case "received"  -> "log__status--9";
            case "cancelled" -> "log__status--5";
            case "problem"   -> "log__status--10";
            case "returned"  -> "log__status--12";
            default          -> "log__status";
        };
    }
```

> O parser de data de `HistoryLog.fromJson` lê o campo `"date"`. Como o evento traz `occurredAt`, ajuste o parse no controller (Step 3) renomeando para `date`, **ou** passe a data já pronta. Para simplicidade, o Step 3 injeta `occurredAt` como `date` antes de chamar `setOrderEvent`. Se preferir, troque a linha de `when` por leitura direta de `node.path("occurredAt")` via `HistoryLog`-like parse.

- [ ] **Step 3: Reescrever o HistoryPageController com as duas abas**

Substituir o `HistoryPageController.java` por:

```java
package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryPageController {

    @FXML private VBox historyContainer;

    @FXML
    public void initialize() {
        historyContainer.setMinWidth(0);
        historyContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        historyContainer.setMaxWidth(Double.MAX_VALUE);
        showStock();
    }

    @FXML
    private void showStock() {
        load("/api/stock/movements/activity", false);
    }

    @FXML
    private void showOrders() {
        load("/api/purchase-orders/events", true);
    }

    private void load(String path, boolean isOrders) {
        historyContainer.getChildren().clear();
        historyContainer.getChildren().add(new Label("Carregando atividades..."));

        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception { return ApiClient.get(path); }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            historyContainer.getChildren().clear();
            JsonNode data = task.getValue();
            if (data == null || !data.isArray() || data.isEmpty()) {
                setMessage(isOrders ? "Nenhum evento de pedido registrado ainda."
                                    : "Nenhuma atividade de estoque registrada ainda.");
                return;
            }
            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.CENTER);

            for (JsonNode node : data) {
                HistoryLogCard card = new HistoryLogCard();
                if (isOrders) {
                    // injeta occurredAt como "date" para reusar o parser de data
                    if (node instanceof ObjectNode obj && node.has("occurredAt")) {
                        obj.set("date", node.get("occurredAt"));
                    }
                    card.setOrderEvent(node);
                } else {
                    String type = node.path("type").asText("ENTRADA");
                    HistoryLog.MovementType movType = "SAIDA".equals(type)
                            ? HistoryLog.MovementType.SAIDA : HistoryLog.MovementType.ENTRADA;
                    card.setData(HistoryLog.fromJson(node, movType));
                }
                card.prefWidthProperty().bind(historyContainer.widthProperty());
                flow.getChildren().add(card);
            }
            historyContainer.getChildren().add(flow);
        }));

        task.setOnFailed(e -> Platform.runLater(() ->
                setMessage("Erro ao carregar histórico: " + task.getException().getMessage())));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setMessage(String message) {
        historyContainer.getChildren().clear();
        Label l = new Label(message);
        l.setWrapText(true);
        historyContainer.getChildren().add(l);
    }
}
```

> Com `obj.set("date", occurredAt)`, o método `setOrderEvent` pode simplificar o cálculo de `when` para `HistoryLog.fromJson(node, ...).getDate()` — que é exatamente o que está no Step 2. Mantenha consistente.

- [ ] **Step 4: Compilar**

Run: `cd front && mvn -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add front/src/main/resources/com/fawkes/front/view/history-page.fxml \
        front/src/main/java/com/fawkes/front/controller/HistoryPageController.java \
        front/src/main/java/com/fawkes/front/components/HistoryLogCard.java
git commit -m "feat(front): aba Pedidos no historico com eventos de status

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 13: Verificação manual end-to-end

**Files:** nenhum (validação).

- [ ] **Step 1: Subir a API**

Run: `cd api && ./mvnw -q spring-boot:run` (em um terminal separado)
Expected: app sobe sem erro; o Hibernate cria a coluna `problem_justification`.

- [ ] **Step 2: Rodar o front**

Run: `cd front && mvn -q javafx:run` (ou o alvo de run configurado no `front/pom.xml`)
Expected: login normal.

- [ ] **Step 3: Cotação editável**
  - Como **Gerente**: abrir um pedido `pending` → "Registrar Cotação" → vira "Em Cotação".
  - Reabrir o pedido (`quoted`): aparece **"Editar Cotação"** (gerente e diretor) e os preços vêm pré-preenchidos. Editar, salvar → continua "Em Cotação".
  - Como **Diretor**: além de "Editar Cotação", vê "Marcar como Comprado" e "Negar Pedido".

- [ ] **Step 4: Botões** — no detalhe em `quoted` (diretor), os 3 botões aparecem com texto **completo**, sem cortar.

- [ ] **Step 5: DatePicker** — "Marcar como Comprado" mostra o DatePicker (default hoje+3). Já "Enviar", "Receber", "Problema" e "Recusar" **não** mostram DatePicker.

- [ ] **Step 6: NF numérica** — na tela de Receber, o campo de NF aceita só dígitos (tentar letras: bloqueado); vazio bloqueia o envio.

- [ ] **Step 7: Justificativas** — aprovar com texto → "Justificativa da compra" aparece no detalhe. Depois reportar problema com texto → "Justificativa do problema" aparece **sem apagar** a da compra. A observação do solicitante (`notes`) permanece.

- [ ] **Step 8: Data prevista** — após "Marcar como Comprado", a data prevista aparece no detalhe do pedido **e** no card da lista.

- [ ] **Step 9: Histórico** — página Histórico mostra abas "Estoque" e "Pedidos". A aba "Pedidos" lista as transições (data, responsável, justificativa quando houver), mais recentes primeiro.

- [ ] **Step 10: Commit final (se necessário) / encerrar**

Se tudo passou, nada a commitar. Caso ajustes pontuais tenham sido feitos durante a verificação, commitar com mensagem descritiva.
