# User Story 2

**Como** gerente, **quero** cadastrar, editar e desativar funcionários com seus respectivos perfis de acesso, **para** controlar as permissões e associar responsabilidades dentro do sistema.

---

## Critérios de Aceite (CA)

### CA1: Cadastro de novo funcionário com sucesso
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de funcionários  
**E** preencho todos os campos obrigatórios (nome, e-mail, perfil)  
**E** clico em "Cadastrar"  
**Então** o funcionário é criado com sucesso  
**E** uma mensagem de confirmação é exibida  
**E** o funcionário aparece na listagem com o perfil associado

### CA2: Edição dos dados de um funcionário existente
**Dado** que sou um gerente autenticado no sistema  
**E** existe um funcionário cadastrado  
**Quando** eu acesso a tela de edição deste funcionário  
**E** altero os dados (nome, e-mail ou perfil)  
**E** clico em "Salvar"  
**Então** as alterações são persistidas no banco de dados  
**E** uma mensagem de sucesso é exibida  
**E** os dados atualizados são refletidos na listagem

### CA3: Desativação de um funcionário (soft delete)
**Dado** que sou um gerente autenticado no sistema  
**E** existe um funcionário ativo no sistema  
**Quando** eu seleciono a opção de desativar este funcionário  
**E** confirmo a ação  
**Então** o funcionário é marcado como inativo (soft delete)  
**E** não poderá mais fazer login no sistema  
**E** não aparece mais na listagem de funcionários ativos  
**E** uma mensagem de confirmação é exibida

### CA4: Validação de e-mail duplicado
**Dado** que sou um gerente autenticado no sistema  
**E** já existe um funcionário cadastrado com o e-mail "joao@empresa.com"  
**Quando** tento cadastrar um novo funcionário com o mesmo e-mail  
**E** clico em "Cadastrar"  
**Então** o sistema exibe uma mensagem de erro informando que o e-mail já está em uso  
**E** o cadastro não é realizado

### CA5: Validação de campos obrigatórios
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de funcionários  
**E** tento cadastrar sem preencher um ou mais campos obrigatórios (nome, e-mail, perfil)  
**E** clico em "Cadastrar"  
**Então** o sistema exibe mensagens de erro indicando os campos obrigatórios não preenchidos  
**E** o cadastro não é realizado até que todos os campos sejam preenchidos corretamente

---

## Definition of Ready

| Critério | Descrição | Status para US2 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode gerenciar funcionários e perfis |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 5 critérios definidos cobrindo CRUD completo e validações |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende da US1 (autenticação)** - necessário ter login funcional e controle de permissões por perfil |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das 3 telas principais:<br>- Listagem de funcionários com filtros e ações<br>- Formulário de cadastro de funcionário<br>- Formulário de edição de funcionário |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** CRUD API completa (GET, POST, PUT, DELETE) para entidade Funcionários com validações e soft delete<br>**Frontend:** 3 telas (listagem, cadastro, edição) com formulários validados e integração com API |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 usando middleware de autenticação e verificação de perfil "gerente" |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | 📋 A estimar no Planning Poker (sugestão: 8-13 pontos - complexidade média/alta) |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabela employees com soft delete)<br>- Estrutura de perfis disponíveis no sistema |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US2 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 5 CAs:<br>- CA1: Cadastro com perfil associado<br>- CA2: Edição de dados<br>- CA3: Soft delete funcional<br>- CA4: Validação de e-mail único<br>- CA5: Validação de campos obrigatórios |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada com US1:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "gerente"<br>- Fluxo de login redirecionando corretamente |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Login como gerente → listar funcionários<br>- Cadastrar → editar → desativar funcionário<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/employees` - Listar funcionários ativos<br>- `GET /api/employees/:id` - Buscar funcionário por ID<br>- `POST /api/employees` - Cadastrar funcionário<br>- `PUT /api/employees/:id` - Editar funcionário<br>- `DELETE /api/employees/:id` - Desativar funcionário (soft delete)<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response<br>- Códigos de status HTTP<br>- Regras de validação<br>- Permissões necessárias (perfil gerente) |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou a implementação<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Validação de campos obrigatórios<br>- Validação de e-mail único<br>- Lógica de soft delete<br>- Associação de perfis<br><br>**Backend - Testes de Integração:**<br>- Fluxo CRUD completo<br>- Autenticação e autorização (apenas gerente)<br>- Persistência no banco de dados<br><br>**Frontend - Testes E2E (opcional mas recomendado):**<br>- Fluxo completo de cadastro e edição |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar os endpoints<br>- Apenas perfil "gerente" pode acessar funcionalidades de CRUD de funcionários<br>- Validação de token JWT em todas as requisições<br>- Dados sensíveis (senhas) não retornados nas consultas<br>- Proteção contra SQL Injection e XSS<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden) |
