package org.acme.events;


import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jdk.jfr.Event;
import org.acme.dto.EventDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.UUID;

@ApplicationScoped
public class IncomingProducer {

    @Inject
    @Channel("event-incoming")
    Emitter<EventDTO> emitter;

    public void send(EventDTO eventDTO) {
        System.out.println("Enviando producao de:: " + eventDTO.getDescription());

        Message<EventDTO> message = Message.of(eventDTO).addMetadata(OutgoingCloudEventMetadata.builder()
                .withExtension("correlationId", UUID.randomUUID())
                .build());

        emitter.send(message);
    }


}
