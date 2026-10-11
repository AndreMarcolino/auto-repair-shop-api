package br.com.autorepairshop.application.servico.criar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.servico.Servico;
import br.com.autorepairshop.domain.servico.ServicoRepository;

@Service
public class CriarServicoService implements CriarServicoUseCase {

    private final ServicoRepository servicoRepository;

    public CriarServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional
    public CriarServicoOutput executar(CriarServicoInput input) {
        Servico servico = Servico.criar(
                input.descricao(),
                input.valorReferencia());

        Servico salvo = servicoRepository.salvar(servico);

        return CriarServicoOutput.fromDomain(salvo);
    }
}
