ALTER TABLE ordem_servico
    ADD COLUMN status_id SMALLINT;

UPDATE ordem_servico os
SET status_id = s.id
FROM status_ordem_servico s
WHERE s.codigo = os.status;

ALTER TABLE ordem_servico
    ALTER COLUMN status_id SET NOT NULL;

ALTER TABLE ordem_servico
    ADD CONSTRAINT fk_ordem_servico_status
    FOREIGN KEY (status_id)
    REFERENCES status_ordem_servico (id);

ALTER TABLE ordem_servico
    DROP CONSTRAINT ck_ordem_servico_status;

ALTER TABLE ordem_servico
    DROP COLUMN status;