# User Story 9

**Como** gerente, **quero** rastrear o status de cada pedido de compra e editar informações incorretas antes da aprovação, **para** garantir que as compras sejam concluídas corretamente e sem retrabalho.

---

## Critérios de Aceite (CA)

### CA1: Visualização de todos os pedidos de compra com status
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de gestão de pedidos de compra  
**Então** o sistema exibe uma lista de todos os pedidos com informações resumidas (número, solicitante, data, status, valor total)  
**E** posso filtrar por status (pendente, aprovado, reprovado, em cotação, concluído)  
**E** posso filtrar por período, solicitante ou setor  
**E** posso ordenar por data, número ou valor  
**E** cada pedido exibe seu status atual de forma visual (cores, ícones ou badges)

### CA2: Rastreamento detalhado do histórico de um pedido
**Dado** que sou um gerente visualizando a lista de pedidos  
**Quando** eu clico em um pedido para ver detalhes  
**Então** o sistema exibe todas as informações do pedido  
**E** exibe uma timeline/histórico cronológico com todas as mudanças de status:  
**E** data/hora de cada transição  
**E** usuário responsável pela mudança  
**E** comentários ou pareceres associados  
**E** status anterior e novo status  
**E** o histórico está ordenado cronologicamente (mais recente primeiro)

### CA3: Edição de pedido pendente de aprovação
**Dado** que sou um gerente autenticado no sistema  
**E** existe um pedido com status "Pendente de Aprovação"  
**Quando** eu acesso a tela de edição deste pedido  
**E** altero informações (setor, justificativa, itens, quantidades, valores, centro de custo)  
**E** clico em "Salvar Alterações"  
**Então** as alterações são persistidas no banco de dados  
**E** uma mensagem de sucesso é exibida  
**E** as alterações são registradas no histórico de auditoria  
**E** o pedido permanece com status "Pendente de Aprovação"

### CA4: Impossibilidade de editar pedido após aprovação ou reprovação
**Dado** que sou um gerente autenticado no sistema  
**E** existe um pedido com status "Aprovado" ou "Reprovado"  
**Quando** eu tento acessar a edição deste pedido  
**Então** o sistema não permite a edição  
**E** exibe uma mensagem "Pedidos aprovados ou reprovados não podem ser editados"  
**E** apenas a visualização é permitida

### CA5: Dashboard de status dos pedidos
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso o dashboard de pedidos  
**Então** o sistema exibe indicadores quantitativos por status:  
**E** quantidade de pedidos pendentes  
**E** quantidade de pedidos aprovados  
**E** quantidade de pedidos reprovados  
**E** quantidade de pedidos em cotação  
**E** quantidade de pedidos concluídos  
**E** valor total por categoria de status  
**E** posso clicar em cada indicador para filtrar a listagem

### CA6: Notificação visual de pedidos que precisam de atenção
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de gestão de pedidos  
**Então** o sistema destaca visualmente pedidos que estão há mais de X dias no status "Pendente"  
**E** exibe indicadores de urgência (ex: tempo de espera)  
**E** permite filtrar por pedidos que precisam de atenção

### CA7: Validação de campos ao editar pedido
**Dado** que sou um gerente editando um pedido pendente  
**Quando** eu tento salvar com campos obrigatórios vazios ou valores inválidos  
**Então** o sistema exibe mensagens de erro indicando os problemas  
**E** as alterações não são salvas até que todos os campos sejam corrigidos

---

## Definition of Ready

| Critério | Descrição | Status para US9 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode rastrear status e editar pedidos pendentes |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 7 critérios definidos cobrindo visualização, rastreamento, edição, dashboard e validações |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende de US1 (autenticação), US6 (pedidos) e US7 (aprovação)** - necessário ter pedidos com diferentes status |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das telas:<br>- Dashboard com indicadores de status<br>- Listagem de pedidos com filtros e badges de status<br>- Tela de detalhes com timeline de histórico<br>- Formulário de edição de pedido pendente<br>- Indicadores visuais de urgência |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** API de listagem com filtros, endpoint de edição com validação de status, histórico de transições de status, cálculo de indicadores<br>**Frontend:** Dashboard com KPIs, listagem com filtros, timeline visual de histórico, formulário de edição condicional, badges e indicadores |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 (autenticação), US6 (pedidos), US7 (aprovações) e US5 (histórico de auditoria). Será base para US10 (cotações), US12 (consulta por operacional) e US13 (recebimento) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 13 pontos no Planning Poker (complexidade alta - dashboard + edição condicional) |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Máquina de estados completa dos pedidos<br>- Regras de negócio para edição (apenas status pendente)<br>- Definição de tempo de urgência (X dias no status pendente)<br>- Estrutura de histórico de transições de status |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre regras de edição e indicadores de urgência |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US9 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 7 CAs:<br>- CA1: Listagem com filtros de status<br>- CA2: Timeline de histórico detalhado<br>- CA3: Edição de pedido pendente<br>- CA4: Bloqueio de edição de pedidos aprovados/reprovados<br>- CA5: Dashboard com indicadores<br>- CA6: Notificações de urgência<br>- CA7: Validação de campos na edição |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "gerente"<br>- Máquina de estados respeitada<br>- Histórico de auditoria (US5) registrando edições<br>- Fluxo de aprovação (US7) não afetado |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Validações de status implementadas<br>- Lógica de edição condicional validada<br>- Cálculos de indicadores corretos<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Criar pedido → visualizar na listagem com status<br>- Editar pedido pendente → salvar → verificar alterações<br>- Tentar editar pedido aprovado → erro<br>- Visualizar timeline de histórico<br>- Dashboard exibindo indicadores corretos<br>- Filtros funcionando corretamente<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/purchase-orders` - Listar todos os pedidos (filtros: status, date_from, date_to, requester, department)<br>- `GET /api/purchase-orders/:id` - Detalhes do pedido com histórico<br>- `GET /api/purchase-orders/:id/history` - Timeline de transições de status<br>- `PUT /api/purchase-orders/:id` - Editar pedido (somente se status = pending)<br>- `GET /api/purchase-orders/dashboard` - Indicadores agregados por status<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response<br>- Códigos de status HTTP<br>- Regras de validação<br>- Permissões necessárias (perfil gerente)<br>- Estados válidos para edição<br>- Estrutura de histórico/timeline |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou dashboard, timeline e regras de edição<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Validação de status para permitir edição<br>- Cálculo de indicadores de dashboard<br>- Cálculo de tempo de espera/urgência<br>- Validação de campos na edição<br><br>**Backend - Testes de Integração:**<br>- Edição de pedido pendente → sucesso<br>- Tentativa de edição de pedido aprovado → erro 400<br>- Tentativa de edição de pedido reprovado → erro 400<br>- Consulta de histórico de transições<br>- Filtros de listagem<br>- Cálculo correto de indicadores<br>- Autenticação e autorização (apenas gerente)<br>- Registro de edições no histórico de auditoria<br><br>**Frontend - Testes E2E:**<br>- Fluxo completo de visualização e filtragem<br>- Edição de pedido pendente<br>- Tentativa de editar pedido aprovado (botão desabilitado)<br>- Visualização de timeline de histórico<br>- Dashboard com indicadores |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar endpoints<br>- Apenas perfil "gerente" pode visualizar todos os pedidos e editá-los<br>- Validação de token JWT em todas as requisições<br>- Validação rigorosa de status antes de permitir edição<br>- Pedidos aprovados/reprovados são imutáveis (proteção no backend)<br>- Proteção contra SQL Injection e XSS<br>- Registro de todas as edições no histórico de auditoria (quem, quando, o que)<br>- Rate limiting em endpoints de listagem e dashboard<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden)<br>- Teste de tentativa de editar pedido com status inválido (deve retornar 400 Bad Request) |
