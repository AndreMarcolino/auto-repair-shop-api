package br.com.autorepairshop.infrastructure.eventos;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import br.com.autorepairshop.application.shared.eventos.EventoPublicador;

@Component
public class SpringEventoPublicador implements EventoPublicador {

    private final ApplicationEventPublisher publisher;

    public SpringEventoPublicador(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publicar(Object evento) {
        publisher.publishEvent(evento);
    }
}