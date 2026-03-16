<p align="center">
  <img src="docs/img/logo-fawkes.png" alt="Logo Fawkes" width="180"/>
  <h2 align="center">Fawkes</h2>
</p>

<p align="center">
  | <a href="#desafio">Desafio</a> |
  <a href="#solucao">Solução</a> |
  <a href="#backlog">Backlog do Produto</a> |
  <a href="#dor">DoR</a> |
  <a href="#dod">DoD</a> |
  <a href="#sprints">Sprints</a> |
  <a href="#tecnologias">Tecnologias</a> |
  <a href="#como-executar">Como Executar</a> |
  <a href="#equipe">Equipe</a> |
</p>

> **Parceiro:** Newe Log &nbsp;·&nbsp; **Curso:** 2º ADS &nbsp;·&nbsp; **Período:** 2026-1
>
> **Status do Projeto:** Em andamento 🔄

---

## 🏭 Desafio <a id="desafio"></a>

A **Newe Log** enfrenta um processo de compras manual, descentralizado e sujeito a falhas: pedidos sem rastreabilidade, aprovações sem registro formal e estoque sem visibilidade em tempo real. O resultado é compras não autorizadas, retrabalho e dificuldade de auditoria.

O desafio é construir uma ferramenta que organize, governe e dê transparência a todo o ciclo de aquisições — do pedido ao recebimento da mercadoria.

---

## 💡 Solução <a id="solucao"></a>

O **Sistema para Depto de Compras** é uma aplicação desktop desenvolvida em Java com JavaFX, voltada à gestão completa do processo de compras da Newe Log.

A solução contempla abertura de pedidos com numeração automática, fluxo de aprovação com parecer registrado, controle de estoque com alertas de nível mínimo, módulo de cotações com comparativo entre fornecedores, registro de recebimento com anexo de nota fiscal e dashboard gerencial com indicadores financeiros e operacionais.

O objetivo é padronizar o processo, eliminar compras não autorizadas e gerar dados estratégicos para tomada de decisão e auditorias futuras.

---

## 📋 Backlog do Produto <a id="backlog"></a>

| Rank | Prioridade | User Story | Story Points | Sprint |
|:----:|:----------:|------------|:------------:|:------:|
| 1 | Alta | Como gerente, quero realizar login com usuário e senha, para que somente pessoas autorizadas acessem o sistema conforme seu perfil. | 8 | Sprint 1 |
| 2 | Alta | Como gerente, quero cadastrar, editar e desativar funcionários com perfis de acesso, para controlar permissões e responsabilidades. | 5 | Sprint 1 |
| 3 | Alta | Como gerente, quero cadastrar, editar e inativar fornecedores, para centralizar informações e agilizar cotações e pedidos. | 5 | Sprint 1 |
| 4 | Alta | Como gerente, quero cadastrar itens no estoque com quantidade, unidade de medida e nível mínimo, para ter base de produtos disponível. | 8 | Sprint 1 |
| 5 | Alta | Como gerente, quero visualizar o histórico completo de alterações no sistema, para identificar inconsistências e garantir integridade dos dados. | 13 | Sprint 1 ★ |
| 6 | Alta | Como operacional, quero abrir pedido de compra com número automático, informando solicitante, setor, itens, justificativa e centro de custo. | 13 | Sprint 2 |
| 7 | Alta | Como diretor, quero visualizar pedidos pendentes e aprovar ou reprovar com parecer registrado, para autorizar compras antes de sua efetivação. | 8 | Sprint 2 |
| 8 | Média | Como gerente, quero registrar entradas e saídas de estoque vinculadas a pedidos aprovados, para monitorar o fluxo e evitar rupturas. | 8 | Sprint 2 |
| 9 | Média | Como gerente, quero rastrear o status de cada pedido e editar informações incorretas antes da aprovação, para garantir compras sem retrabalho. | 13 | Sprint 2 ★ |
| 10 | Média | Como gerente, quero realizar cotações comparando propostas de múltiplos fornecedores, para selecionar a opção mais vantajosa. | 13 | Sprint 3 |
| 11 | Média | Como diretor, quero visualizar dashboard gerencial com indicadores de pedidos, gastos por centro de custo e alertas de estoque. | 13 | Sprint 3 |
| 12 | Média | Como operacional, quero consultar o status e histórico dos meus pedidos, para acompanhar o andamento das solicitações. | 5 | Sprint 3 |
| 13 | Média | Como gerente, quero registrar o recebimento de mercadorias vinculado ao pedido, conferindo itens e anexando a nota fiscal. | 13 | Sprint 3 |
| 14 | Baixa | Como diretor, quero receber e-mail quando um pedido for submetido para aprovação, para não perder solicitações urgentes. | 13 | Sprint 3 ★ |

> ★ Extra — previsto, sem compromisso de entrega na Sprint

---

## ✅ DoR — Definition of Ready <a id="dor"></a>

Uma User Story só pode entrar em uma Sprint quando **todos** os critérios abaixo estiverem atendidos:

- User Story no formato: *Como `<persona>`, quero `<ação>`, para `<valor>`*
- Objetivo compreendido por todos os membros sem leitura do texto
- Mínimo de 3 critérios de aceitação verificáveis (sim/não)
- Regras de negócio e validações documentadas
- Estimativa atribuída via Planning Poker
- US cabe em uma Sprint (estimativa ≤ 13 pts — se maior, deve ser dividida)
- Sem dependências bloqueadoras não resolvidas
- Wireframe ou esboço de tela aprovado pelo PO
- Diagrama ER refletindo as tabelas necessárias
- Ao menos 2 casos de teste definidos: caminho feliz + 1 cenário de erro

---

## 🏆 DoD — Definition of Done <a id="dod"></a>

Uma US só está **Done** quando todos os itens abaixo forem verificados:

**Funcionalidade**
- Todos os critérios de aceitação atendidos
- Nenhuma funcionalidade anterior quebrada (checklist de regressão executado)

**Código**
- Commits padronizados: `feat|fix|docs|style|refactor|test|chore (#id): descrição`
- Sem commit direto na `main`
- PR aberto e aprovado por ao menos 1 membro antes do merge
- Sem `System.out.println` de debug ou código comentado
- Padrão MVC respeitado — lógica de negócio em Service/Model

**Banco de Dados**
- Script SQL de migração criado e versionado em `/migrations`
- Diagrama ER atualizado em `/docs/modelo-er`

**Testes**
- Casos de teste manuais executados e aprovados
- Cenários de erro testados (dados inválidos, campos vazios)

**Documentação**
- Javadoc nas classes e métodos públicos criados ou alterados
- Manual do Usuário atualizado em `/docs/manual-usuario`
- README / Manual de Instalação atualizado se necessário

**Entrega**
- Funcionalidade demonstrável na Sprint Review em ambiente limpo
- PO validou que a entrega atende à necessidade do parceiro

---

## 📅 Sprints <a id="sprints"></a>

| Sprint | Período | Meta | Documentação |
|--------|:-------:|------|:------------:|
| 🔖 **Sprint 1** | 16/03 – 05/04 | Login, Funcionários, Fornecedores, Estoque (cadastro) | [Docs](./docs/sprints/sprint-1/backlog.md) |
| 🔖 **Sprint 2** | 13/04 – 03/05 | Pedidos de compra, Aprovação, Movimentação de estoque | [Docs](./docs/sprints/sprint-2/backlog.md) |
| 🔖 **Sprint 3** | 11/05 – 31/05 | Cotações, Dashboard, Histórico, Recebimento | [Docs](./docs/sprints/sprint-3/backlog.md) |

---

## 💻 Tecnologias <a id="tecnologias"></a>

<p>
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
  <a href="https://openjfx.io/"><img src="https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white"/></a>
  <a href="https://www.mysql.com/"><img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/></a>
  <a href="https://github.com/"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a>
  <a href="https://www.figma.com/"><img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/></a>
</p>

---

## 🚀 Como Executar <a id="como-executar"></a>

### Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [JavaFX SDK 17+](https://openjfx.io/)
- [MySQL 8+](https://dev.mysql.com/downloads/)
- [Git](https://git-scm.com/)

Consulte o [Manual de Instalação](./docs/manual-instalacao.md) para detalhes adicionais.

---

## 👥 Equipe <a id="equipe"></a>

<div align="center">
  <table>
    <tr>
      <th>Membro</th>
      <th>Função</th>
      <th>GitHub</th>
      <th>LinkedIn</th>
    </tr>
    <tr>
      <td>Thiago [Sobrenome]</td>
      <td>Product Owner</td>
      <td><a href="https://github.com/Pottassiuw"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Marcos [Sobrenome]</td>
      <td>Scrum Master</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Thaís [Sobrenome]</td>
      <td>Desenvolvedora</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Matheus [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>João [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>João [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Vitor [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Gabriel [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
    <tr>
      <td>Adler [Sobrenome]</td>
      <td>Desenvolvedor</td>
      <td><a href="#"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a></td>
      <td><a href="#"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/></a></td>
    </tr>
  </table>
</div>

---

<sub>Fawkes · 2º ADS · Fatec SJC · 2026-1</sub>
