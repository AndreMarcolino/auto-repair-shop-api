CREATE TABLE orcamento_item_insumo (
    id UUID PRIMARY KEY,
    orcamento_id UUID NOT NULL,
    insumo_id UUID NOT NULL,

    descricao VARCHAR(200) NOT NULL,
    quantidade NUMERIC(10, 2) NOT NULL,
    valor_unitario NUMERIC(12, 2) NOT NULL,

    CONSTRAINT fk_orcamento_item_insumo_orcamento
        FOREIGN KEY (orcamento_id)
        REFERENCES orcamento (id),

    CONSTRAINT fk_orcamento_item_insumo_insumo
        FOREIGN KEY (insumo_id)
        REFERENCES insumos (id),

    CONSTRAINT ck_orcamento_item_insumo_quantidade
        CHECK (quantidade > 0),

    CONSTRAINT ck_orcamento_item_insumo_valor
        CHECK (valor_unitario >= 0)
);

CREATE INDEX idx_orcamento_item_insumo_orcamento_id
    ON orcamento_item_insumo (orcamento_id);