ALTER TABLE insumos
    DROP CONSTRAINT IF EXISTS ck_pecas_quantidade_estoque,
    DROP COLUMN IF EXISTS quantidade_estoque;