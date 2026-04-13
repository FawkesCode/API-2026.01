# US003 - Gestão de Fornecedores

**Prioridade:** Alta | **Estimativa:** 5 SP | **Sprint:** 1

## 1. Descrição
Como **gerente**, quero **cadastrar, editar e inativar fornecedores**, para **centralizar informações e agilizar futuras cotações e pedidos**.

---

## 2. Critérios de Aceite
* **CA1: Validação de Campos Obrigatórios**
    * **Dado** que tento salvar um fornecedor sem CNPJ ou Nome Fantasia;
    * **Então** o sistema deve exibir um alerta de campo obrigatório.
* **CA2: Edição de Informações**
    * **Dado** que os dados de contato de um fornecedor mudaram;
    * **Quando** atualizo o cadastro;
    * **Então** o sistema deve refletir essas mudanças em todos os novos pedidos de compra.

---

## 3. Checklist DoR
- [x] Descrição seguindo o padrão Ágil.
- [x] Critérios de aceite definidos.
- [x] Documentos de apoio (lista de atributos) anexados.

## 4. Checklist DoD
- [ ] CRUD funcional e testado.
- [ ] Persistência em banco de dados validada.