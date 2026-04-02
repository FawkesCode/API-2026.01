# User Story 3

**Como** gerente, **quero** cadastrar, editar e inativar fornecedores com todos os seus atributos, **para** centralizar informações e agilizar futuras cotações e pedidos.

---

## Critérios de Aceite (CA)

### CA1: Cadastro de novo fornecedor com sucesso
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de fornecedores  
**E** preencho todos os campos obrigatórios (razão social, CNPJ, contato, telefone, e-mail)  
**E** clico em "Cadastrar"  
**Então** o fornecedor é criado com sucesso  
**E** uma mensagem de confirmação é exibida  
**E** o fornecedor aparece na listagem de fornecedores ativos

### CA2: Edição dos dados de um fornecedor existente
**Dado** que sou um gerente autenticado no sistema  
**E** existe um fornecedor cadastrado  
**Quando** eu acesso a tela de edição deste fornecedor  
**E** altero os dados (razão social, endereço, contato, telefone, e-mail, etc.)  
**E** clico em "Salvar"  
**Então** as alterações são persistidas no banco de dados  
**E** uma mensagem de sucesso é exibida  
**E** os dados atualizados são refletidos na listagem

### CA3: Inativação de um fornecedor (soft delete)
**Dado** que sou um gerente autenticado no sistema  
**E** existe um fornecedor ativo no sistema  
**Quando** eu seleciono a opção de inativar este fornecedor  
**E** confirmo a ação  
**Então** o fornecedor é marcado como inativo (soft delete)  
**E** não aparece mais na listagem de fornecedores ativos por padrão  
**E** uma mensagem de confirmação é exibida

### CA4: Validação de CNPJ duplicado
**Dado** que sou um gerente autenticado no sistema  
**E** já existe um fornecedor cadastrado com o CNPJ "12.345.678/0001-90"  
**Quando** tento cadastrar um novo fornecedor com o mesmo CNPJ  
**E** clico em "Cadastrar"  
**Então** o sistema exibe uma mensagem de erro informando que o CNPJ já está em uso  
**E** o cadastro não é realizado

### CA5: Validação de campos obrigatórios
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu acesso a tela de cadastro de fornecedores  
**E** tento cadastrar sem preencher um ou mais campos obrigatórios (razão social, CNPJ, contato principal)  
**E** clico em "Cadastrar"  
**Então** o sistema exibe mensagens de erro indicando os campos obrigatórios não preenchidos  
**E** o cadastro não é realizado até que todos os campos sejam preenchidos corretamente

### CA6: Validação de formato de CNPJ
**Dado** que sou um gerente autenticado no sistema  
**Quando** eu tento cadastrar um fornecedor com CNPJ em formato inválido  
**E** clico em "Cadastrar"  
**Então** o sistema exibe uma mensagem de erro "CNPJ inválido"  
**E** o cadastro não é realizado

---

## Definition of Ready

| Critério | Descrição | Status para US3 |
|----------|-----------|-----------------|
| 1. Clareza na Descrição | A User Story está escrita no formato "Como [persona], quero [ação] para que [objetivo]" | ✅ Definida: Gerente pode gerenciar fornecedores e seus atributos |
| 2. Critérios de Aceite / Cenários de Teste | A história possui critérios objetivos que indicam o que é necessário para considerá-la concluída, escrita no formato (Dado, Quando, Então) | ✅ 6 critérios definidos cobrindo CRUD completo, validações de CNPJ e campos obrigatórios |
| 3. Independente | A história pode ser implementada sem depender de outra tarefa da mesma Sprint. | ⚠️ **Depende da US1 (autenticação)** - necessário ter login funcional e controle de permissões por perfil |
| 4. Referência Visual no Figma | O protótipo correspondente está disponível e vinculado (quando aplicável ao front-end). | 📋 **Requerido**: Protótipos das 3 telas principais:<br>- Listagem de fornecedores com filtros (ativos/inativos) e ações<br>- Formulário de cadastro de fornecedor com todos os atributos<br>- Formulário de edição de fornecedor |
| 5. Escopo Técnico Validado | Está claro se a história envolve frontend, backend ou ambos. | ✅ **Fullstack:**<br>**Backend:** CRUD API completa (GET, POST, PUT, DELETE) para entidade Fornecedores com validações (CNPJ único, formato válido) e soft delete<br>**Frontend:** 3 telas (listagem, cadastro, edição) com formulários validados e integração com API. Máscara de CNPJ e validação client-side |
| 6. Integração com Histórias | A funcionalidade se conecta logicamente com Sprints anteriores. | ✅ Integra com US1 usando middleware de autenticação e verificação de perfil "gerente". Será utilizada nas US10 (cotações) e US6 (pedidos de compra) |
| 7. Estimável | A história foi pontuada no Planning Poker ou tem uma estimativa clara. | ✅ Estimada em 5 pontos no Planning Poker |
| 8. Documentos de Apoio | Há arquivos, links, instruções adicionais quando necessário. | 📋 Necessário:<br>- Modelagem do banco (tabela suppliers com soft delete)<br>- Lista completa de atributos do fornecedor (razão social, CNPJ, IE, endereço completo, contato, telefone, e-mail, condições de pagamento, prazo de entrega, observações)<br>- Biblioteca para validação de CNPJ |
| 9. Validação com PO | A história foi discutida com o PO e validada com o time técnico. | 📋 Pendente validação com PO sobre atributos completos do fornecedor |

---

## Definition of Done

| Critério | Descrição | Detalhamento para US3 |
|----------|-----------|----------------------|
| 1. Critérios de Aceitação Atendidos | Todos os critérios definidos na US foram implementados e validados com sucesso. | ✅ Validar os 6 CAs:<br>- CA1: Cadastro com todos os atributos<br>- CA2: Edição de dados<br>- CA3: Soft delete funcional<br>- CA4: Validação de CNPJ único<br>- CA5: Validação de campos obrigatórios<br>- CA6: Validação de formato de CNPJ |
| 2. Integração com o Sistema Existente | Não há quebra do fluxo existente; funcionalidades se integram ao sistema principal. | ✅ Integração verificada com US1:<br>- Middleware de autenticação funcionando<br>- Rotas protegidas por perfil "gerente"<br>- Fluxo de login redirecionando corretamente |
| 3. Código Revisado | A funcionalidade passou por revisão de código entre pares. | ✅ Code review aprovado por pelo menos 1 desenvolvedor<br>- Padrões de código seguidos<br>- Validações de CNPJ implementadas corretamente<br>- Sem code smells críticos |
| 4. Funcionalidade Integrada | Foi testada junto com o fluxo completo da aplicação. | ✅ Testes de integração validados:<br>- Login como gerente → listar fornecedores<br>- Cadastrar → editar → inativar fornecedor<br>- Filtros de fornecedores ativos/inativos funcionando<br>- Tentativas de acesso sem permissão bloqueadas |
| 5. Documentação Atualizada | Endpoints, autenticação, permissões e anotações técnicas foram documentadas. | ✅ **Documentação obrigatória:**<br>**API Endpoints:**<br>- `GET /api/suppliers` - Listar fornecedores (query param: status=active/inactive/all)<br>- `GET /api/suppliers/:id` - Buscar fornecedor por ID<br>- `POST /api/suppliers` - Cadastrar fornecedor<br>- `PUT /api/suppliers/:id` - Editar fornecedor<br>- `DELETE /api/suppliers/:id` - Inativar fornecedor (soft delete)<br><br>**Incluir em cada endpoint:**<br>- Payload de request/response com todos os atributos<br>- Códigos de status HTTP<br>- Regras de validação (CNPJ, campos obrigatórios)<br>- Permissões necessárias (perfil gerente) |
| 6. Time Validou Funcionalmente | O time testou e aprovou a funcionalidade. | ✅ Validação funcional realizada:<br>- QA testou todos os cenários (CAs)<br>- PO aprovou a implementação<br>- Demo realizada para stakeholders |
| 7. Testes Automatizados | Cobertura de testes adequada para garantir qualidade e manutenibilidade. | ✅ **Testes obrigatórios:**<br>**Backend - Testes de Unidade:**<br>- Validação de campos obrigatórios<br>- Validação de CNPJ único<br>- Validação de formato de CNPJ<br>- Lógica de soft delete<br><br>**Backend - Testes de Integração:**<br>- Fluxo CRUD completo<br>- Autenticação e autorização (apenas gerente)<br>- Persistência no banco de dados<br>- Consultas com filtros (ativos/inativos)<br><br>**Frontend - Testes de Componente:**<br>- Validação de máscara de CNPJ<br>- Validação de campos obrigatórios<br>- Fluxo de cadastro e edição |
| 8. Segurança Validada | Controle de acesso e permissões implementados corretamente. | ✅ **Requisitos de segurança:**<br>- Apenas usuários autenticados podem acessar os endpoints<br>- Apenas perfil "gerente" pode gerenciar fornecedores<br>- Validação de token JWT em todas as requisições<br>- Proteção contra SQL Injection e XSS<br>- Sanitização de dados de entrada (especialmente CNPJ)<br>- Teste de tentativa de acesso sem permissão (deve retornar 403 Forbidden) |
