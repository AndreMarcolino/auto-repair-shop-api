package br.com.autorepairshop.domain.ordemservico;

public enum StatusOrdemServico {

    RECEBIDA((short) 1),
    EM_DIAGNOSTICO((short) 2),
    AGUARDANDO_APROVACAO((short) 3),
    APROVADA((short) 4),
    AGUARDANDO_INSUMOS((short) 5),
    EM_EXECUCAO((short) 6),
    FINALIZADA((short) 7),
    AGUARDANDO_PAGAMENTO((short) 8),
    PAGA((short) 9),
    ENTREGUE((short) 10),
    REJEITADA((short) 11);

    private final Short id;

    StatusOrdemServico(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public static StatusOrdemServico porId(Short id) {
        for (var status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Status de ordem de serviço inválido: " + id
        );
    }
}