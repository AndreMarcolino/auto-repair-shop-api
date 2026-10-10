package br.com.autorepairshop.controller.dto;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.autorepairshop.domain.shared.DomainException;

@RestControllerAdvice
public class TratamentoExcecoesApi {

    private static final Logger log =LoggerFactory.getLogger(TratamentoExcecoesApi.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroApi> tratarErroInesperado(
            Exception exception) {

        log.error("Erro inesperado ao processar requisição.", exception);

        var erro = new ErroApi("ERRO_INTERNO","Ocorreu um erro interno. Tente novamente mais tarde."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomainException(DomainException ex) {

        List<ErroApi> erros = ex.getErros()
            .stream()
            .map(erro -> new ErroApi(
                erro.getCodigo(),
                erro.getMensagem()
            ))
            .toList();

        return ResponseEntity.badRequest().body(erros);
    }
}
