# US9 — Rastreamento de Pedidos + Fluxo de Cotação — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar o fluxo de cotação (pending → quoted → confirmed) com status em PT-BR coloridos no frontend, endpoint de preenchimento de preços, edição de pedido pendente, e filtros de status na tela de pedidos.

**Architecture:** Backend adiciona enum `quoted` e dois endpoints PUT no `PurchaseOrderController`/`PurchaseOrderService`. Frontend adiciona método `getStatusColor()` no modelo `Order`, atualiza `StringUtils`, colore badges no `OrdersCard`, adiciona filtros por status no `OrdersPageController`, e cria `QuoteRequestForm` com FXML próprio para o gerente preencher preços.

**Tech Stack:** Java 17, Spring Boot, Lombok, Jakarta Persistence, JavaFX 17, JFoenix, Jackson ObjectMapper.

---

## Mapa de Arquivos

| Arquivo | Ação |
|---|---|
| `api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java` | Modificar — adicionar `quoted` ao enum `Status` |
| `api/src/main/java/com/fawkes/api/Entities/PurchaseOrderItem.java` | Modificar — `unitPrice` com default `ZERO` |
| `api/src/main/java/com/fawkes/api/DTOs/Request/UpdateOrderRequest.java` | Criar — record com `notes` e `expectedDeliveryDate` |
| `api/src/main/java/com/fawkes/api/DTOs/Request/UpdateItemPricesRequest.java` | Criar — record com lista de `ItemPriceEntry` |
| `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java` | Modificar — `updateOrder`, `fillItemPrices`, `confirmOrder` |
| `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java` | Modificar — `PUT /{id}` e `PUT /{id}/items/prices` |
| `front/src/main/java/com/fawkes/front/utils/StringUtils.java` | Modificar — `requestStatusTranslation` + `getStatusColor` |
| `front/src/main/java/com/fawkes/front/models/Order.java` | Modificar — `getStatusLabel()` + `getStatusColor()` |
| `front/src/main/java/com/fawkes/front/components/OrdersCard.java` | Modificar — usar `getStatusColor()` + labels PT-BR |
| `front/src/main/java/com/fawkes/front/controller/OrdersPageController.java` | Modificar — filtros de status por botão |
| `front/src/main/resources/com/fawkes/front/view/orders-page.fxml` | Modificar — adicionar HBox de filtros |
| `front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java` | Modificar — casos `pending` e `quoted` com Registrar Cotação |
| `front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java` | Criar — controller do formulário de cotação |
| `front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml` | Criar — FXML com campos de preço por item |

---

## Task 1: Enum `quoted` + `unitPrice` opcional

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java:16-19`
- Modify: `api/src/main/java/com/fawkes/api/Entities/PurchaseOrderItem.java:27-35`

- [ ] **Step 1: Adicionar `quoted` ao enum `Status` em `PurchaseOrder`**

Em `PurchaseOrder.java`, linha 16–19, alterar de:
```java
public enum Status {
    draft, pending, confirmed, shipped, received, cancelled,
    problem, returned
}
```
para:
```java
public enum Status {
    draft, pending, quoted, confirmed, shipped, received, cancelled,
    problem, returned
}
```

- [ ] **Step 2: Tornar `unitPrice` e `totalPrice` não obrigatórios em `PurchaseOrderItem`**

Em `PurchaseOrderItem.java`, alterar as anotações das colunas de preço:
```java
@Column(name = "unit_price", precision = 10, scale = 2)
private BigDecimal unitPrice = BigDecimal.ZERO;

@Column(name = "total_price", precision = 10, scale = 2)
private BigDecimal totalPrice = BigDecimal.ZERO;
```
(remover `nullable = false` de ambas)

- [ ] **Step 3: Ajustar `addItem` no service para aceitar preço zero**

Em `PurchaseOrderService.java`, o método `addItem` já calcula `totalPrice = unitPrice * qty`. Garantir que funciona com `BigDecimal.ZERO` (funciona — nenhuma mudança necessária, mas verificar que não há validação de preço > 0 no método).

- [ ] **Step 4: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Entities/PurchaseOrder.java
git add api/src/main/java/com/fawkes/api/Entities/PurchaseOrderItem.java
git commit -m "feat: adiciona status quoted e torna unitPrice opcional no item"
```

---

## Task 2: DTOs de Request

**Files:**
- Create: `api/src/main/java/com/fawkes/api/DTOs/Request/UpdateOrderRequest.java`
- Create: `api/src/main/java/com/fawkes/api/DTOs/Request/UpdateItemPricesRequest.java`

- [ ] **Step 1: Criar `UpdateOrderRequest`**

```java
package com.fawkes.api.DTOs.Request;

import java.time.LocalDateTime;

public record UpdateOrderRequest(
    String notes,
    LocalDateTime expectedDeliveryDate
) {}
```

- [ ] **Step 2: Criar `UpdateItemPricesRequest`**

```java
package com.fawkes.api.DTOs.Request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateItemPricesRequest(
    List<ItemPriceEntry> items
) {
    public record ItemPriceEntry(Long itemId, BigDecimal unitPrice) {}
}
```

- [ ] **Step 3: Commit**
```bash
git add api/src/main/java/com/fawkes/api/DTOs/Request/UpdateOrderRequest.java
git add api/src/main/java/com/fawkes/api/DTOs/Request/UpdateItemPricesRequest.java
git commit -m "feat: DTOs UpdateOrderRequest e UpdateItemPricesRequest"
```

---

## Task 3: Service — `updateOrder` e `fillItemPrices`

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java`

- [ ] **Step 1: Adicionar import de `PurchaseOrderItemRepository` ou usar a lista de itens**

O `PurchaseOrderItem` já é acessível via `order.getItems()`. Não é necessário repositório separado — os itens são gerenciados via cascade. Adicionar import:
```java
import com.fawkes.api.DTOs.Request.UpdateOrderRequest;
import com.fawkes.api.DTOs.Request.UpdateItemPricesRequest;
```

- [ ] **Step 2: Adicionar método `updateOrder`**

No final da classe `PurchaseOrderService`, antes do último `}`:
```java
@Transactional
public PurchaseOrder updateOrder(Long orderId, UpdateOrderRequest request) {
    PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

    if (order.getStatus() != PurchaseOrder.Status.draft
            && order.getStatus() != PurchaseOrder.Status.pending) {
        throw new RegraDeNegocioException(
                "Pedidos em cotação ou já aprovados não podem ser editados");
    }

    if (request.notes() != null) {
        order.setNotes(request.notes());
    }
    if (request.expectedDeliveryDate() != null) {
        order.setExpectedDeliveryDate(request.expectedDeliveryDate());
    }

    return purchaseOrderRepository.save(order);
}
```

- [ ] **Step 3: Adicionar método `fillItemPrices`**

```java
@Transactional
public PurchaseOrder fillItemPrices(Long orderId, UpdateItemPricesRequest request) {
    PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

    if (order.getStatus() != PurchaseOrder.Status.pending) {
        throw new RegraDeNegocioException(
                "Só é possível registrar cotação em pedidos com status 'Sob Revisão'");
    }

    for (UpdateItemPricesRequest.ItemPriceEntry entry : request.items()) {
        PurchaseOrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(entry.itemId()))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Item " + entry.itemId() + " não pertence a este pedido"));

        if (entry.unitPrice() == null || entry.unitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException(
                    "Preço do item " + entry.itemId() + " deve ser maior que zero");
        }

        item.setUnitPrice(entry.unitPrice());
        item.setTotalPrice(entry.unitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    recalculateTotal(order);
    order.setStatus(PurchaseOrder.Status.quoted);
    return purchaseOrderRepository.save(order);
}
```

- [ ] **Step 4: Adicionar validação de status `quoted` em `confirmOrder`**

Localizar o método `confirmOrder` (linha ~112) e substituir por:
```java
@Transactional
public PurchaseOrder confirmOrder(Long orderId) {
    PurchaseOrder order = purchaseOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    if (order.getStatus() != PurchaseOrder.Status.quoted) {
        throw new RegraDeNegocioException(
                "Só é possível aprovar pedidos com cotação registrada (status 'Em Cotação')");
    }

    order.setStatus(PurchaseOrder.Status.confirmed);
    return purchaseOrderRepository.save(order);
}
```

- [ ] **Step 5: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Services/PurchaseOrderService.java
git commit -m "feat: service updateOrder, fillItemPrices e validacao confirmOrder"
```

---

## Task 4: Controller — novos endpoints PUT

**Files:**
- Modify: `api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java`

- [ ] **Step 1: Adicionar imports necessários**

No topo de `PurchaseOrderController.java`, adicionar:
```java
import com.fawkes.api.DTOs.Request.UpdateOrderRequest;
import com.fawkes.api.DTOs.Request.UpdateItemPricesRequest;
import org.springframework.http.ResponseEntity;
```
(`ResponseEntity` e `List` já são importados.)

- [ ] **Step 2: Adicionar endpoint `PUT /{id}`**

Após o endpoint `GET /{id}`, adicionar:
```java
@PutMapping("/{id}")
public ResponseEntity<PurchaseOrder> update(
        @PathVariable Long id,
        @RequestBody UpdateOrderRequest request) {
    return ResponseEntity.ok(purchaseOrderService.updateOrder(id, request));
}
```

- [ ] **Step 3: Adicionar endpoint `PUT /{id}/items/prices`**

Após o endpoint acima:
```java
@PutMapping("/{id}/items/prices")
public ResponseEntity<PurchaseOrder> fillItemPrices(
        @PathVariable Long id,
        @RequestBody UpdateItemPricesRequest request) {
    return ResponseEntity.ok(purchaseOrderService.fillItemPrices(id, request));
}
```

- [ ] **Step 4: Verificar que a API compila e sobe**

```bash
cd api
./mvnw spring-boot:run
```
Aguardar "Started ApiApplication". Se houver erro de migração de banco (novo valor de enum), ver Task 5.

- [ ] **Step 5: Testar endpoints manualmente (cURL ou Postman)**

```bash
# Criar draft
curl -s -X POST http://localhost:8080/api/purchase-orders/draft \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"supplierId":1,"userId":1}'

# Adicionar item sem preço (preço 0)
curl -s -X POST http://localhost:8080/api/purchase-orders/1/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"productId":1,"quantity":2,"unitPrice":0}'

# Submit → pending
curl -s -X POST http://localhost:8080/api/purchase-orders/1/submit \
  -H "Authorization: Bearer <TOKEN>" -d '{}'

# Preencher preços → quoted
curl -s -X PUT http://localhost:8080/api/purchase-orders/1/items/prices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"items":[{"itemId":1,"unitPrice":50.00}]}'

# Confirmar → confirmed
curl -s -X POST http://localhost:8080/api/purchase-orders/1/confirm \
  -H "Authorization: Bearer <TOKEN>" -d '{}'
```
Esperado: status muda de `pending` → `quoted` → `confirmed` em cada passo.

- [ ] **Step 6: Commit**
```bash
git add api/src/main/java/com/fawkes/api/Controllers/PurchaseOrderController.java
git commit -m "feat: endpoints PUT purchase-orders/{id} e /{id}/items/prices"
```

---

## Task 5: Frontend — StringUtils e Order model

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/utils/StringUtils.java`
- Modify: `front/src/main/java/com/fawkes/front/models/Order.java`

- [ ] **Step 1: Atualizar `StringUtils.requestStatusTranslation` com todos os labels PT-BR**

Substituir o método inteiro em `StringUtils.java`:
```java
public static String requestStatusTranslation(String status) {
    return switch (status != null ? status : "") {
        case "draft"     -> "Rascunho";
        case "pending"   -> "Sob Revisão";
        case "quoted"    -> "Em Cotação";
        case "confirmed" -> "Aprovado para Compra";
        case "shipped"   -> "Em Trânsito";
        case "received"  -> "Recebido";
        case "cancelled" -> "Negado para Compra";
        case "overdue"   -> "Em Atraso";
        case "problem"   -> "Problemas no Recebimento";
        case "returned"  -> "Devolvido";
        default          -> status;
    };
}
```

- [ ] **Step 2: Adicionar `getStatusColor` em `StringUtils`**

Após o método `requestStatusTranslation`, adicionar:
```java
public static String getStatusColor(String status) {
    return switch (status != null ? status : "") {
        case "draft"     -> "#6B7280";
        case "pending"   -> "#F59E0B";
        case "quoted"    -> "#3B82F6";
        case "confirmed" -> "#10B981";
        case "shipped"   -> "#8B5CF6";
        case "received"  -> "#059669";
        case "cancelled" -> "#EF4444";
        case "overdue"   -> "#DC2626";
        case "problem"   -> "#F97316";
        case "returned"  -> "#374151";
        default          -> "#6B7280";
    };
}
```

- [ ] **Step 3: Atualizar `Order.getStatusLabel()` para incluir `quoted`**

Em `Order.java`, substituir o método `getStatusLabel()`:
```java
public String getStatusLabel() {
    return StringUtils.requestStatusTranslation(status);
}
```
(Delegar para `StringUtils` em vez de duplicar a lógica.)

Adicionar import no topo de `Order.java`:
```java
import com.fawkes.front.utils.StringUtils;
```

- [ ] **Step 4: Adicionar `getStatusColor()` em `Order`**

```java
public String getStatusColor() {
    return StringUtils.getStatusColor(getEffectiveStatus());
}
```

- [ ] **Step 5: Commit**
```bash
git add front/src/main/java/com/fawkes/front/utils/StringUtils.java
git add front/src/main/java/com/fawkes/front/models/Order.java
git commit -m "feat: labels PT-BR e cores por status em StringUtils e Order"
```

---

## Task 6: OrdersCard — badge colorido

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/components/OrdersCard.java`

- [ ] **Step 1: Simplificar `setData` usando `StringUtils`**

No método `setData(Order order)`, substituir o bloco `status.getStyleClass()...switch` inteiro por:
```java
String effectiveStatus = order.getEffectiveStatus();
this.status.setText(StringUtils.requestStatusTranslation(effectiveStatus));
String color = StringUtils.getStatusColor(effectiveStatus);
status.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
        + "-fx-background-radius: 4; -fx-padding: 2 8 2 8;");
```

O bloco `switch` manual com as cores antigas (`#07b0f3`, `#7F77DD`, etc.) deve ser **removido** — substituído pelas duas linhas acima.

- [ ] **Step 2: Commit**
```bash
git add front/src/main/java/com/fawkes/front/components/OrdersCard.java
git commit -m "feat: badge de status colorido no OrdersCard usando paleta unificada"
```

---

## Task 7: OrdersPageController — filtros por status

**Files:**
- Modify: `front/src/main/resources/com/fawkes/front/view/orders-page.fxml`
- Modify: `front/src/main/java/com/fawkes/front/controller/OrdersPageController.java`

- [ ] **Step 1: Adicionar HBox de filtros no FXML `orders-page.fxml`**

Dentro do `<VBox fx:id="pageContent"...>`, logo após o `<HBox alignment="CENTER_LEFT"...>` que contém os labels de status e o botão de novo pedido, adicionar:

```xml
<HBox spacing="8.0" alignment="CENTER_LEFT">
   <children>
      <JFXButton fx:id="filterAll"      onAction="#filterByStatus" text="Todos"              styleClass="btn--filter" userData="all" />
      <JFXButton fx:id="filterPending"  onAction="#filterByStatus" text="Sob Revisão"        styleClass="btn--filter" userData="pending" />
      <JFXButton fx:id="filterQuoted"   onAction="#filterByStatus" text="Em Cotação"         styleClass="btn--filter" userData="quoted" />
      <JFXButton fx:id="filterConfirmed" onAction="#filterByStatus" text="Aprovado"          styleClass="btn--filter" userData="confirmed" />
      <JFXButton fx:id="filterShipped"  onAction="#filterByStatus" text="Em Trânsito"        styleClass="btn--filter" userData="shipped" />
      <JFXButton fx:id="filterReceived" onAction="#filterByStatus" text="Recebido"           styleClass="btn--filter" userData="received" />
      <JFXButton fx:id="filterCancelled" onAction="#filterByStatus" text="Negado"            styleClass="btn--filter" userData="cancelled" />
   </children>
</HBox>
```

Adicionar o import no topo do FXML (já existe `<?import com.jfoenix.controls.JFXButton?>`).

- [ ] **Step 2: Adicionar `filterByStatus` em `OrdersPageController`**

```java
@FXML
private void filterByStatus(javafx.event.ActionEvent event) {
    String statusKey = (String) ((com.jfoenix.controls.JFXButton) event.getSource()).getUserData();
    if ("all".equals(statusKey)) {
        renderOrders(allRequests);
    } else {
        List<Order> filtered = allRequests.stream()
                .filter(o -> statusKey.equals(o.getStatus()))
                .toList();
        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum pedido com status \"" 
                + StringUtils.requestStatusTranslation(statusKey) + "\".");
        } else {
            renderOrders(filtered);
        }
    }
}
```

Adicionar o import em `OrdersPageController.java`:
```java
import com.fawkes.front.utils.StringUtils;
```

- [ ] **Step 3: Commit**
```bash
git add front/src/main/resources/com/fawkes/front/view/orders-page.fxml
git add front/src/main/java/com/fawkes/front/controller/OrdersPageController.java
git commit -m "feat: filtros de status por botao na tela de pedidos"
```

---

## Task 8: PendingRequestForm — casos `pending` e `quoted`

**Files:**
- Modify: `front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java`

- [ ] **Step 1: Adicionar import de `QuoteRequestForm`**

No topo de `PendingRequestForm.java`, adicionar:
```java
import com.fawkes.front.controller.QuoteRequestForm;
```

- [ ] **Step 2: Substituir o caso `"pending"` em `renderActions`**

Localizar o bloco `case "pending" -> { ... }` (linha ~88) e substituir por:
```java
case "pending" -> {
    if (isDirectorManager) {
        btnActionContainer.getChildren().addAll(
                makeBtn("📋  Registrar Cotação", "btn--info", this::handleQuote),
                makeBtn("✗  Negar Pedido", "btn--danger", this::handleDeclined)
        );
    } else {
        btnActionContainer.getChildren().add(infoLabel("⏳  Sob revisão — aguardando cotação"));
    }
}
```

- [ ] **Step 3: Adicionar o caso `"quoted"` em `renderActions`**

Logo após o bloco `case "pending"`, adicionar:
```java
case "quoted" -> {
    if (isDirectorManager) {
        btnActionContainer.getChildren().addAll(
                makeBtn("✓  Aprovar para Compra", "btn--submit", this::handleAproved),
                makeBtn("✗  Negar Pedido", "btn--danger", this::handleDeclined)
        );
    } else {
        btnActionContainer.getChildren().add(infoLabel("📋  Cotação registrada — aguardando aprovação"));
    }
}
```

- [ ] **Step 4: Adicionar método `handleQuote`**

```java
private void handleQuote() {
    abrirSubModal(new QuoteRequestForm(), "Registrar Cotação — Pedido " + order.getId());
}
```

- [ ] **Step 5: Atualizar `abrirSubModal` para suportar `QuoteRequestForm`**

No método `abrirSubModal`, dentro do bloco de ifs de controller, adicionar após o `else if (controller instanceof ProblemRequestForm p)`:
```java
else if (controller instanceof QuoteRequestForm q) {
    q.setData(order);
    q.setOnSaveSuccess(onSaveSuccess);
}
```

E para o `QuoteRequestForm`, o FXML é diferente — editar o `abrirSubModal` para detectar qual FXML usar:

```java
private void abrirSubModal(Object controller, String titulo) {
    try {
        String fxmlPath = (controller instanceof QuoteRequestForm)
                ? "/com/fawkes/front/view/forms/quote-request-form.fxml"
                : "/com/fawkes/front/view/forms/director-request-form.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setController(controller);
        Parent formulario = loader.load();

        if (controller instanceof AproveRequestForm a) {
            a.setData(order); a.setOnSaveSuccess(onSaveSuccess);
        } else if (controller instanceof DeclineRequestForm d) {
            d.setData(order); d.setOnSaveSuccess(onSaveSuccess);
        } else if (controller instanceof ShipRequestForm s) {
            s.setData(order); s.setOnSaveSuccess(onSaveSuccess);
        } else if (controller instanceof ReceiveRequestForm r) {
            r.setData(order); r.setOnSaveSuccess(onSaveSuccess);
        } else if (controller instanceof ProblemRequestForm p) {
            p.setData(order); p.setOnSaveSuccess(onSaveSuccess);
        } else if (controller instanceof QuoteRequestForm q) {
            q.setData(order); q.setOnSaveSuccess(onSaveSuccess);
        }

        Stage stageAtual = (Stage) btnActionContainer.getScene().getWindow();
        double height = (controller instanceof QuoteRequestForm) ? 420.0 : 350.0;
        Platform.runLater(() -> {
            stageAtual.close();
            ModalManager.openModal(curStage, formulario, titulo,
                    700.0, height, "ModalFrameM_heightSM.fxml", false);
        });

    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("CAUSA: " + e.getCause());
    }
}
```

- [ ] **Step 6: Commit**
```bash
git add front/src/main/java/com/fawkes/front/controller/PendingRequestForm.java
git commit -m "feat: PendingRequestForm suporta estado quoted e abre QuoteRequestForm"
```

---

## Task 9: QuoteRequestForm — FXML + Controller

**Files:**
- Create: `front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml`
- Create: `front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java`

- [ ] **Step 1: Criar o FXML `quote-request-form.fxml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import com.jfoenix.controls.JFXButton?>
<?import java.lang.String?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ScrollPane?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<AnchorPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity"
            prefHeight="390.0" prefWidth="673.0"
            xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1">
   <children>
      <VBox alignment="TOP_CENTER" layoutX="16.0" layoutY="12.0" prefWidth="642.0"
            AnchorPane.bottomAnchor="60.0" AnchorPane.leftAnchor="16.0"
            AnchorPane.rightAnchor="16.0" AnchorPane.topAnchor="12.0" spacing="8.0">
         <children>
            <Label styleClass="input__label" text="Preencha o preço unitário para cada item:" />
            <ScrollPane fitToWidth="true" prefHeight="280.0" VBox.vgrow="ALWAYS">
               <content>
                  <VBox fx:id="itemsContainer" spacing="10.0" style="-fx-padding: 8;" />
               </content>
            </ScrollPane>
            <Label fx:id="errorLabel" prefWidth="640.0" style="-fx-text-fill: #EF4444;" />
         </children>
      </VBox>
      <HBox alignment="CENTER_RIGHT" spacing="15.0" prefHeight="42.0" prefWidth="673.0"
            AnchorPane.bottomAnchor="12.0" AnchorPane.leftAnchor="16.0" AnchorPane.rightAnchor="16.0">
         <children>
            <JFXButton fx:id="btnConfirm" onAction="#handleSubmit" prefHeight="26.0" prefWidth="160.0"
                       styleClass="btn--submit" text="Confirmar Cotação" />
            <JFXButton fx:id="btnCancel" onAction="#handleCloseModal" prefHeight="26.0" prefWidth="100.0"
                       styleClass="btn--cancel" text="Cancelar" />
         </children>
      </HBox>
   </children>
</AnchorPane>
```

- [ ] **Step 2: Criar o controller `QuoteRequestForm.java`**

```java
package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuoteRequestForm {

    @FXML private VBox      itemsContainer;
    @FXML private Label     errorLabel;
    @FXML private JFXButton btnConfirm;
    @FXML private JFXButton btnCancel;

    private Order    order;
    private Runnable onSaveSuccess;

    // Mantém (itemId, TextField de preço) para leitura no submit
    private final List<long[]>      itemIds    = new ArrayList<>();
    private final List<TextField>   priceFields = new ArrayList<>();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void setData(Order order) {
        this.order = order;
        itemsContainer.getChildren().clear();
        itemIds.clear();
        priceFields.clear();

        for (RequestItem item : order.getItemsList()) {
            Label name = new Label(item.getProduct().getName());
            name.getStyleClass().add("input__label--info");
            name.setPrefWidth(220);

            Label qty = new Label("x " + item.getQuantity());
            qty.getStyleClass().add("input__label--info");

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            TextField priceField = new TextField(
                    item.getUnitPrice() > 0
                            ? String.format("%.2f", item.getUnitPrice()).replace(",", ".")
                            : "");
            priceField.setPromptText("Preço unitário (R$)");
            priceField.setPrefWidth(130);

            HBox row = new HBox(8, name, qty, spacer, priceField);
            row.setAlignment(Pos.CENTER_LEFT);
            itemsContainer.getChildren().add(row);

            itemIds.add(new long[]{item.getId()});
            priceFields.add(priceField);
        }
    }

    @FXML
    private void handleSubmit() {
        errorLabel.setText("");
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            ArrayNode items = mapper.createArrayNode();

            for (int i = 0; i < itemIds.size(); i++) {
                String raw = priceFields.get(i).getText().trim().replace(",", ".");
                if (raw.isEmpty()) {
                    errorLabel.setText("Preencha o preço de todos os itens.");
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    errorLabel.setText("Preço inválido: \"" + raw + "\".");
                    return;
                }
                if (price <= 0) {
                    errorLabel.setText("O preço deve ser maior que zero.");
                    return;
                }
                ObjectNode entry = mapper.createObjectNode();
                entry.put("itemId", itemIds.get(i)[0]);
                entry.put("unitPrice", price);
                items.add(entry);
            }

            body.set("items", items);
            ApiClient.put("/api/purchase-orders/" + order.getId() + "/items/prices",
                    mapper.writeValueAsString(body));

            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) btnConfirm.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
```

- [ ] **Step 3: Verificar que `RequestItem.getUnitPrice()` existe**

Em `front/src/main/java/com/fawkes/front/models/RequestItem.java`, confirmar que há um getter `getUnitPrice()` retornando `double`. Se não existir, adicionar:
```java
public double getUnitPrice() { return unitPrice; }
```

- [ ] **Step 4: Commit**
```bash
git add front/src/main/resources/com/fawkes/front/view/forms/quote-request-form.fxml
git add front/src/main/java/com/fawkes/front/controller/QuoteRequestForm.java
git commit -m "feat: QuoteRequestForm FXML e controller para registro de cotacao"
```

---

## Task 10: Verificação end-to-end

- [ ] **Step 1: Subir API**
```bash
cd api && ./mvnw spring-boot:run
```
Aguardar "Started ApiApplication" no log.

- [ ] **Step 2: Subir frontend**
```bash
cd front && ./mvnw javafx:run
```

- [ ] **Step 3: Fluxo completo — golden path**
1. Login como Operacional → abrir novo pedido com produtos (sem preço ou com preço 0)
2. Submeter o pedido → pedido aparece com badge **"Sob Revisão"** (âmbar)
3. Login como Gerente → acessar tela de Pedidos
4. Filtrar por "Sob Revisão" → pedido aparece
5. Abrir pedido → botão "Registrar Cotação" visível
6. Clicar → `QuoteRequestForm` abre → preencher preços → clicar "Confirmar Cotação"
7. Pedido volta na lista com badge **"Em Cotação"** (azul)
8. Abrir pedido → botões "Aprovar para Compra" e "Negar Pedido"
9. Clicar "Aprovar para Compra" → pedido vai para **"Aprovado para Compra"** (verde)
10. Fluxo continua: Marcar como Enviado → Em Trânsito (roxo) → Receber → Recebido (verde escuro)

- [ ] **Step 4: Verificar bloqueio de edição (CA4)**
- Abrir pedido com status "Em Cotação" ou "Aprovado" no backend e tentar `PUT /{id}` → deve retornar 400 com mensagem correta.

- [ ] **Step 5: Commit final**
```bash
git add -A
git commit -m "chore: verificacao e2e fluxo cotacao US9 concluida"
```
