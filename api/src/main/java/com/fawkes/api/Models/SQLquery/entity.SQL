CREATE DATABASE IF NOT EXISTS FAWKES;

USE FAWKES;

--SESSÃO DE USUÁRIOS E GRUPOS--

CREATE TABLE IF NOT EXISTS usuarios (


    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    tipo_usuario ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    departamento_usuario ENUM('TI', 'RH', 'Financeiro', 'Vendas') NOT NULL,
    grupo_id INT NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id)
    --definir se, haverá usuario admin
    --definir se haverá email
    --departamento é enum ??
);

CREATE TABLE IF NOT EXISTS grupos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_grupo VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS permissoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_permissao VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
    --definir se as permissões serão enum
    --se for enum, definir quais serão as permissões
);

CREATE TABLE IF NOT EXISTS grupo_permissoes (
    grupo_id INT NOT NULL,
    permissao_id INT NOT NULL,
    PRIMARY KEY (grupo_id, permissao_id),
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

--SESSÃO DE USUÁRIOS E GRUPOS--

--SESSÃO DE PEDIDOS E COMPRAS--

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    criado_as TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_departamento INT NOT NULL,
    id_nota_fiscal INT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    STATUS ENUM('pendente', 'processando', 'concluido', 'cancelado') DEFAULT 'pendente',
    valor DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id),
    FOREIGN KEY (id_nota_fiscal) REFERENCES notas_fiscais(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS departamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_departamento VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS notas_fiscais(
    id INT AUTO_INCREMENT PRIMARY KEY,
    chave_acesso VARCHAR(255) NOT NULL UNIQUE,
    serie VARCHAR(255) NOT NULL,
    data_emissao DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS saidas(
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_saida DATE NOT NULL,
    id_pedido_FK INT NOT NULL,
    FOREIGN KEY (id_pedido_FK) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS estoque(
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id_FK INT DEFAULT NULL,
    qtd_produtos INT NOT NULL,
    FOREIGN KEY (product_id_FK) REFERENCES 
);

CREATE TABLE IF NOT EXISTS fornecedor(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_fornecedor VARCHAR(255),
    cnpj_fornecedor VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS produtos(
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255),
    product_type VARCHAR(255),
    fornecedor_id_FK INT NOT NULL,
    FOREIGN KEY (fornecedor_id_FK) REFERENCES ON fornecedor(id)
);

