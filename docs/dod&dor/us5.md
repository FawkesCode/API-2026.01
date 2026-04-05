# User Story 5

**Como** gerente, **quero** visualizar o histórico completo de alterações realizadas no sistema, **para** identificar inconsistências e garantir a integridade dos dados ao longo do tempo.

---

## Critérios de Aceite (CA)

### CA1: Visualização de histórico de alterações com filtros
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de histórico de alterações  
**Então** o sistema exibe uma lista cronológica de todas as alterações realizadas  
**E** posso filtrar por entidade (funcionários, fornecedores, estoque, pedidos)  
**E** posso filtrar por período (data inicial e final)  
**E** posso filtrar por usuário que realizou a alteração  
**E** posso filtrar por tipo de operação (criação, edição, exclusão/inativação)

### CA2: Detalhamento de cada alteração no histórico
**Dado** que sou um gerente autenticado visualizando o histórico  
**Quando** eu clico em uma entrada do histórico  
**Então** o sistema exibe os detalhes completos da alteração:  
**E** data e hora da alteração  
**E** usuário que realizou a alteração  
**E** entidade afetada (tipo e ID)  
**E** tipo de operação (INSERT, UPDATE, DELETE)  
**E** valores anteriores (before) e novos valores (after) em formato comparativo  
**E** endereço IP de origem da alteração

### CA3: Auditoria automática de todas as operações críticas
**Dado** que o sistema está em funcionamento  
**Quando** qualquer usuário realiza uma operação de criação, edição ou exclusão/inativação  
**Em** entidades críticas (funcionários, fornecedores, estoque, pedidos de compra, aprovações)  
**Então** o sistema registra automaticamente no histórico:  
**E** timestamp da operação  
**E** usuário que realizou a operação  
**E** tipo de operação  
**E** dados antes e depois da alteração (diff)  
**E** endereço IP de origem

### CA4: Paginação e performance do histórico
**Dado** que sou um gerente autenticado visualizando o histórico  
**E** existem muitas alterações registradas  
**Quando** eu acesso a listagem do histórico  
**Então** o sistema exibe os resultados paginados (ex: 50 registros por página)  
**E** a navegação entre páginas é fluida  
**E** o carregamento não excede 3 segundos mesmo com grandes volumes de dados

### CA5: Impossibilidade de edição ou exclusão do histórico
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso o histórico de alterações  
**Então** não há opção para editar ou excluir registros do histórico  
**E** o histórico é somente leitura (read-only)  
**E** garante a integridade e confiabilidade dos registros de auditoria

### CA6: Exportação do histórico para auditoria externa
**Dado** que sou um gerente autenticado visualizando o histórico  
**Quando** eu seleciono a opção de exportar o histórico  
**E** aplico filtros desejados (período, entidade, usuário)  
**Então** o sistema gera um arquivo CSV ou Excel com os dados filtrados  
**E** o arquivo contém todas as colunas relevantes para auditoria  
**E** o download é iniciado automaticamente

---

## Definition of Ready

| Critério | Descrição | Status para US5 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode visualizar histórico completo de alterações para auditoria |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 6 critérios definidos cobrindo visualização, filtros, auditoria automática, performance, segurança e exportação |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende de US1, US2, US3 e US4** - necessário ter as entidades principais implementadas para auditar suas operações |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das telas:<br>- Listagem de histórico com filtros avançados e paginação<br>- Modal/página de detalhamento de alteração com diff visual (before/after)<br>- Opção de exportação de dados |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** Sistema de auditoria com triggers/interceptors para capturar todas as operações (CREATE, UPDATE, DELETE) em entidades críticas. API de consulta com filtros e paginação. Geração de arquivos CSV/Excel<br>**Frontend:** Tela de histórico com filtros, tabela paginada, modal de detalhes com diff visual, botão de exportação |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com todas as US anteriores (US1-US4) capturando operações em funcionários, fornecedores e estoque. Será utilizado para rastreabilidade de pedidos de compra (US6, US7) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 13 pontos no Planning Poker (complexidade alta - sistema transversal) |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabela audit_log com campos: id, entity_type, entity_id, operation, user_id, timestamp, ip_address, before_data, after_data)<br>- Estratégia de implementação (triggers, ORM hooks, ou interceptors)<br>- Política de retenção de dados (quanto tempo manter histórico) |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre quais entidades devem ser auditadas e formato de exportação |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US5 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 6 CAs:<br>- CA1: Visualização com filtros funcionais<br>- CA2: Detalhamento completo de alterações com diff<br>- CA3: Auditoria automática de todas operações<br>- CA4: Paginação e performance adequadas<br>- CA5: Histórico read-only (sem edição/exclusão)<br>- CA6: Exportação funcionando corretamente |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada:<br>- Auditoria não impacta performance das operações CRUD existentes<br>- Middleware de autenticação funcionando<br>- Todas as entidades críticas sendo auditadas automaticamente<br>- Fluxo de login redirecionando corretamente |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Sistema de auditoria eficiente e performático<br>- Estratégia de captura de alterações validada<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Criar/editar/inativar funcionário → verificar registro no histórico<br>- Criar/editar/inativar fornecedor → verificar registro no histórico<br>- Criar/editar item de estoque → verificar registro no histórico<br>- Filtros funcionando corretamente<br>- Exportação gerando arquivos válidos<br>- Performance adequada com grande volume de dados |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/audit-logs` - Listar histórico com filtros (entity, operation, user, date_from, date_to, page, limit)<br>- `GET /api/audit-logs/:id` - Detalhes de uma alteração específica<br>- `GET /api/audit-logs/export` - Exportar histórico (formato: csv ou xlsx)<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response<br>- Códigos de status HTTP<br>- Estrutura do diff (before/after)<br>- Permissões necessárias (perfil gerente)<br><br>**Documentação técnica:**<br>- Como o sistema de auditoria funciona (triggers/hooks/interceptors)<br>- Quais entidades são auditadas<br>- Política de retenção de dados |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou a visualização e filtros<br>- Teste de carga realizado para validar performance<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Captura correta de operações (CREATE, UPDATE, DELETE)<br>- Serialização de dados before/after<br>- Geração de diff entre versões<br>- Filtros de consulta<br><br>**Backend - Testes de Integração:**<br>- Auditoria automática ao criar/editar/deletar entidades<br>- Consultas com múltiplos filtros<br>- Paginação<br>- Exportação de dados<br>- Performance com grande volume (teste de carga)<br><br>**Frontend - Testes E2E:**<br>- Fluxo completo de visualização e filtragem<br>- Exibição correta de diff visual<br>- Exportação de arquivo |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar histórico<br>- Apenas perfil "gerente" pode visualizar histórico completo<br>- Histórico é read-only (sem endpoints de UPDATE/DELETE)<br>- Dados sensíveis (senhas) não são registrados no histórico<br>- IPs são registrados para rastreabilidade<br>- Proteção contra SQL Injection em filtros<br>- Rate limiting em endpoints de exportação<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden) |
