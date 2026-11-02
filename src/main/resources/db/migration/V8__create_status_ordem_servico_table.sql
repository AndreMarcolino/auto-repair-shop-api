CREATE TABLE status_ordem_servico (
    id SMALLINT PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    descricao VARCHAR(100) NOT NULL
);