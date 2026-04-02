# User Story 7

**Como** diretor, **quero** visualizar os pedidos de compra pendentes atribuídos a mim e aprovar ou reprovar cada um com parecer registrado, **para** avaliar e autorizar as solicitações antes de sua efetivação.

---

## Critérios de Aceite (CA)

### CA1: Visualização de pedidos pendentes de aprovação
**Dado** que sou um diretor autenticado no sistema  
**Quando** eu acesso a tela de aprovações  
**Então** o sistema exibe uma lista de todos os pedidos de compra com status "Pendente de Aprovação"  
**E** cada pedido mostra informações resumidas (número, solicitante, setor, valor total, data de criação)  
**E** posso filtrar pedidos por setor, período ou valor  
**E** posso ordenar por data, valor ou número do pedido

### CA2: Visualização de detalhes completos do pedido antes de aprovar/reprovar
**Dado** que sou um diretor visualizando pedidos pendentes  
**Quando** eu clico em um pedido para visualizar detalhes  
**Então** o sistema exibe todas as informações do pedido:  
**E** número do pedido  
**E** solicitante e data de criação  
**E** setor e centro de custo  
**E** justificativa detalhada  
**E** lista completa de itens com quantidades e valores  
**E** valor total do pedido  
**E** histórico de alterações (se houver)

### CA3: Aprovação de pedido de compra com parecer
**Dado** que sou um diretor visualizando os detalhes de um pedido pendente  
**Quando** eu seleciono a opção "Aprovar"  
**E** registro um parecer no campo de observações  
**E** clico em "Confirmar Aprovação"  
**Então** o status do pedido é alterado para "Aprovado"  
**E** o parecer é registrado junto com data/hora e meu usuário  
**E** uma mensagem de sucesso é exibida  
**E** o pedido sai da minha lista de pendentes  
**E** o solicitante pode visualizar a aprovação

### CA4: Reprovação de pedido de compra com parecer obrigatório
**Dado** que sou um diretor visualizando os detalhes de um pedido pendente  
**Quando** eu seleciono a opção "Reprovar"  
**E** registro um parecer obrigatório explicando o motivo da reprovação  
**E** clico em "Confirmar Reprovação"  
**Então** o status do pedido é alterado para "Reprovado"  
**E** o parecer é registrado junto com data/hora e meu usuário  
**E** uma mensagem de confirmação é exibida  
**E** o pedido sai da minha lista de pendentes  
**E** o solicitante pode visualizar a reprovação e o motivo

### CA5: Validação de parecer obrigatório na reprovação
**Dado** que sou um diretor reprovando um pedido  
**Quando** eu tento confirmar a reprovação sem preencher o campo de parecer  
**Então** o sistema exibe uma mensagem de erro "O parecer é obrigatório para reprovar um pedido"  
**E** a reprovação não é efetivada até que o parecer seja preenchido

### CA6: Visualização de histórico de aprovações/reprovações
**Dado** que sou um diretor autenticado  
**Quando** eu acesso a seção de histórico de aprovações  
**Então** o sistema exibe todos os pedidos que já aprovei ou reprovei  
**E** posso filtrar por status (aprovado/reprovado) e período  
**E** posso visualizar os detalhes e pareceres registrados

### CA7: Impossibilidade de editar pedido após aprovação/reprovação
**Dado** que um pedido foi aprovado ou reprovado por um diretor  
**Quando** qualquer usuário tenta editar este pedido  
**Então** o sistema não permite a edição  
**E** exibe uma mensagem informando que pedidos aprovados/reprovados não podem ser editados

---

## Definition of Ready

| Critério | Descrição | Status para US7 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Diretor pode visualizar, aprovar ou reprovar pedidos de compra |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 7 critérios definidos cobrindo visualização, detalhamento, aprovação, reprovação, validações e histórico |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende de US1 (autenticação) e US6 (criação de pedidos)** - necessário ter pedidos criados para aprovar |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das telas:<br>- Dashboard de pedidos pendentes com filtros<br>- Tela de detalhes do pedido com opções de aprovar/reprovar<br>- Modal de aprovação com campo de parecer<br>- Modal de reprovação com campo de parecer obrigatório<br>- Histórico de aprovações realizadas |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** API de consulta de pedidos pendentes, endpoints de aprovação/reprovação com validação de parecer, atualização de status, registro de aprovador e timestamp<br>**Frontend:** Dashboard de aprovações, tela de detalhes, modais de confirmação com validação, integração com API |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 (autenticação de diretor), US6 (pedidos criados) e US5 (histórico de aprovações). Será base para US9 (rastreamento de status) e US12 (consulta de status pelo operacional) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 8 pontos no Planning Poker |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Atualização da modelagem do banco (adicionar campos: status, approver_id, approval_date, approval_comments em purchase_orders)<br>- Máquina de estados dos status do pedido<br>- Regras de negócio para aprovação (ex: valores acima de X precisam de aprovação de diretor específico) |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre fluxo de aprovação e regras de alçada |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US7 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 7 CAs:<br>- CA1: Listagem de pedidos pendentes com filtros<br>- CA2: Detalhamento completo do pedido<br>- CA3: Aprovação com parecer<br>- CA4: Reprovação com parecer obrigatório<br>- CA5: Validação de parecer obrigatório<br>- CA6: Histórico de aprovações<br>- CA7: Imutabilidade após aprovação/reprovação |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "diretor"<br>- Status do pedido alterado corretamente<br>- Histórico de auditoria (US5) registrando aprovações<br>- Operacional (US6) consegue visualizar status atualizado |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Máquina de estados validada<br>- Validações de parecer implementadas<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Criar pedido (operacional) → aprovar (diretor) → verificar status<br>- Criar pedido → reprovar com parecer → verificar motivo<br>- Verificar que pedido aprovado não pode ser editado<br>- Filtros e ordenação funcionando<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/purchase-orders/pending-approval` - Listar pedidos pendentes (apenas diretor)<br>- `GET /api/purchase-orders/:id` - Detalhes do pedido<br>- `POST /api/purchase-orders/:id/approve` - Aprovar pedido (body: { comments })<br>- `POST /api/purchase-orders/:id/reject` - Reprovar pedido (body: { comments } - obrigatório)<br>- `GET /api/purchase-orders/my-approvals` - Histórico de aprovações do diretor<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response<br>- Códigos de status HTTP<br>- Regras de validação (parecer obrigatório na reprovação)<br>- Permissões necessárias (perfil diretor)<br>- Transições de status válidas |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou o fluxo de aprovação e interface<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Validação de parecer obrigatório na reprovação<br>- Transições de status válidas<br>- Registro de aprovador e timestamp<br><br>**Backend - Testes de Integração:**<br>- Aprovação de pedido → status alterado para "Aprovado"<br>- Reprovação de pedido → status alterado para "Reprovado"<br>- Reprovação sem parecer → erro de validação<br>- Tentativa de editar pedido aprovado → bloqueado<br>- Autenticação e autorização (apenas diretor)<br>- Registro no histórico de auditoria<br><br>**Frontend - Testes E2E:**<br>- Fluxo completo de aprovação<br>- Fluxo de reprovação com validação de parecer<br>- Filtros e ordenação de pedidos |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar endpoints<br>- Apenas perfil "diretor" pode aprovar/reprovar pedidos<br>- Validação de token JWT em todas as requisições<br>- Aprovador registrado é sempre o usuário logado<br>- Pedidos aprovados/reprovados são imutáveis<br>- Proteção contra SQL Injection e XSS<br>- Registro de IP e timestamp no histórico de auditoria<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden)<br>- Teste de tentativa de aprovar pedido já aprovado/reprovado (deve retornar erro) |
