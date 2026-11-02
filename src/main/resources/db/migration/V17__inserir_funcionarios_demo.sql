
INSERT INTO funcionarios (
    id,
    nome,
    email,
    senha_hash,
    tipo,
    ativo
)
VALUES
(
    gen_random_uuid(),
    'Mecanico',
    'mecanico@auto-repair-shop.com.br',
    '$2a$12$WnjIOkXywLjIf8HGOUTLKusyv9Oh56leRtwPkfDeU05XpdibIMqcW',
    'MECANICO',
    TRUE
),
(
    gen_random_uuid(),
    'Atendente',
    'atendente@auto-repair-shop.com.br',
    '$2a$12$WnjIOkXywLjIf8HGOUTLKusyv9Oh56leRtwPkfDeU05XpdibIMqcW',
    'ATENDENTE',
    TRUE
);
