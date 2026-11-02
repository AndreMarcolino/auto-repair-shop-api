ALTER TABLE funcionarios
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN senha_hash VARCHAR(255);

CREATE UNIQUE INDEX uk_funcionarios_email
    ON funcionarios (LOWER(email))
    WHERE email IS NOT NULL;