package br.com.autorepairshop.controller.shared.dto;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.autorepairshop.domain.shared.DomainException;
import jakarta.validation.ConstraintViolationException;

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

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erros);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApi> tratarValidacaoBody(
            MethodArgumentNotValidException exception) {

        List<String> erros = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ErroApi("VALIDACAO_INVALIDA",
                		"Existem campos inválidos na requisição. Erros: " + String.join(", ", erros)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroApi> tratarConstraintViolation(
            ConstraintViolationException exception) {

        List<String> erros = exception.getConstraintViolations()
                .stream()
                .map(erro -> erro.getPropertyPath() + ": " + erro.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ErroApi("VALIDACAO_INVALIDA",
                		"Existem campos inválidos na requisição. Erros: " + String.join(", ", erros)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroApi> tratarTipoInvalido(
            MethodArgumentTypeMismatchException exception) {

        return badRequest("PARAMETRO_INVALIDO",
                "O parâmetro '" + exception.getName()
                        + "' possui um valor ou formato inválido.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroApi> tratarParametroAusente(
            MissingServletRequestParameterException exception) {

        return badRequest("PARAMETRO_OBRIGATORIO",
                "O parâmetro '" + exception.getParameterName()
                        + "' é obrigatório.");
    }
    
    private ResponseEntity<ErroApi> badRequest(
            String codigo, String mensagem) {

    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroApi(codigo.toString(), mensagem));
    }
}
