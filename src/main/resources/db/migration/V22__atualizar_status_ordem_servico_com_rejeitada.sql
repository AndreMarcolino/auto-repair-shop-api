DELETE FROM status_ordem_servico;

INSERT INTO status_ordem_servico (id, codigo, descricao)
VALUES
    (1, 'RECEBIDA', 'Recebida'),
    (2, 'EM_DIAGNOSTICO', 'Em diagnóstico'),
    (3, 'AGUARDANDO_APROVACAO', 'Aguardando aprovação do orçamento'),
    (4, 'APROVADA', 'Orçamento aprovado pelo cliente'),
    (5, 'AGUARDANDO_PECAS', 'Aguardando peças'),
    (6, 'EM_EXECUCAO', 'Em execução'),
    (7, 'FINALIZADA', 'Finalizada'),
    (8, 'ENTREGUE', 'Entregue'),
    (9, 'REJEITADA', 'Rejeitada');