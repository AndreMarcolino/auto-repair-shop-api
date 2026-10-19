DELETE FROM status_ordem_servico;

INSERT INTO status_ordem_servico (id, codigo, descricao)
VALUES
    (1, 'RECEBIDA', 'Recebida'),
    (2, 'EM_DIAGNOSTICO', 'Em diagnóstico'),
    (3, 'AGUARDANDO_APROVACAO', 'Aguardando aprovação do orçamento'),
    (4, 'AGUARDANDO_PECAS', 'Aguardando peças'),
    (5, 'EM_EXECUCAO', 'Em execução'),
    (6, 'FINALIZADA', 'Finalizada'),
    (7, 'ENTREGUE', 'Entregue');