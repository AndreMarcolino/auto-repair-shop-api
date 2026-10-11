package br.com.autorepairshop.application.peca.criar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.autorepairshop.domain.peca.Peca;
import br.com.autorepairshop.domain.peca.PecaRepository;

@Service
public class CriarPecaService implements CriarPecaUseCase {

    private final PecaRepository pecaRepository;

    public CriarPecaService(PecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    @Override
    @Transactional
    public CriarPecaOutput executar(CriarPecaInput input) {
        Peca peca = Peca.criar(
                input.descricao(),
                input.valorReferencia(),
                input.quantidadeEstoque());

        Peca salva = pecaRepository.salvar(peca);

        return CriarPecaOutput.fromDomain(salva);
    }
}
