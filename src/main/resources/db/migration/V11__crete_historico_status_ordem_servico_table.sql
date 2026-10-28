CREATE TABLE historico_status_ordem_servico (
    id UUID PRIMARY KEY,
    ordem_servico_id UUID NOT NULL,
    status_anterior_id SMALLINT,
    status_novo_id SMALLINT NOT NULL,
    data_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historico_os
        FOREIGN KEY (ordem_servico_id)
        REFERENCES ordem_servico (id),

    CONSTRAINT fk_historico_status_anterior
        FOREIGN KEY (status_anterior_id)
        REFERENCES status_ordem_servico (id),

    CONSTRAINT fk_historico_status_novo
        FOREIGN KEY (status_novo_id)
        REFERENCES status_ordem_servico (id)
);

CREATE INDEX idx_historico_status_os
    ON historico_status_ordem_servico (ordem_servico_id, data_hora);