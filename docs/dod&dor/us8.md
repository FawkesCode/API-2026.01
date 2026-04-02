# User Story 8

**Como** gerente, **quero** registrar entradas e saídas de estoque vinculadas a pedidos aprovados ou uso interno, **para** monitorar o fluxo de produtos e evitar rupturas ou excessos.

---

## Critérios de Aceite (CA)

### CA1: Registro de entrada de estoque vinculada a pedido aprovado
**Dado** que sou um gerente autenticado no sistema  
**E** existe um pedido de compra aprovado  
**Quando** eu acesso a tela de registro de movimentação de estoque  
**E** seleciono "Entrada"  
**E** informo o pedido de compra relacionado  
**E** informo os itens, quantidades recebidas e data de entrada  
**E** clico em "Registrar Entrada"  
**Então** o sistema atualiza a quantidade em estoque dos itens  
**E** a movimentação é registrada no histórico de estoque  
**E** uma mensagem de sucesso é exibida  
**E** o sistema verifica se algum item atingiu o nível mínimo

### CA2: Registro de saída de estoque por uso interno
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de registro de movimentação de estoque  
**E** seleciono "Saída"  
**E** informo o motivo da saída (uso interno, perda, devolução, etc.)  
**E** informo os itens, quantidades e data de saída  
**E** clico em "Registrar Saída"  
**Então** o sistema deduz a quantidade em estoque dos itens  
**E** a movimentação é registrada no histórico de estoque  
**E** uma mensagem de sucesso é exibida  
**E** o sistema alerta se algum item ficou abaixo do nível mínimo

### CA3: Validação de quantidade disponível para saída
**Dado** que sou um gerente registrando uma saída de estoque  
**Quando** eu tento registrar uma saída com quantidade maior que a disponível no estoque  
**E** clico em "Registrar Saída"  
**Então** o sistema exibe uma mensagem de erro "Quantidade insuficiente em estoque"  
**E** indica a quantidade disponível atual  
**E** a saída não é registrada

### CA4: Visualização de histórico de movimentações por item
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de detalhes de um item de estoque  
**Então** o sistema exibe o histórico completo de movimentações (entradas e saídas)  
**E** cada movimentação mostra: data, tipo (entrada/saída), quantidade, usuário responsável, motivo/pedido vinculado  
**E** posso filtrar por período e tipo de movimentação  
**E** o histórico está ordenado por data (mais recente primeiro)

### CA5: Atualização automática de quantidade atual do item
**Dado** que uma movimentação de estoque é registrada (entrada ou saída)  
**Quando** o sistema processa a movimentação  
**Então** a quantidade atual do item é atualizada automaticamente  
**E** para entrada: quantidade_atual = quantidade_anterior + quantidade_entrada  
**E** para saída: quantidade_atual = quantidade_anterior - quantidade_saída  
**E** a atualização ocorre de forma atômica (transação)

### CA6: Validação de campos obrigatórios
**Dado** que sou um gerente registrando uma movimentação  
**Quando** eu tento registrar sem preencher campos obrigatórios (tipo, item, quantidade, data, motivo)  
**E** clico em "Registrar"  
**Então** o sistema exibe mensagens de erro indicando os campos não preenchidos  
**E** a movimentação não é registrada

### CA7: Alerta de estoque baixo após movimentação
**Dado** que uma saída de estoque é registrada  
**Quando** a quantidade atual de um item fica abaixo do nível mínimo após a movimentação  
**Então** o sistema exibe um alerta visual destacando o item  
**E** registra a necessidade de reposição  
**E** o alerta fica disponível no dashboard do gerente

---

## Definition of Ready

| Critério | Descrição | Status para US8 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode registrar movimentações de estoque (entradas e saídas) |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 7 critérios definidos cobrindo entradas, saídas, validações, histórico e alertas |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende de US1 (autenticação), US4 (itens de estoque), US6 (pedidos) e US7 (aprovação)** - necessário ter itens e pedidos aprovados |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das telas:<br>- Formulário de registro de movimentação (entrada/saída)<br>- Tela de histórico de movimentações por item<br>- Modal de confirmação de movimentação<br>- Alertas de estoque baixo |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** API de registro de movimentações com atualização atômica de quantidade, validação de estoque disponível, vínculo com pedidos, geração de alertas de estoque baixo<br>**Frontend:** Formulários de entrada/saída, histórico de movimentações, alertas visuais, integração com API |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 (autenticação), US4 (itens de estoque), US6/US7 (pedidos aprovados) e US5 (histórico de auditoria). Será base para US11 (dashboard com alertas) e US13 (recebimento de mercadorias) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 8 pontos no Planning Poker |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabela inventory_movements com: id, item_id, type, quantity, purchase_order_id, reason, user_id, timestamp)<br>- Lista de motivos de saída (uso interno, perda, devolução, ajuste, etc.)<br>- Regras de negócio para cálculo de estoque e geração de alertas |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre motivos de saída aceitos e regras de alertas |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US8 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 7 CAs:<br>- CA1: Entrada vinculada a pedido<br>- CA2: Saída por uso interno<br>- CA3: Validação de quantidade disponível<br>- CA4: Histórico de movimentações<br>- CA5: Atualização automática de quantidade<br>- CA6: Validação de campos obrigatórios<br>- CA7: Alertas de estoque baixo |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "gerente"<br>- Vínculo com pedidos aprovados funcionando<br>- Atualização de quantidade em itens de estoque<br>- Histórico de auditoria registrando movimentações |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Transações atômicas implementadas<br>- Validações de estoque corretas<br>- Lógica de alertas validada<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Criar pedido → aprovar → registrar entrada → verificar quantidade<br>- Registrar saída → verificar dedução de quantidade<br>- Tentar saída com quantidade insuficiente → erro<br>- Visualizar histórico de movimentações<br>- Alertas de estoque baixo aparecendo<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `POST /api/inventory/movements` - Registrar movimentação (entrada/saída)<br>- `GET /api/inventory/movements` - Listar movimentações (filtros: item, type, date_from, date_to)<br>- `GET /api/inventory/movements/by-item/:itemId` - Histórico por item<br>- `GET /api/inventory/items/:id` - Detalhes do item (inclui quantidade atual e status)<br><br>**Payload de movimentação:**<br>```json<br>{<br>  "type": "IN/OUT",<br>  "item_id": 123,<br>  "quantity": 50,<br>  "purchase_order_id": 456, // opcional, obrigatório para entrada<br>  "reason": "internal_use", // obrigatório para saída<br>  "date": "2026-03-27"<br>}<br>```<br><br>**Incluir:**<br>- Códigos de status HTTP<br>- Regras de validação<br>- Permissões necessárias (perfil gerente)<br>- Lógica de cálculo de quantidade<br>- Regras de geração de alertas |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou o fluxo de movimentações e alertas<br>- Teste de concorrência realizado (múltiplas movimentações simultâneas)<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Cálculo de quantidade após entrada<br>- Cálculo de quantidade após saída<br>- Validação de quantidade disponível<br>- Geração de alertas de estoque baixo<br>- Validação de campos obrigatórios<br><br>**Backend - Testes de Integração:**<br>- Transação atômica (rollback se falhar)<br>- Entrada vinculada a pedido aprovado<br>- Saída com motivo interno<br>- Tentativa de saída com quantidade insuficiente → erro<br>- Concorrência (múltiplas movimentações no mesmo item)<br>- Autenticação e autorização (apenas gerente)<br>- Registro no histórico de auditoria<br><br>**Frontend - Testes E2E:**<br>- Fluxo completo de registro de entrada<br>- Fluxo de registro de saída com validação<br>- Visualização de histórico |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem registrar movimentações<br>- Apenas perfil "gerente" pode registrar movimentações<br>- Validação de token JWT em todas as requisições<br>- Usuário responsável registrado automaticamente<br>- Transações atômicas previnem inconsistências<br>- Proteção contra SQL Injection e XSS<br>- Validação de tipos de dados (quantidade positiva)<br>- Validação de existência de item e pedido vinculado<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden)<br>- Locks de transação para prevenir race conditions |
