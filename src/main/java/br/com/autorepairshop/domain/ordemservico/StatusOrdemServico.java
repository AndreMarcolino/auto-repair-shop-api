package br.com.autorepairshop.domain.ordemservico;

public enum StatusOrdemServico {
    RECEBIDA((short) 1),
    EM_DIAGNOSTICO((short) 2),
    AGUARDANDO_ENVIO_ORCAMENTO((short) 3),
    AGUARDANDO_APROVACAO((short) 4),
    AGUARDANDO_PECAS((short) 5),
    EM_EXECUCAO((short) 6),
    FINALIZADA((short) 7),
    ENTREGUE((short) 8);

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