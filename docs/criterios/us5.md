# US005 - Auditoria do Sistema

**Prioridade:** Alta | **Estimativa:** 13 SP | **Sprint:** 1

## 1. Descrição
Como **gerente**, quero **visualizar o histórico completo de alterações realizadas no sistema**, para **identificar inconsistências e garantir a integridade dos dados**.

---

## 2. Critérios de Aceite
* **CA1: Registro de Logs**
    * **Dado** que qualquer usuário realize um Cadastro, Edição ou Exclusão;
    * **Então** o sistema deve gravar: Usuário, Data/Hora, Ação e Valor Anterior/Novo.
* **CA2: Filtros de Busca**
    * **Dado** a tela de histórico;
    * **Quando** filtro por "Período" ou "Funcionário";
    * **Então** o sistema deve exibir apenas os registros correspondentes.

---

## 3. Checklist DoR
- [x] Definição clara dos campos de auditoria.
- [x] Validação de performance (histórico pode ser volumoso).

## 4. Checklist DoD
- [ ] Backend registrando logs em todas as tabelas críticas.
- [ ] Interface de visualização performática.