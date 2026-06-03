# Design — Correção do fluxo de Pedidos (rodada 2) + histórico de status

- **Projeto:** API-2026.01
- **Branch:** fix/pedidos-historico-estoque
- **Data:** 2026-06-02

## Contexto

A rodada anterior de correções do fluxo de Pedidos
(`2026-06-01-pedidos-fix-flow-design.md`) foi implementada, mas o uso revelou 7
defeitos/ajustes remanescentes, além da pendência do histórico de status por
pedido (US9 CA2). Este spec cobre essa segunda rodada.

Causa raiz de boa parte dos bugs de UI: cinco telas de transição
(Aprovar / Recusar / Enviar / Receber / Problema) **compartilham** o mesmo
`director-request-form.fxml`, que contém um `DatePicker` de entrega e um
`TextArea`. Campos vazam para etapas onde não fazem sentido (DatePicker no
envio/recebimento; NF como TextArea no recebimento).

## Decisões confirmadas com o usuário

1. **Justificativas — 3 campos separados:** `notes` (observação do solicitante,
   na abertura), `purchaseJustification` (decisão de compra: aprovar **ou**
   recusar), `problemJustification` (problema no recebimento). Nenhuma sobrescreve
   a outra. A justificativa de recusa vai para `purchaseJustification` (é uma
   decisão sobre a compra).
2. **Cotação editável** por **Gerente e Diretor** enquanto o pedido não for
   aprovado (status `quoted`).
3. **Histórico de status** exibido na **página Histórico** (não no modal do
   pedido), com **abas separadas**: "Estoque" (entradas/saídas, atual) e
   "Pedidos" (eventos de status).
4. **Separação de FXMLs por etapa** (Opção A) em vez de esconder campos por
   controller.
5. **Data de entrega prevista** exibida **no detalhe do pedido E no card da
   lista** (`OrdersCard`).
6. **Editar cotação já registrada** é **edição silenciosa** — não gera novo
   evento no histórico.

## 1. Modelo de dados (API)

`PurchaseOrder` passa a ter três campos de texto distintos:

- `notes` (TEXT) — observação do solicitante (abertura). **Sem mudança.**
- `purchaseJustification` (TEXT) — justificativa da decisão de compra. **Reusa a
  coluna existente `decision_reason`** (apenas renomeia o campo Java, mantendo
  `@Column(name = "decision_reason")`), preservando dados já gravados.
- `problemJustification` (TEXT, **novo** — coluna `problem_justification`) —
  justificativa de problema no recebimento.

Comportamento no `PurchaseOrderService`:

- `confirmOrder` → grava `reason` em `purchaseJustification`.
- `cancelOrder` → grava `reason` em `purchaseJustification`.
- `markAsProblem` → grava `reason` em `problemJustification` (hoje grava em
  `decisionReason`/`decision_reason`, sobrescrevendo a justificativa da compra).

> Migração: `ddl-auto` adiciona a coluna nova; nenhuma migração manual de dados é
> necessária pois `purchaseJustification` reaproveita `decision_reason`.

> **Impacto no front (JSON):** a serialização do `PurchaseOrder` deixa de expor
> `decisionReason` e passa a expor `purchaseJustification` e
> `problemJustification`. O `Order.fromJson` (front), que hoje lê
> `decisionReason`, passa a ler as duas chaves novas; `Order` ganha
> `purchaseJustification`/`problemJustification` no lugar de `decisionReason`. No
> detalhe (`PendingRequestForm`), exibir as duas justificativas quando presentes
> ("Justificativa da compra" e "Justificativa do problema"), em vez do único
> "Justificativa" atual.

## 2. Cotação editável até a aprovação (itens 1 e 2)

**Backend — `fillItemPrices`:** aceitar status `pending` **ou** `quoted`.

- `pending` → atualiza preços, recalcula total, transiciona para `quoted`, grava
  evento `pending → quoted`.
- `quoted` → atualiza preços e recalcula total, **mantém `quoted`**, **sem gravar
  evento** (edição silenciosa).

A validação de "todos os itens com preço > 0" permanece.

**Front — `PendingRequestForm.renderActions`:**

- `pending` → "Registrar Cotação" (MANAGER/DIRECTOR).
- `quoted`:
  - "Editar Cotação" (MANAGER **e** DIRECTOR).
  - "Marcar como Comprado" / "Negar Pedido" (**somente** DIRECTOR).
  - Para quem não é diretor: além do botão "Editar Cotação", o label informativo
    "Cotação registrada — aguardando decisão do diretor".

`QuoteRequestForm` já pré-preenche os preços existentes, servindo para registrar
e para editar — nenhuma mudança de lógica, apenas é reaberto em `quoted`.

**Estilo do modal de cotação** (`quote-request-form.fxml`): refinar alinhamento
das linhas (nome/qtd/preço), largura dos campos de preço, espaçamento e
cabeçalho.

## 3. Botões sem colapsar (item 3)

Em `pending-request-form.fxml`, `btnActionContainer` está com `prefWidth=246` e
`layoutX=440`, enquanto `PendingRequestForm.makeBtn` cria botões com
`prefWidth=170` — dois botões longos não cabem e o texto é cortado.

- Alargar/reposicionar o `btnActionContainer` (preferir âncoras a `layoutX` fixo)
  para acomodar dois botões com texto completo.
- Em `makeBtn`, substituir `prefWidth` fixo por largura que acompanha o conteúdo
  (`USE_COMPUTED_SIZE` + `minWidth` adequado + padding), garantindo o texto
  inteiro em qualquer rótulo.

## 4. FXMLs separados por etapa (Opção A — itens 4 e 6)

| Tela | Controller | FXML | Campos |
|------|-----------|------|--------|
| Aprovar | `AproveRequestForm` | `director-request-form.fxml` | detalhe + **DatePicker** de entrega + TextArea de justificativa |
| Recusar | `DeclineRequestForm` | `simple-request-form.fxml` (**novo**) | detalhe + TextArea + botões |
| Enviar | `ShipRequestForm` | `simple-request-form.fxml` | detalhe + TextArea + botões |
| Problema | `ProblemRequestForm` | `simple-request-form.fxml` | detalhe + TextArea + botões |
| Receber | `ReceiveRequestForm` | `receive-request-form.fxml` (**novo**) | detalhe + **TextField numérico** de NF + botões |

- `simple-request-form.fxml`: cópia enxuta do form atual **sem** o `DatePicker`
  e seu label. `DeclineRequestForm` deixa de precisar esconder o DatePicker
  manualmente.
- `receive-request-form.fxml`: NF vira um `TextField` com `TextFormatter` que
  aceita **apenas dígitos**. `ReceiveRequestForm` lê esse campo e valida que é
  numérico e não vazio.
- `PendingRequestForm.abrirSubModal` escolhe o FXML conforme o tipo do controller
  (hoje só distingue Quote vs. director).

## 5. Data de entrega prevista exibida (item 4b)

`Order` já parseia `expectedDeliveryDate` (ISO string).

- **Detalhe (`pending-request-form.fxml` / `PendingRequestForm`):** label
  "Entrega prevista: dd/MM/aaaa", visível quando `expectedDeliveryDate != null`
  (a partir de `confirmed`).
- **Card da lista (`OrdersCard`):** mostrar a data prevista quando existir
  (ex.: linha "Entrega prevista: dd/MM/aaaa").
- Formatação dd/MM/aaaa a partir do ISO já disponível.

## 6. Histórico de status dos pedidos (item 7)

O backend **já grava** cada transição em `PurchaseOrderEvent` e expõe
`GET /{id}/events`. Falta a visão agregada e o front.

**Backend:** novo endpoint `GET /api/purchase-orders/events` — todos os eventos,
ordenados do mais recente para o mais antigo. Repository ganha
`findAllByOrderByOccurredAtDesc()`. O DTO de resposta inclui, além dos campos do
`PurchaseOrderEventDTO` atual, identificação do pedido (id) e um rótulo
(produto principal ou "Pedido #id") para exibição.

**Front — `HistoryPage`:**

- Adicionar um **seletor de abas** no topo de `history-page.fxml`: "Estoque" e
  "Pedidos".
- "Estoque": comportamento atual (`/api/stock/movements/activity` +
  `HistoryLogCard`).
- "Pedidos": consome `/api/purchase-orders/events`, mapeia cada `toStatus` para o
  `MovementType` correspondente e renderiza com o **`HistoryLogCard` existente**
  (estilos `log__status--3..12` já prontos no componente):

  | `toStatus` | `MovementType` |
  |-----------|----------------|
  | pending | REVISAO |
  | quoted | REVISAO (cotação registrada) |
  | confirmed | COMPRA |
  | shipped | EM_TRANSITO |
  | received | RECEBIDO |
  | cancelled | NEGACAO |
  | problem | PROBLEMA |
  | returned | DEVOLVIDO |

  Cada card mostra a data (`occurredAt`), o responsável (`performedBy`) e, quando
  houver, a justificativa (`reason`). `HistoryLog`/`HistoryLogCard` podem precisar
  de pequeno ajuste para acomodar o texto do evento de pedido (rótulo do pedido +
  reason) reusando os campos existentes.

## Arquivos afetados

**API:**
- `Entities/PurchaseOrder.java` — renomear `decisionReason`→`purchaseJustification`
  (mesma coluna), novo `problemJustification`.
- `Services/PurchaseOrderService.java` — `fillItemPrices` (aceitar `quoted`,
  edição silenciosa), `confirmOrder`/`cancelOrder` (→ `purchaseJustification`),
  `markAsProblem` (→ `problemJustification`), `listAllEvents()`.
- `Controllers/PurchaseOrderController.java` — `GET /events` agregado.
- `Repositories/PurchaseOrderEventRepository.java` — `findAllByOrderByOccurredAtDesc`.
- `DTOs/Response/PurchaseOrderEventDTO.java` (ou novo DTO agregado) — incluir
  id/rótulo do pedido.

**Front:**
- `controller/PendingRequestForm.java` — `renderActions` (botão "Editar Cotação"
  em `quoted` p/ MANAGER+DIRECTOR); `makeBtn` (largura por conteúdo); exibir data
  prevista; exibir as duas justificativas (compra e problema) quando presentes;
  `abrirSubModal` (escolha de FXML por etapa).
- `controller/DeclineRequestForm.java` — usar `simple-request-form.fxml`, remover
  o hide manual do DatePicker.
- `controller/ShipRequestForm.java` / `ProblemRequestForm.java` — usar
  `simple-request-form.fxml`.
- `controller/ReceiveRequestForm.java` — usar `receive-request-form.fxml`; NF como
  TextField numérico (validação só dígitos).
- `controller/HistoryPageController.java` — abas Estoque/Pedidos; carregar e
  renderizar eventos de pedido.
- `components/OrdersCard.java` — exibir data prevista.
- `models/Order.java` — ler `purchaseJustification`/`problemJustification` (no
  lugar de `decisionReason`); getter formatado de data prevista (se necessário).
- `models/HistoryLog.java` / `components/HistoryLogCard.java` — pequeno ajuste para
  eventos de pedido (rótulo + reason).
- `view/forms/quote-request-form.fxml` — estilo do modal de cotação.
- `view/forms/simple-request-form.fxml` (**novo**).
- `view/forms/receive-request-form.fxml` (**novo**).
- `view/forms/pending-request-form.fxml` — largura/posição do `btnActionContainer`;
  label de entrega prevista.
- `view/components/OrdersCard.fxml` — label de entrega prevista.
- `view/history-page.fxml` — seletor de abas.

## Fora de escopo

- Remoção do preço da entidade/CRUD de Produto (campo `unitValue`) — plano de
  Produtos.
- Timeline de eventos dentro do modal do pedido (a pedido do usuário, histórico
  fica só na página Histórico).

## Verificação (end-to-end)

1. **Justificativas:** aprovar/recusar grava em `purchaseJustification`; reportar
   problema grava em `problemJustification`; nenhuma sobrescreve a outra; a
   observação do solicitante (`notes`) permanece intacta o fluxo todo.
2. **Cotação editável:** como gerente e como diretor, em `quoted`, "Editar
   Cotação" reabre o modal com os preços atuais, salva e mantém `quoted` sem novo
   evento no histórico. Aprovar/Negar continua só para diretor.
3. **Botões:** no detalhe do pedido, "Marcar como Comprado" e "Negar Pedido"
   aparecem com texto completo, sem colapsar.
4. **DatePicker:** aparece **apenas** na tela de Aprovar; ausente em
   Enviar/Receber/Problema/Recusar.
5. **NF:** na tela de Receber, o campo aceita **apenas números**; texto é
   rejeitado; vazio bloqueia o envio.
6. **Data prevista:** após "Marcar como Comprado", a data prevista aparece no
   detalhe do pedido e no card da lista.
7. **Modal de cotação:** alinhado/estilizado corretamente.
8. **Histórico:** página Histórico com abas "Estoque" e "Pedidos"; a aba
   "Pedidos" lista as transições de status (data, responsável, justificativa) de
   todos os pedidos, mais recentes primeiro.
