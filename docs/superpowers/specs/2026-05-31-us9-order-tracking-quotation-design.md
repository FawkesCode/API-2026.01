# Design: US9 — Rastreamento de Pedidos + Fluxo de Cotação

**Data:** 2026-05-31  
**Scope:** Backend (Spring Boot) + Frontend (JavaFX)  
**Sprint:** 3 (entrega 31/05/2026)

---

## Problema

O fluxo de pedidos atual não suporta cotação: preços são obrigatórios na criação do item, mas na prática o gerente só obtém preços *depois* de enviar o pedido ao fornecedor. Além disso, não existe endpoint de edição de pedido pendente (CA3 da US9) nem filtro visual por status no front.

---

## Máquina de Estados

```
[draft] → submit → [pending] → fillPrices → [quoted] → confirm → [confirmed] → ship → [shipped] → receive → [received]
                                                                                                               ↓
                                                                                                           [problem] → return → [returned]
Qualquer estado (exceto received/cancelled) → cancel → [cancelled]
overdue: calculado dinamicamente quando shipped + expectedDeliveryDate < now
```

### Regras de edição
- `PUT /{id}` (metadados: notes, expectedDeliveryDate): permitido em `draft` e `pending` apenas
- `PUT /{id}/items/prices`: permitido apenas em `pending`; transita para `quoted`
- Estados `quoted` em diante: somente leitura

---

## Status — Labels e Cores

| Enum       | Label PT-BR                  | Cor hex   |
|------------|------------------------------|-----------|
| draft      | Rascunho                     | `#6B7280` |
| pending    | Sob Revisão                  | `#F59E0B` |
| quoted     | Em Cotação                   | `#3B82F6` |
| confirmed  | Aprovado para Compra         | `#10B981` |
| shipped    | Em Trânsito                  | `#8B5CF6` |
| received   | Recebido                     | `#059669` |
| cancelled  | Negado para Compra           | `#EF4444` |
| problem    | Problemas no Recebimento     | `#F97316` |
| returned   | Devolvido                    | `#374151` |
| overdue*   | Em Atraso                    | `#DC2626` |

*overdue calculado via `getEffectiveStatus()`, não é enum.

---

## Backend

### 1. `PurchaseOrder.Status` — adicionar `quoted`
```java
public enum Status {
    draft, pending, quoted, confirmed, shipped, received, cancelled, problem, returned
}
```

### 2. `PurchaseOrderItem` — unitPrice opcional
- `unitPrice` e `totalPrice` passam a ter default `BigDecimal.ZERO`
- `addItem` não deve lançar erro se `unitPrice == 0`

### 3. Novo endpoint: `PUT /api/purchase-orders/{id}/items/prices`
- **Payload:** `[{ "itemId": Long, "unitPrice": BigDecimal }]`
- **Validação:** status deve ser `pending`; caso contrário → 400
- **Efeito:** atualiza `unitPrice`, `totalPrice` por item; recalcula `totalValue`; transita para `quoted`
- **Permissão:** MANAGER / DIRECTOR

### 4. Novo endpoint: `PUT /api/purchase-orders/{id}`
- **Payload:** `{ "notes": String, "expectedDeliveryDate": LocalDateTime }`
- **Validação:** status deve ser `draft` ou `pending`; caso contrário → 400 "Pedidos em cotação ou já aprovados não podem ser editados"
- **Permissão:** MANAGER / DIRECTOR

### 5. `confirmOrder` — validação de status
- Deve exigir status `quoted` (lança `RegraDeNegocioException` se não for)

### 6. DTOs novos
- `UpdateOrderRequest`: `notes`, `expectedDeliveryDate`
- `UpdateItemPricesRequest`: `List<ItemPriceEntry>` com `itemId` + `unitPrice`

---

## Frontend

### `Order.getStatusLabel()` — adicionar `quoted`
```java
case "quoted" -> "Em Cotação";
```

### `Order.getStatusColor()` — novo método
Retorna a cor hex conforme tabela acima para uso nos badges.

### `OrdersCard` — badge de status colorido
- Substituir texto simples por Label com `-fx-background-color` e `-fx-text-fill: white` conforme cor do status

### `OrdersPageController` — filtros por status
- Adicionar `HBox` com botões: "Todos | Sob Revisão | Em Cotação | Aprovado | Em Trânsito | Recebido | Negado"
- Clicar filtra `allRequests` localmente sem nova chamada à API

### `PendingRequestForm.renderActions()` — caso `pending` (gerente/diretor)
```
[Registrar Cotação]   [Negar Pedido]
```

### `PendingRequestForm.renderActions()` — caso `quoted` (gerente/diretor)
```
[✓ Aprovar para Compra]   [✗ Negar Pedido]
```

### Novo `QuoteRequestForm.java`
- Lista itens do pedido (nome, quantidade)
- Campo `TextField` de preço unitário por item (pré-preenchido com 0 ou valor existente)
- Botão "Confirmar Cotação" → `PUT /{id}/items/prices` → fecha modal → recarrega lista
- Validação: todos os preços devem ser > 0 antes de submeter

---

## CAs da US9 atendidos

| CA | O que cobre | Atendido? |
|----|-------------|-----------|
| CA1 | Listagem com filtro por status | ✅ Filtros por botão no front |
| CA3 | Edição de pedido pendente | ✅ `PUT /{id}` (notes/data) |
| CA4 | Bloqueio de edição pós-aprovação | ✅ Validação no service |
| CA7 | Validação de campos na edição | ✅ Preço > 0, campos obrigatórios |

## CAs fora do escopo (prazo)

| CA | Motivo |
|----|--------|
| CA2 | Timeline/histórico — requer tabela de auditoria nova |
| CA5 | Dashboard KPIs por status — dashboard já existente, sem tempo para expandir |
| CA6 | Indicador de urgência (X dias pendente) — demonstrativo; `overdue` já cobre shipped |
