CREATE TABLE ordem_servico (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,

    status VARCHAR(40) NOT NULL,

    data_abertura TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_inicio_servico TIMESTAMP WITHOUT TIME ZONE,
    data_fim_servico TIMESTAMP WITHOUT TIME ZONE,
    data_pagamento TIMESTAMP WITHOUT TIME ZONE,
    data_entrega TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_ordem_servico_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes (id),

    CONSTRAINT fk_ordem_servico_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos (id),

    CONSTRAINT ck_ordem_servico_status
        CHECK (status IN (
            'RECEBIDA',
            'EM_DIAGNOSTICO',
            'AGUARDANDO_ENVIO_ORCAMENTO',
            'AGUARDANDO_APROVACAO',
            'AGUARDANDO_PECAS',
            'EM_EXECUCAO',
            'FINALIZADA',
            'ENTREGUE'
        ))
);

CREATE INDEX idx_ordem_servico_cliente_id
    ON ordem_servico (cliente_id);

CREATE INDEX idx_ordem_servico_veiculo_id
    ON ordem_servico (veiculo_id);

CREATE INDEX idx_ordem_servico_status
    ON ordem_servico (status);