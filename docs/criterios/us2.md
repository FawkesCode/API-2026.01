# US002 - Gestão de Funcionários e Perfis

**Prioridade:** Alta | **Estimativa:** 5 SP | **Sprint:** 1

## 1. Descrição
Como **gerente**, quero **cadastrar, editar e desativar funcionários com seus respectivos perfis**, para **controlar as permissões e associar responsabilidades dentro do sistema**.

---

## 2. Critérios de Aceite 
* **CA1: Cadastro de novo funcionário**
    * **Dado** que preencho o formulário com Nome, E-mail e Perfil;
    * **Quando** clico em "Salvar";
    * **Então** o funcionário deve ser listado como "Ativo" no sistema.
* **CA2: Edição de dados e permissões**
    * **Dado** que seleciono um funcionário existente;
    * **Quando** altero seu perfil de acesso;
    * **Então** as novas permissões devem ser aplicadas no próximo login do usuário.
* **CA3: Desativação de conta**
    * **Dado** que um funcionário foi desligado;
    * **Quando** altero seu status para "Inativo";
    * **Então** ele deve perder o acesso ao sistema imediatamente.

---

## 3. Checklist DoR
- [x] Descrição clara e concisa.
- [x] Critérios de Aceite objetivos.
- [x] Escopo técnico validado.
- [x] História estimável.

## 4. Checklist DoD
- [ ] Interface integrada ao backend.
- [ ] Logs de alteração implementados.
- [ ] Sem bugs críticos em ambiente de homologação.