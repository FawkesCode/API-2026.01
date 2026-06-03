# Design — Correção do fluxo de Pedidos de Compra (US9 — ajustes)

- **Projeto:** API-2026.01
- **Branch:** fix/pedidos-historico-estoque
- **Data:** 2026-06-01

## Contexto

O fluxo de pedidos (abertura → cotação → aprovação → envio → recebimento) já
existe e cobre a maior parte da US9, mas tem 7 defeitos/ajustes que o tornam
incorreto na prática. Os mais graves: a observação/justificativa digitada pelo
usuário nunca é persistida, há um erro de papéis (gerente aprovando compra) e
uma mudança de semântica pedida pelo negócio ("marcar como comprado" com data de
entrega).

Este spec cobre **somente os pedidos**. O histórico/timeline de status por pedido
(US9 CA2) fica para um plano seguinte e separado.

## Decisões de negócio confirmadas

1. **Papéis:** Gerente (MANAGER) registra a cotação; apenas o Diretor (DIRECTOR)
   aprova / "marca como comprado" e nega o pedido.
2. **Preço:** removido da tela de abertura — o preço passa a existir somente na
   cotação (registrada pelo gerente). A remoção do preço da entidade/CRUD de
   Produto **não** faz parte deste plano (fica no plano de Produtos).
3. **"Marcar como Comprado"** substitui o passo "Aprovar para Compra" (mantém o
   estado interno `confirmed`), capturando data de entrega com previsão default =
   data atual + 3 dias.
4. **Histórico** = plano seguinte.

## Decisões de design (resolvem ambiguidades do relatório original)

- **`notes` vs `decisionReason`:** o `PurchaseOrder` ganha um campo novo
  `decisionReason` (TEXT). `notes` passa a guardar **somente** a observação do
  solicitante (abertura). `decisionReason` guarda a justificativa de
  aprovação/negação **e** o motivo de problema. Isso resolve a inconsistência do
  relatório original (item 1 queria persistir a observação em `notes`, mas
  aprovar/negar/problema também gravavam em `notes`, destruindo a observação).
  `markAsProblem` migra de gravar em `notes` para `decisionReason`.
- **Negação antes da cotação:** em `pending` aparece **apenas** "Registrar
  Cotação". "Negar Pedido" só existe em `quoted` e é exclusivo do DIRECTOR.
- **Reforço de papel DIRECTOR (backend):** feito no `SecurityConfig` por URL
  (padrão já usado no projeto para todas as transições de estado), em vez de
  `@PreAuthorize` no controller ou checagem no service. Mudança centralizada e
  consistente.

## Os 7 ajustes

### 1. Observação/justificativa persistida `[CRÍTICO]`

- **Abertura (`NewRequestForm.handleSubmmit`):** hoje o `descriptionField`
  (TextArea) nunca é lido. Passar a ler `descriptionField.getText()`; após criar
  cada draft e **antes** do `submit`, fazer
  `PUT /api/purchase-orders/{id}` com `{ "notes": <observação> }`. A mesma
  observação é gravada em cada pedido gerado por fornecedor. `updateOrder` já
  aceita `notes` em estado `draft`.
- **Aprovar/Negar (`AproveRequestForm`/`DeclineRequestForm.handleSubmit`):** hoje
  postam `{}`. Passar a enviar `{ "reason": <descriptionField> }` para `/confirm`
  e `/cancel`.
- **API:** `confirmOrder` e `cancelOrder` passam a aceitar um `reason` opcional e
  gravá-lo em `decisionReason`.
- **Exibição:** o model `Order` do front passa a ler `decisionReason`;
  `PendingRequestForm` mostra a observação (`notes`) e a justificativa
  (`decisionReason`) no detalhe do pedido.

### 2. Sem preço na abertura `[CRÍTICO]`

- **Front:** remover exibição/uso de preço em `ShoppingRequestForm`,
  `ProductShopCard`, `RequestedProductCard` e `RequestProductsPageController`;
  `NewRequestForm` envia `unitPrice: 0` no `addItem`.
- **API:** `addItem` já aceita 0 (não há checagem `> 0`). O preço real entra só na
  cotação (`fillItemPrices`, que exige `> 0`). Nenhuma mudança de backend.
- **Fora de escopo:** entidade/CRUD de Produto (campo `unitValue`) — plano de
  Produtos. Aqui só se garante que a abertura não dependa do preço do produto.

### 3. Estilo do modal de cotação `[ATENÇÃO]`

- Refinar `quote-request-form.fxml` (alinhamento, espaçamento, largura dos campos
  de preço, header). A lógica de `QuoteRequestForm` (montagem programática das
  linhas) permanece intacta.

### 4. Justificativa abaixo da descrição `[ATENÇÃO]`

- O `director-request-form.fxml` já posiciona o `descriptionField` abaixo do
  resumo do pedido. Garantir esse posicionamento e — principal — que o campo seja
  **persistido** (ver item 1). Ajuste de layout pequeno.

### 5. Papéis corretos `[CRÍTICO]`

- **`PendingRequestForm.renderActions`:**
  - `pending` → somente "Registrar Cotação" (MANAGER ou DIRECTOR).
  - `quoted` → "Marcar como Comprado" / "Negar Pedido" (**somente DIRECTOR**).
- **Backend (`SecurityConfig`):** mover `/api/purchase-orders/*/confirm` e
  `/api/purchase-orders/*/cancel` do matcher atual
  `hasAnyRole("MANAGER","DIRECTOR")` para `hasRole("DIRECTOR")`. As demais
  transições (`/ship`, `/problem`, `/return`) permanecem como estão.

### 6. "Marcar como Comprado" + data de entrega `[CRÍTICO]`

- **Front:** renomear o botão/sub-modal de "Aprovar para Compra" para "Marcar
  como Comprado"; adicionar um `DatePicker` de data de entrega, com previsão
  default = data atual + 3 dias (pré-preenchida, editável).
- **API:** novo DTO `ConfirmOrderRequest(LocalDateTime expectedDeliveryDate,
  String reason)`. `confirmOrder` passa a receber e persistir
  `expectedDeliveryDate` (gravado no fim do dia, p/ evitar marcar "em atraso" no
  próprio dia da entrega) e `decisionReason`. O endpoint `/confirm` passa a
  receber esse corpo. Estado interno continua `confirmed` (muda só o rótulo + a
  captura de data).

### 7. Filtros "Em Atraso" e "Problema" `[ATENÇÃO]`

- **`orders-page.fxml`:** adicionar botões "Em Atraso" (`userData="overdue"`) e
  "Problema" (`userData="problem"`).
- **`OrdersPageController.filterByStatus`:** para `overdue`, filtrar por
  `o.getEffectiveStatus()`; para `problem`, por `o.getStatus()`. Os demais filtros
  continuam por `getStatus()`.

## Arquivos afetados

**API:**
- `Entities/PurchaseOrder.java` — novo campo `decisionReason`.
- `Services/PurchaseOrderService.java` — `confirmOrder` (data de entrega + reason),
  `cancelOrder` (reason), `markAsProblem` (migrar para `decisionReason`).
- `Controllers/PurchaseOrderController.java` — payloads de `/confirm` e `/cancel`.
- `DTOs/Request/ConfirmOrderRequest.java` — novo DTO.
- `Security/SecurityConfig.java` — `/confirm` e `/cancel` exigem DIRECTOR.

**Front:**
- `controller/NewRequestForm.java` — enviar observação (PUT antes do submit);
  `unitPrice: 0`.
- `controller/ShoppingRequestForm.java`, `components/ProductShopCard`,
  `components/RequestedProductCard`, `controller/RequestProductsPageController.java`
  — remover preço.
- `controller/AproveRequestForm.java` / `DeclineRequestForm.java` — enviar reason;
  "Marcar como Comprado" + `DatePicker` (default +3 dias).
- `controller/PendingRequestForm.java` — `renderActions` (papéis); exibir
  `notes` + `decisionReason`.
- `models/Order.java` — ler `decisionReason`.
- `controller/OrdersPageController.java` — filtros overdue/problem.
- `view/forms/quote-request-form.fxml` — estilo do modal de cotação.
- `view/forms/director-request-form.fxml` — justificativa abaixo da descrição +
  `DatePicker`.
- `view/orders-page.fxml` — botões de filtro.

## Fora de escopo (plano seguinte)

- **US9 CA2 — histórico/timeline de status por pedido:** requer tabela de
  auditoria de transições (quem, quando, de/para qual status, comentário),
  endpoint `GET /{id}/history` e tela de timeline. Será especificado
  separadamente, junto da avaliação da `HistoryPage` atual. O campo
  `decisionReason` introduzido aqui facilita esse trabalho futuro.
- Remoção do preço da entidade/CRUD de Produto — plano de Produtos.

## Verificação (end-to-end)

1. **Observação:** abrir pedido com observação → `notes` persistido e visível no
   detalhe do pedido.
2. **Justificativa:** aprovar/negar com texto → gravado em `decisionReason` e
   visível; a observação original (`notes`) permanece intacta.
3. **Preço:** abrir pedido → sem campo/exibição de preço; itens criados com preço
   0; preço só aparece após a cotação.
4. **Papéis:** logado como MANAGER → só vê "Registrar Cotação" (sem
   aprovar/negar); como DIRECTOR → vê "Marcar como Comprado"/"Negar". Tentativa de
   `confirm`/`cancel` como MANAGER via API → bloqueada (403).
5. **Marcar como comprado:** registrar cotação → como diretor, "Marcar como
   Comprado" com data default = hoje + 3 dias (editável) → `expectedDeliveryDate`
   persistido; estado vira `confirmed`.
6. **Filtros:** "Em Atraso" lista pedidos shipped com entrega vencida; "Problema"
   lista pedidos em problem.
7. **Cotação (UI):** modal de cotação alinhado/estilizado corretamente.
