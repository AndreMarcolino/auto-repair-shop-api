CREATE TABLE orcamento (
    id UUID PRIMARY KEY,
    ordem_servico_id UUID NOT NULL UNIQUE,
    mecanico_id UUID NOT NULL,

    diagnostico TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'EM_ELABORACAO',

    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    enviado_em TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_orcamento_ordem_servico
        FOREIGN KEY (ordem_servico_id)
        REFERENCES ordem_servico (id),

    CONSTRAINT fk_orcamento_mecanico
        FOREIGN KEY (mecanico_id)
        REFERENCES funcionarios (id),

    CONSTRAINT ck_orcamento_status
        CHECK (status IN (
            'EM_ELABORACAO',
            'AGUARDANDO_APROVACAO',
            'APROVADO',
            'REPROVADO'
        ))
);

CREATE INDEX idx_orcamento_mecanico_id
    ON orcamento (mecanico_id);