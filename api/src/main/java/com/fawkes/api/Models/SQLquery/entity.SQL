CREATE DATABASE IF NOT EXISTS FAWKES;
USE FAWKES;


CREATE TABLE IF NOT EXISTS grupos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_grupo VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS permissoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_permissao VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS departamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_departamento VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS notas_fiscais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_nota CHAR(45) UNIQUE,
    serie VARCHAR(20),
    data_emissao DATE
);

CREATE TABLE IF NOT EXISTS fornecedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_fornecedor VARCHAR(255),
    cnpj_fornecedor VARCHAR(20) UNIQUE,
    meio_pagamento ENUM("PIX","CREDITO","DEBITO","BOLETO") DEFAULT "PIX"
);


CREATE TABLE IF NOT EXISTS grupo_permissoes (
    grupo_id INT NOT NULL,
    permissao_id INT NOT NULL,
    PRIMARY KEY (grupo_id, permissao_id),
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    esta_ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    grupo_id INT NOT NULL,
    departamento_id INT NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id),
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);

CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_nome VARCHAR(255),
    produto_tipo VARCHAR(255),
    valor_unitario DECIMAL (10,2) NOT NULL,
    descricao TEXT,
    fornecedor_id_FK INT NOT NULL,
    FOREIGN KEY (fornecedor_id_FK) REFERENCES fornecedor(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Corrigida a vírgula aqui
    id_departamento INT NOT NULL,
    id_nota_fiscal INT DEFAULT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    STATUS ENUM('pendente', 'processando', 'concluido', 'cancelado') DEFAULT 'pendente',
    valor DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_nota_fiscal) REFERENCES notas_fiscais(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id_FK INT DEFAULT NULL,
    qtd_produtos INT NOT NULL,
    unidade_medida ENUM('METROS','CAIXAS','LITROS','KILOGRAMAS','OUTROS','NÃO DEFINIDO') DEFAULT 'NÃO DEFINIDO',
    ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id_FK) REFERENCES produtos(id) ON DELETE CASCADE,
    CONSTRAINT CHECK (qtd_produtos >= 0)
);

CREATE TABLE IF NOT EXISTS saidas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_saida DATETIME NOT NULL,
    id_pedido_FK INT,
    FOREIGN KEY (id_pedido_FK) REFERENCES pedidos(id) ON DELETE SET NULL
);