# User Story 4

**Como** gerente, **quero** cadastrar itens no estoque com quantidade inicial, unidade de medida e nível mínimo, **para** ter uma base de produtos disponível antes de registrar movimentações.

---

## Critérios de Aceite (CA)

### CA1: Cadastro de novo item de estoque com sucesso
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de itens de estoque  
**E** preencho todos os campos obrigatórios (nome do item, descrição, quantidade inicial, unidade de medida, nível mínimo)  
**E** clico em "Cadastrar"  
**Então** o item é criado com sucesso no estoque  
**E** uma mensagem de confirmação é exibida  
**E** o item aparece na listagem de estoque com a quantidade inicial registrada

### CA2: Edição dos dados de um item de estoque existente
**Dado** que sou um gerente autenticado no sistema  
**E** existe um item cadastrado no estoque  
**Quando** eu acesso a tela de edição deste item  
**E** altero os dados (nome, descrição, unidade de medida, nível mínimo)  
**E** clico em "Salvar"  
**Então** as alterações são persistidas no banco de dados  
**E** uma mensagem de sucesso é exibida  
**E** os dados atualizados são refletidos na listagem

### CA3: Validação de campos obrigatórios
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de itens de estoque  
**E** tento cadastrar sem preencher um ou mais campos obrigatórios (nome, unidade de medida, nível mínimo)  
**E** clico em "Cadastrar"  
**Então** o sistema exibe mensagens de erro indicando os campos obrigatórios não preenchidos  
**E** o cadastro não é realizado até que todos os campos sejam preenchidos corretamente

### CA4: Validação de quantidade inicial não negativa
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu tento cadastrar um item com quantidade inicial negativa  
**E** clico em "Cadastrar"  
**Então** o sistema exibe uma mensagem de erro "A quantidade inicial não pode ser negativa"  
**E** o cadastro não é realizado

### CA5: Validação de nível mínimo não negativo
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu tento cadastrar um item com nível mínimo negativo  
**E** clico em "Cadastrar"  
**Então** o sistema exibe uma mensagem de erro "O nível mínimo não pode ser negativo"  
**E** o cadastro não é realizado

### CA6: Alerta visual de estoque abaixo do nível mínimo
**Dado** que sou um gerente autenticado no sistema  
**E** existe um item no estoque com quantidade atual abaixo do nível mínimo  
**Quando** eu acesso a listagem de itens de estoque  
**Então** o item é destacado visualmente (ex: cor vermelha ou ícone de alerta)  
**E** é possível identificar facilmente itens que precisam de reposição

---

## Definition of Ready

| Critério | Descrição | Status para US4 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode cadastrar e gerenciar itens de estoque com controles de quantidade |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 6 critérios definidos cobrindo cadastro, edição, validações de quantidade e alertas de nível mínimo |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende da US1 (autenticação)** - necessário ter login funcional e controle de permissões por perfil |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das 3 telas principais:<br>- Listagem de itens de estoque com indicadores de nível (verde/amarelo/vermelho)<br>- Formulário de cadastro de item com campos de quantidade e nível mínimo<br>- Formulário de edição de item (sem alterar quantidade, apenas dados cadastrais) |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** CRUD API completa (GET, POST, PUT) para entidade Estoque com validações de quantidade não negativa e cálculo de status (acima/abaixo do mínimo)<br>**Frontend:** 3 telas (listagem com alertas visuais, cadastro, edição) com validação de números e integração com API. Indicadores visuais de status de estoque |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 usando middleware de autenticação e verificação de perfil "gerente". Será base para US8 (movimentações de estoque), US6 (pedidos de compra) e US11 (dashboard com alertas) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 8 pontos no Planning Poker |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabela inventory_items com campos: id, name, description, current_quantity, unit, minimum_level, created_at, updated_at)<br>- Lista de unidades de medida padrão (UN, KG, L, M, CX, PCT, etc.)<br>- Regras de negócio para cálculo de status de estoque |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre unidades de medida aceitas e regras de alerta |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US4 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 6 CAs:<br>- CA1: Cadastro com quantidade inicial e nível mínimo<br>- CA2: Edição de dados cadastrais<br>- CA3: Validação de campos obrigatórios<br>- CA4: Validação de quantidade inicial não negativa<br>- CA5: Validação de nível mínimo não negativo<br>- CA6: Alerta visual de estoque baixo |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada com US1:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "gerente"<br>- Fluxo de login redirecionando corretamente |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Validações de quantidade implementadas corretamente<br>- Lógica de cálculo de status validada<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Login como gerente → listar itens de estoque<br>- Cadastrar item com quantidade inicial → verificar na listagem<br>- Editar item → verificar atualização<br>- Visualizar alertas de estoque baixo<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/inventory/items` - Listar itens de estoque (com status: all/low-stock)<br>- `GET /api/inventory/items/:id` - Buscar item por ID<br>- `POST /api/inventory/items` - Cadastrar item de estoque<br>- `PUT /api/inventory/items/:id` - Editar item de estoque<br><br>**Response deve incluir:**<br>- Campo calculado `status` (ok/warning/critical) baseado em current_quantity vs minimum_level<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response<br>- Códigos de status HTTP<br>- Regras de validação (quantidade >= 0, nível mínimo >= 0)<br>- Permissões necessárias (perfil gerente)<br>- Regras de negócio para cálculo de status |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou a implementação e os indicadores visuais<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Validação de campos obrigatórios<br>- Validação de quantidade inicial não negativa<br>- Validação de nível mínimo não negativo<br>- Cálculo correto de status (ok/warning/critical)<br><br>**Backend - Testes de Integração:**<br>- Fluxo CRUD completo<br>- Autenticação e autorização (apenas gerente)<br>- Persistência no banco de dados<br>- Consultas com filtros de status<br><br>**Frontend - Testes de Componente:**<br>- Validação de campos numéricos<br>- Exibição correta de alertas visuais<br>- Fluxo de cadastro e edição |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar os endpoints<br>- Apenas perfil "gerente" pode cadastrar e editar itens de estoque<br>- Validação de token JWT em todas as requisições<br>- Proteção contra SQL Injection e XSS<br>- Validação de tipos de dados (números não negativos)<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden) |
