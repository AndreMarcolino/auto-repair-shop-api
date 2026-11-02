CREATE TABLE clientes (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,

    cpf_cnpj_tipo_pessoa SMALLINT NOT NULL,
    cpf_cnpj VARCHAR(14) NOT NULL,

    telefone VARCHAR(20),
    email VARCHAR(254),

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT ck_clientes_cpf_cnpj_tipo_pessoa
        CHECK (cpf_cnpj_tipo_pessoa IN (1, 2)),

    CONSTRAINT ck_clientes_cpf_cnpj_tamanho
        CHECK (
            (
                cpf_cnpj_tipo_pessoa = 1
                AND LENGTH(cpf_cnpj) = 11
            )
            OR
            (
                cpf_cnpj_tipo_pessoa = 2
                AND LENGTH(cpf_cnpj) = 14
            )
        )
);

-- Índice para consultas por nome
CREATE INDEX ix_clientes_nome
    ON clientes (nome);

-- Índice para listagem de clientes ativos
CREATE INDEX ix_clientes_ativos
    ON clientes (ativo);

-- Índice para localizar clientes pelo CPF/CNPJ
CREATE INDEX ix_clientes_cpf_cnpj
    ON clientes (cpf_cnpj_tipo_pessoa, cpf_cnpj);