# User Story 1

**Como** gerente, **quero** realizar login no sistema com usuário e senha, **para que** somente pessoas autorizadas acessem as funcionalidades de acordo com seu perfil.

---

## Critérios de Aceite (CA)

### CA1: Login com Sucesso
**Dado** que o usuário está na tela de login  
**Quando** ele insere credenciais válidas (usuário e senha corretos)  
**Então** o sistema autentica o usuário, gera um token de sessão e redireciona para a página inicial de acordo com seu perfil

### CA2: Tentativa de Login com Credenciais Inválidas
**Dado** que o usuário está na tela de login  
**Quando** ele insere credenciais inválidas (usuário inexistente ou senha incorreta)  
**Então** o sistema exibe uma mensagem de erro "Usuário ou senha inválidos" e não permite o acesso ao sistema

### CA3: Tentativa de Acesso a Página Protegida sem Autenticação
**Dado** que o usuário não está autenticado no sistema  
**Quando** ele tenta acessar diretamente uma URL de página protegida  
**Então** o sistema redireciona automaticamente para a tela de login

### CA4: Validação de Campos Obrigatórios
**Dado** que o usuário está na tela de login  
**Quando** ele tenta submeter o formulário sem preencher o campo de usuário ou senha  
**Então** o sistema exibe mensagens de erro nos campos vazios indicando "Campo obrigatório" e não permite o envio do formulário

---

## Definition of Ready

| Critério | Descrição |
|----------|-----------|
| 1. Clareza na Descrição | ✅ A User Story está escrita no formato "Como gerente, quero realizar login no sistema com usuário e senha, para que somente pessoas autorizadas acessem as funcionalidades de acordo com seu perfil" |
| 2. Critérios de Aceite / Cenários de Teste | ✅ A história possui 4 critérios de aceite escritos no formato (Dado, Quando, Então) cobrindo: login com sucesso, credenciais inválidas, acesso sem autenticação e validação de campos |
| 3. Independente | ⚠️ A história possui dependência parcial: pode ser implementada na Sprint atual, mas o controle de acesso completo pós-login depende da US2 (estrutura de perfis de usuário) |
| 4. Referência Visual no Figma | ✅ O protótipo da tela de login está disponível no Figma com especificações de layout, campos (usuário, senha), botão de login e mensagens de erro |
| 5. Escopo Técnico Validado | ✅ **Frontend:** Criação de formulário de login com validação de campos e gerenciamento de estado de autenticação. **Backend:** API REST de autenticação (POST /auth/login) com validação de credenciais e geração de token JWT |
| 6. Integração com Histórias | ✅ Esta é a primeira US do projeto e serve como base para todas as funcionalidades protegidas. Conecta-se com US2 (perfis de usuário) para controle de acesso |
| 7. Estimável | ✅ A história foi estimada em Planning Poker considerando complexidade de autenticação, validações e integração frontend-backend |
| 8. Documentos de Apoio | ✅ Disponíveis: link do protótipo Figma, especificação de segurança (hash de senha, HTTPS), formato do token JWT e estrutura de resposta da API |
| 9. Validação com PO | ✅ A história foi validada pelo PO quanto aos fluxos de autenticação, mensagens de erro e comportamento esperado do sistema |

---

## Definition of Done

| Critério | Descrição |
|----------|-----------|
| 1. Critérios de Aceitação Atendidos | ✅ Todos os 4 critérios de aceite foram implementados: login com sucesso, tratamento de credenciais inválidas, proteção de rotas e validação de campos obrigatórios |
| 2. Testes Implementados | ✅ **Backend:** Testes unitários para lógica de autenticação (validação de credenciais, geração de token, verificação de hash). **Frontend:** Testes de componente para formulário de login (validações, submissão, exibição de erros) |
| 3. Segurança Implementada | ✅ Senhas armazenadas com hash seguro (bcrypt ou argon2), tokens JWT assinados, comunicação exclusivamente via HTTPS, proteção contra ataques de força bruta implementada |
| 4. Integração com o Sistema Existente | ✅ Middleware de autenticação implementado para proteger rotas, não há quebra do fluxo existente, token persistido no frontend (localStorage/sessionStorage) |
| 5. Código Revisado | ✅ A funcionalidade passou por revisão de código (code review) entre pares, seguindo padrões de código do projeto |
| 6. Funcionalidade Integrada | ✅ Testada junto com o fluxo completo: login → autenticação → acesso a páginas protegidas → logout |
| 7. Documentação Atualizada | ✅ **API:** Endpoint POST /auth/login documentado (OpenAPI/Swagger) com parâmetros, respostas e códigos de status. **Técnica:** Fluxo de autenticação, estrutura do token JWT e configuração de variáveis de ambiente documentados |
| 8. Time Validou Funcionalmente | ✅ O time testou e aprovou: fluxo de login, mensagens de erro, proteção de rotas e experiência do usuário |
