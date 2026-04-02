# User Story 6

**Como** operacional, **quero** abrir um pedido de compra com número automático, informando solicitante, setor, itens, justificativa, valor estimado e centro de custo, **para** formalizar e registrar as necessidades de reposição de forma organizada.

---

## Critérios de Aceite (CA)

### CA1: Abertura de pedido de compra com sucesso
**Dado** que sou um operacional autenticado no sistema  
**Quando** eu acesso a tela de criação de pedido de compra  
**E** preencho todos os campos obrigatórios (setor, justificativa, centro de custo)  
**E** adiciono pelo menos um item com quantidade e valor estimado  
**E** clico em "Criar Pedido"  
**Então** o sistema gera automaticamente um número único para o pedido  
**E** o pedido é criado com status "Pendente de Aprovação"  
**E** o solicitante é preenchido automaticamente com o usuário logado  
**E** uma mensagem de sucesso é exibida com o número do pedido  
**E** o pedido aparece na minha listagem de pedidos

### CA2: Adição de múltiplos itens ao pedido de compra
**Dado** que sou um operacional criando um pedido de compra  
**Quando** eu adiciono um item informando nome, quantidade e valor unitário estimado  
**E** clico em "Adicionar Item"  
**Então** o item é adicionado à lista de itens do pedido  
**E** o valor total do pedido é calculado automaticamente (soma de quantidade × valor unitário de todos os itens)  
**E** posso adicionar mais itens ao pedido  
**E** posso remover itens da lista antes de submeter o pedido

### CA3: Validação de campos obrigatórios
**Dado** que sou um operacional criando um pedido de compra  
**Quando** eu tento criar um pedido sem preencher campos obrigatórios (setor, justificativa, centro de custo)  
**Ou** sem adicionar pelo menos um item  
**E** clico em "Criar Pedido"  
**Então** o sistema exibe mensagens de erro indicando os campos não preenchidos  
**E** o pedido não é criado até que todos os campos obrigatórios sejam preenchidos

### CA4: Validação de valores monetários não negativos
**Dado** que sou um operacional adicionando um item ao pedido  
**Quando** eu tento informar valor unitário estimado negativo ou zero  
**Então** o sistema exibe uma mensagem de erro "O valor deve ser maior que zero"  
**E** não permite adicionar o item

### CA5: Validação de quantidade não negativa
**Dado** que sou um operacional adicionando um item ao pedido  
**Quando** eu tento informar quantidade negativa ou zero  
**Então** o sistema exibe uma mensagem de erro "A quantidade deve ser maior que zero"  
**E** não permite adicionar o item

### CA6: Geração automática de número do pedido
**Dado** que o sistema está em funcionamento  
**Quando** um pedido de compra é criado  
**Então** o sistema gera automaticamente um número único sequencial (ex: PC-2026-0001)  
**E** este número é único no sistema  
**E** não pode ser alterado manualmente pelo usuário

---

## Definition of Ready

| Critério | Descrição | Status para US6 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Operacional pode criar pedidos de compra formalizados |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 6 critérios definidos cobrindo criação, múltiplos itens, validações e geração automática de número |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende de US1 (autenticação) e US4 (itens de estoque)** - necessário ter usuários autenticados e referência a itens |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das telas:<br>- Listagem de pedidos de compra do operacional (meus pedidos)<br>- Formulário de criação de pedido com campos principais e lista de itens<br>- Componente de adição de itens ao pedido<br>- Modal de confirmação de criação com número do pedido |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** API de criação de pedidos com geração automática de número sequencial, validações de campos e cálculo de valor total. Relacionamento com itens de estoque e usuários<br>**Frontend:** Formulário multi-step com validações, adição dinâmica de itens, cálculo automático de totais, integração com API |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 (autenticação de operacional) e US4 (referência a itens de estoque). Será base para US7 (aprovação), US9 (rastreamento) e US12 (consulta de status) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 13 pontos no Planning Poker (complexidade alta - formulário complexo com múltiplos itens) |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabelas: purchase_orders e purchase_order_items)<br>- Lista de setores disponíveis<br>- Lista de centros de custo<br>- Regras de negócio para geração de número do pedido<br>- Estrutura de status do pedido (pendente, aprovado, reprovado, em cotação, concluído) |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre campos obrigatórios, setores e centros de custo |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US6 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 6 CAs:<br>- CA1: Criação com número automático<br>- CA2: Adição de múltiplos itens com cálculo de total<br>- CA3: Validação de campos obrigatórios<br>- CA4: Validação de valores monetários<br>- CA5: Validação de quantidades<br>- CA6: Unicidade de número do pedido |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "operacional"<br>- Referência a itens de estoque (US4) funcionando<br>- Histórico de alterações (US5) registrando criação de pedidos |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Lógica de geração de número sequencial validada<br>- Cálculos de totais corretos<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Login como operacional → criar pedido<br>- Adicionar múltiplos itens → verificar cálculo de total<br>- Criar pedido → verificar número automático único<br>- Visualizar pedido criado na listagem<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `POST /api/purchase-orders` - Criar pedido de compra<br>- `GET /api/purchase-orders/my-orders` - Listar pedidos do usuário logado<br>- `GET /api/purchase-orders/:id` - Detalhes de um pedido<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response completo<br>- Estrutura de itens do pedido<br>- Códigos de status HTTP<br>- Regras de validação<br>- Permissões necessárias (perfil operacional)<br>- Lógica de geração de número automático<br>- Status possíveis do pedido |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou o fluxo e os campos do formulário<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Geração de número sequencial único<br>- Cálculo de valor total do pedido<br>- Validação de campos obrigatórios<br>- Validação de valores e quantidades positivos<br><br>**Backend - Testes de Integração:**<br>- Criação de pedido com múltiplos itens<br>- Transação atômica (rollback se falhar)<br>- Autenticação e autorização (apenas operacional)<br>- Persistência no banco de dados<br>- Registro no histórico de auditoria<br><br>**Frontend - Testes E2E:**<br>- Fluxo completo de criação de pedido<br>- Adição e remoção de itens<br>- Cálculo dinâmico de totais |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem criar pedidos<br>- Apenas perfil "operacional" (ou superior) pode criar pedidos<br>- Solicitante é sempre o usuário logado (não pode ser alterado)<br>- Validação de token JWT em todas as requisições<br>- Proteção contra SQL Injection e XSS<br>- Validação de tipos de dados (números positivos)<br>- Números de pedido são gerados pelo backend (não aceitos do frontend)<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden) |
