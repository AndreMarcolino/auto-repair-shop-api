CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    descricao VARCHAR(200) NOT NULL,
    valor_referencia NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_servicos_valor_referencia
        CHECK (valor_referencia >= 0)
);