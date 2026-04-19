# Manual de Usuário — Sistema Fawkes

Este documento orienta o uso do sistema Fawkes para gestão de compras e estoque.

---

## 1. Visão Geral

O **Fawkes** é um sistema desktop para gestão completa do processo de compras da Newe Log. Ele permite:

- Login e controle de acesso por perfil
-Cadastro de funcionários, fornecedores e produtos
- Abertura e aprovação de pedidos de compra
- Controle de estoque com alertas de nível mínimo
- Módulo de cotações entre fornecedores
- Registro de recebimento de mercadorias
- Dashboard gerencial com indicadores

---

## 2. Perfis de Usuário

O sistema possui três perfis com permissões específicas:

| Perfil | Descrição | Permissões |
|-------|----------|-----------|
| **Gerente** | Gestão total | Cadastros, pedidos, aprovações, estoque, relatórios |
| **Operacional** | Operação diária | Abrir pedidos, consultar próprios pedidos |
| **Diretor** | Aprovação | Aprovar/reprovar pedidos, dashboard |

---

## 3. Tela de Login

Ao abrir o sistema, a primeira tela é o login.

### 3.1 Credenciais

| Perfil | Usuário | Senha |
|-------|--------|------|
| Gerente | `admin` | `admin123` |
| Operacional | `oper1` | `oper123` |
| Diretor | `dir1` | `dir123` |

### 3.2 Fluxo

1. Digite o usuário e senha
2. Clique em "Entrar"
3. O sistema redireciona para a tela inicial conforme o perfil

---

## 4._FUNCIONALIDADES POR PERFIL

## 4.1 Gerente

O manager tem acesso a todas as funcionalidades.

### Cadastro de Funcionários

1. Acesse **Funcionários** no menu
2. Clique em **Novo Funcionário**
3. Preencha os dados:
   - Nome completo
   - CPF
   - E-mail
   - Setor
   - Perfil de acesso
4. Clique em **Salvar**

### Cadastro de Fornecedores

1. Acesse **Fornecedores** no menu
2. Clique em **Novo Fornecedor**
3. Preencha os dados:
   - Razão social
   - CNPJ
   - Endereço
   - Contato
   - Telefone
   - E-mail
4. Clique em **Salvar**

### Cadastro de Produtos (Estoque)

1. Acesse **Estoque** no menu
2. Clique em **Novo Produto**
3. Preencha os dados:
   - Nome do produto
   - Descrição
   - Unidade de medida (un, kg, l, etc.)
   - Quantidade inicial
   - Nível mínimo (alerta de estoque)
4. Clique em **Salvar**

### Abrir Pedido de Compra

1. Acesse **Pedidos** → **Novo Pedido**
2. Preencha:
   - Setor solicitante
   - Itens (produto + quantidade)
   - Justificativa
   - Valor estimado
   - Centro de custo
3. O sistema gera número automático
4. Clique em **Enviar para Aprovação**

### Aprovar Pedidos

1. Acesse **Aprovações**
2. Visualize pedidos pendentes
3. Clique em **Aprovar** ou **Reprovar**
4. Adicione um parecer (obrigatório)
5. Confirme a ação

### Movimentar Estoque

1. Acesse **Estoque** → **Movimentação**
2. Selecione o tipo: **Entrada** ou **Saída**
3. Vincule a um pedido (se aplicável)
4. Selecione os produtos e quantidades
5. Confirme a movimentação

### Realizar Cotação

1. Acesse **Cotações**
2. Selecione o pedido a cotar
3. Adicione fornecedores e valores
4. O sistema compara e indica a melhor opção
5. Selecione o fornecedor vencedor

### Registrar Recebimento

1. Acesse **Recebimento**
2. Selecione o pedido aprovado
3. Confira itens e quantidades
4. Anexe a nota fiscal (PDF/imagem)
5. Confirme — o estoque é atualizado automaticamente

### Ver Histórico

1. Acesse **Histórico**
2. Aplique filtros:
   - Entidade (funcionário, fornecedor, pedido)
   - Período
   - Usuário
   - Tipo de operação
3. Visualize os detalhes de cada alteração

---

## 4.2 Operacional

O perfil operacional tem acesso restrito.

### Abrir Pedido de Compra

1. Acesse **Novo Pedido**
2. Preencha os dados
3. Envie para aprovação

### Consultar Meus Pedidos

1. Acesse **Meus Pedidos**
2. Visualize o status e histórico
3. Não é possível editar após envio

---

## 4.3 Diretor

O perfil diretor tem acesso à aprovação e dashboard.

### Aprovar Pedidos

1. Acesse **Aprovações**
2. Visualize pedidos enviados pelos operacionais
3. Analise a justificativa e valores
4. Clique em **Aprovar** ou **Reprovar**
5. Adicione parecer

### Dashboard Gerencial

1. Acesse **Dashboard**
2. Visualize indicadores:
   - Pedidos por status
   - Gastos por centro de custo
   - Alertas de estoque baixo
   - Evolução de compras
3. Filtre por período

---

## 5. Status dos Pedidos

| Status | Descrição |
|--------|----------|
| **Rascunho** | Em edição (operacional) |
| **Pendente** | Aguardando aprovação do gerente |
| **Aprovado Gerente** | Aprovado pelo gerente |
| **Pendente Diretor** | Aguardando aprovação do diretor |
| **Aprovado** | Aprovado pelo diretor |
| **Reprovado** | Rejeitado (com parecer) |
| **Em Cotação** | Em processo de cotação |
| **Recebido** | Mercadoria recebida e conferida |

---

## 6. Fluxo Completo de um Pedido

```
[1] Operacional abre pedido
        ↓
[2] Gerente aprova (ou reprova)
        ↓
[3] Diretor aprova (ou reprova)
        ↓
[4] Gerente realiza cotação
        ↓
[5] Fornecedor vencedor selecionado
        ↓
[6] Recebimento registrado
        ↓
[7] Estoque atualizado automaticamente
```

---

## 7. Alertas do Sistema

O sistema gera alertas nas seguintes situações:

- **Estoque baixo:** Quando a quantidade atinge o nível mínimo
- **Pedido pendente:** Quando há pedidos aguardando aprovação
- **Prazo excedido:** Quando o pedido não é aprovado em tempo razoável

---

## 8. Relatórios e Exportação

### Exportar Histórico

1. Acesse **Histórico**
2. Aplique os filtros desejados
3. Clique em **Exportar**
4. Escolha o formato (CSV ou Excel)

### Relatório de Gastos

1. Acesse **Dashboard**
2. Selecione o período
3. Visualize o gráfico de gastos por centro de custo

---

## 9. Logout

Para sair do sistema:

1. Clique no menu do usuário (canto superior)
2. Selecione **Sair**
3. Confirme a ação

---

## 10. Suporte

Em caso de dúvidas ou problemas:

- consulte a documentação em `docs/`
- entre em contato com o administrador do sistema
- verifique o Manual de Instalação para problemas técnicos

---

Este manual cobre as principais funcionalidades do sistema Fawkes. Para informações técnicas, consulte o [Manual de Instalação](./manual-instalacao.md).