CREATE TABLE ordem_servico_insumo_pendente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem_servico_id UUID NOT NULL,
    insumo_id UUID NOT NULL,
    quantidade_necessaria NUMERIC(12, 3) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_os_insumo_pendente_ordem_servico
        FOREIGN KEY (ordem_servico_id)
        REFERENCES ordem_servico (id),

    CONSTRAINT fk_os_insumo_pendente_insumo
        FOREIGN KEY (insumo_id)
        REFERENCES insumos (id),

    CONSTRAINT uq_os_insumo_pendente
        UNIQUE (ordem_servico_id, insumo_id),

    CONSTRAINT ck_os_insumo_pendente_quantidade
        CHECK (quantidade_necessaria > 0)
);

CREATE INDEX ix_os_insumo_pendente_insumo
    ON ordem_servico_insumo_pendente (insumo_id);