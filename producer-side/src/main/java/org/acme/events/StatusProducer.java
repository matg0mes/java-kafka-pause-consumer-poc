package org.acme.events;


import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.StatusDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.UUID;

@ApplicationScoped
public class StatusProducer {

    @Inject
    @Channel("status-incoming")
    Emitter<StatusDTO> emitter;

    public void send(StatusDTO statusDTO) {
        System.out.println("Enviando estímulo:: " + statusDTO.getDescription());

        Message<StatusDTO> message = Message.of(statusDTO).addMetadata(OutgoingCloudEventMetadata.builder()
                .withExtension("correlationId", UUID.randomUUID())
                .build());

        emitter.send(message);
    }


}
