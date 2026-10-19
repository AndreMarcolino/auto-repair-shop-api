	
CREATE TABLE orcamento_item_servico (
    id UUID PRIMARY KEY,
    orcamento_id UUID NOT NULL,
    servico_id UUID NOT NULL,

    descricao VARCHAR(200) NOT NULL,
    quantidade NUMERIC(10, 2) NOT NULL,
    valor_unitario NUMERIC(12, 2) NOT NULL,

    CONSTRAINT fk_orcamento_item_servico_orcamento
        FOREIGN KEY (orcamento_id)
        REFERENCES orcamento (id),

    CONSTRAINT fk_orcamento_item_servico_servico
        FOREIGN KEY (servico_id)
        REFERENCES servicos (id),

    CONSTRAINT ck_orcamento_item_servico_quantidade
        CHECK (quantidade > 0),

    CONSTRAINT ck_orcamento_item_servico_valor
        CHECK (valor_unitario >= 0)
);

CREATE INDEX idx_orcamento_item_servico_orcamento_id
    ON orcamento_item_servico (orcamento_id);