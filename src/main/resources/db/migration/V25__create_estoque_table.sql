CREATE TABLE estoque (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    insumo_id UUID NOT NULL UNIQUE
        REFERENCES insumos(id),
    quantidade_disponivel NUMERIC(12, 3) NOT NULL DEFAULT 0,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_estoque_quantidade
        CHECK (quantidade_disponivel >= 0)
);