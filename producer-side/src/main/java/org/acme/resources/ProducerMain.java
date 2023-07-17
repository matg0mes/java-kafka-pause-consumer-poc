package org.acme.resources;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.acme.dto.EventDTO;
import org.acme.events.IncomingProducer;

import java.util.UUID;

@QuarkusMain
public class ProducerMain implements QuarkusApplication {

    @Inject
    IncomingProducer incomingProducer;

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Iniciando producao de eventos no topico event-incoming, intervalo de 1 segundo");

        EventDTO eventDTO = new EventDTO();
        Integer i = 0;
        while(true) {
            eventDTO.setEventId(UUID.randomUUID().toString());
            eventDTO.setDescription(String.format("Teste %d produzido", i++));

            incomingProducer.send(eventDTO);
            System.out.println(String.format("Evento Produzido - EventId: %s | Description: %s", eventDTO.getEventId(), eventDTO.getDescription()));

            Thread.sleep(1000L);
        }
    }
}
