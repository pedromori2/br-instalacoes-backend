CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','OPERADOR')),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    documento VARCHAR(20),
    email VARCHAR(150),
    telefone VARCHAR(30),
    endereco VARCHAR(255),
    observacoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE itens (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('PRODUTO','SERVICO')),
    preco_unitario NUMERIC(12,2) NOT NULL,
    unidade VARCHAR(20) NOT NULL DEFAULT 'un',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorias_lancamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA')),
    descricao VARCHAR(255),
    UNIQUE (nome, tipo)
);

CREATE TABLE orcamentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    data_orcamento DATE NOT NULL,
    data_validade DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO' CHECK (status IN ('RASCUNHO','ENVIADO','APROVADO','REJEITADO','CANCELADO')),
    observacoes TEXT,
    valor_total NUMERIC(12,2) NOT NULL DEFAULT 0,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orcamento_itens (
    id BIGSERIAL PRIMARY KEY,
    orcamento_id BIGINT NOT NULL REFERENCES orcamentos(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES itens(id),
    descricao_personalizada VARCHAR(255),
    quantidade NUMERIC(12,3) NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL,
    tipo_desconto VARCHAR(12) NOT NULL DEFAULT 'NENHUM' CHECK (tipo_desconto IN ('NENHUM','PERCENTUAL','VALOR_FIXO')),
    valor_desconto NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_total NUMERIC(12,2) NOT NULL
);

CREATE TABLE fechamentos_caixa (
    id BIGSERIAL PRIMARY KEY,
    data_fechamento DATE NOT NULL UNIQUE,
    saldo_inicial NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_entradas NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_saidas NUMERIC(12,2) NOT NULL DEFAULT 0,
    saldo_final_calculado NUMERIC(12,2) NOT NULL DEFAULT 0,
    saldo_final_informado NUMERIC(12,2),
    diferenca NUMERIC(12,2),
    status VARCHAR(10) NOT NULL DEFAULT 'ABERTO' CHECK (status IN ('ABERTO','FECHADO')),
    observacoes TEXT,
    usuario_id BIGINT REFERENCES usuarios(id),
    data_hora_fechamento TIMESTAMP
);

CREATE TABLE lancamentos (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA')),
    categoria_id BIGINT NOT NULL REFERENCES categorias_lancamento(id),
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    data_lancamento DATE NOT NULL,
    forma_pagamento VARCHAR(20) NOT NULL CHECK (forma_pagamento IN ('DINHEIRO','PIX','CARTAO_CREDITO','CARTAO_DEBITO','TRANSFERENCIA','BOLETO')),
    orcamento_id BIGINT REFERENCES orcamentos(id),
    fechamento_caixa_id BIGINT REFERENCES fechamentos_caixa(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lancamentos_data ON lancamentos(data_lancamento);
CREATE INDEX idx_lancamentos_tipo ON lancamentos(tipo);
CREATE INDEX idx_lancamentos_categoria ON lancamentos(categoria_id);
CREATE INDEX idx_lancamentos_fechamento ON lancamentos(fechamento_caixa_id);
CREATE INDEX idx_orcamentos_status ON orcamentos(status);
CREATE INDEX idx_orcamentos_cliente ON orcamentos(cliente_id);
CREATE INDEX idx_orcamento_itens_orcamento ON orcamento_itens(orcamento_id);
