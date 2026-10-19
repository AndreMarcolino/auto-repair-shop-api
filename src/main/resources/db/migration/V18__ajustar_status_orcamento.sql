ALTER TABLE orcamento
    DROP CONSTRAINT ck_orcamento_status;

ALTER TABLE orcamento
    ADD CONSTRAINT ck_orcamento_status
    CHECK (status IN (
        'EM_ELABORACAO',
        'AGUARDANDO_REVISAO',
        'ENVIADO_AO_CLIENTE',
        'APROVADO',
        'REPROVADO'
    ));