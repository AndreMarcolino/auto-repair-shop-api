CREATE TABLE pecas (
    id UUID PRIMARY KEY,
    descricao VARCHAR(200) NOT NULL,
    valor_referencia NUMERIC(12, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_pecas_valor_referencia
        CHECK (valor_referencia >= 0),

    CONSTRAINT ck_pecas_quantidade_estoque
        CHECK (quantidade_estoque >= 0)
);