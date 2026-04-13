# US001 - Autenticação de Usuário

**Prioridade:** Alta | **Estimativa:** 8 SP | **Sprint:** 1

## 1. Descrição
Como **gerente**, quero **realizar login no sistema com usuário e senha**, para que **somente pessoas autorizadas acessem as funcionalidades de acordo com seu perfil**.

---

## 2. Critérios de Aceite 
* **CA1: Login com sucesso**
    * **Dado** que o gerente está na página de login;
    * **Quando** insere um usuário e senha válidos;
    * **Então** o sistema deve autenticar o usuário e redirecioná-lo para a Dashboard principal.
* **CA2: Login inválido**
    * **Dado** que o gerente insere credenciais incorretas;
    * **Quando** clica no botão "Entrar";
    * **Então** uma mensagem de erro deve ser exibida e o acesso negado.
* **CA3: Restrição de Perfil**
    * **Dado** que um usuário autenticado tenta acessar uma URL restrita ao seu perfil;
    * **Então** o sistema deve exibir uma página de "Acesso Negado".

---

## 3. Checklist Definition of Ready (DoR)
- [x] Descrição clara (Persona, Ação, Objetivo).
- [x] Critérios de Aceite definidos em formato BDD.
- [x] Independente de outras histórias da Sprint.
- [ ] Protótipo no Figma vinculado.
- [x] Escopo técnico (Front/Back) validado.
- [x] Estimativa definida.

## 4. Checklist Definition of Done (DoD)
- [ ] Código revisado por pares.
- [ ] Testes unitários implementados.
- [ ] Documentação de API (Swagger) atualizada.
- [ ] Funcionalidade validada pelo PO.