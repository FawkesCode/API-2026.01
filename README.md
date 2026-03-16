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

O **Sistema (nome)** é uma aplicação desktop desenvolvida em Java com JavaFX, voltada à gestão completa do processo de compras da Newe Log.

A solução contempla abertura de pedidos com numeração automática, fluxo de aprovação com parecer registrado, controle de estoque com alertas de nível mínimo, módulo de cotações com comparativo entre fornecedores, registro de recebimento com anexo de nota fiscal e dashboard gerencial com indicadores financeiros e operacionais.

O objetivo é padronizar o processo, eliminar compras não autorizadas e gerar dados estratégicos para tomada de decisão e auditorias futuras.

---

## 📋 Backlog do Produto <a id="backlog"></a>

[Backlog Do Produto](./docs/product-backlog.md)

---

## ✅ DoR — Definition of Ready <a id="dor"></a>

[DOR](./docs/dor.md)

---

## 🏆 DoD — Definition of Done <a id="dod"></a>
[DOR](./docs/dod.md)

---

## 📅 Sprints <a id="sprints"></a>

| Sprint | Período | Meta | Documentação |
|--------|:-------:|------|:------------:|
| 🏃🏻 **Sprint 1** | 16/03 – 05/04 | Login, Funcionários, Fornecedores, Estoque (cadastro) | [Docs](./docs/sprints/sprint-1/backlog.md) |
| 🏃🏻 **Sprint 2** | 13/04 – 03/05 | Pedidos de compra, Aprovação, Movimentação de estoque | [Docs](./docs/sprints/sprint-2/backlog.md) |
| 🏃🏻 **Sprint 3** | 11/05 – 31/05 | Cotações, Dashboard, Histórico, Recebimento | [Docs](./docs/sprints/sprint-3/backlog.md) |

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

### 1. Clonar o repositório

```bash
git clone https://github.com/fawkes-api/sistema-compras.git
cd sistema-compras
```

### 2. Configurar o banco de dados

```bash
mysql -u root -p < migrations/YYYYMMDD_init.sql
```

Configure as credenciais em `src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/newe_compras
db.user=seu_usuario
db.password=sua_senha
```

### 3. Executar a aplicação

```bash
# Via Maven
mvn javafx:run

# Via IDE — adicione ao VM Options:
--module-path /caminho/para/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

> Consulte o [Manual de Instalação](./docs/manual-instalacao.md) para detalhes adicionais.

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
