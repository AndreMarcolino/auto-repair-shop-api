-- Sequence para gerar o número público da OS
CREATE SEQUENCE seq_numero_ordem_servico
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


ALTER TABLE ordem_servico
    ADD COLUMN numero VARCHAR(20);


ALTER TABLE ordem_servico
    ADD COLUMN codigo_acesso_hash VARCHAR(255);


CREATE UNIQUE INDEX uk_ordem_servico_numero
    ON ordem_servico (numero);