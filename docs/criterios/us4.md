# US004 - Gestão de Itens de Estoque

**Prioridade:** Alta | **Estimativa:** 8 SP | **Sprint:** 1

## 1. Descrição
Como **gerente**, quero **cadastrar itens no estoque com quantidade inicial, unidade de medida e nível mínimo**, para **ter uma base de produtos disponível antes de registrar movimentações**.

---

## 2. Critérios de Aceite
* **CA1: Cadastro com Nível Mínimo**
    * **Dado** que cadastro um item "Papel A4" com nível mínimo "10";
    * **Quando** o estoque atingir esse valor, o sistema deve sinalizar visualmente a necessidade de reposição.
* **CA2: Unidade de Medida**
    * **Dado** a tela de cadastro;
    * **Então** devo poder selecionar entre UN, KG, L ou CX.

---

## 3. Checklist DoR
- [x] Descrição completa.
- [x] Critérios de aceite técnicos definidos.
- [x] Validação com o time técnico concluída.

## 4. Checklist DoD
- [ ] Lógica de alerta de estoque mínimo validada.
- [ ] Interface amigável para seleção de unidades.