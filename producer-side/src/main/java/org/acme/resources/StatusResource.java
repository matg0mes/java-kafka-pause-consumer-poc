package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.EventDTO;
import org.acme.dto.StatusDTO;
import org.acme.events.StatusProducer;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/status")
public class StatusResource {

    @Inject
    StatusProducer producer;

    @GET()
    @Path("/pause")
    @Produces(MediaType.APPLICATION_JSON)
    public String pause() {
        StatusDTO dto = new StatusDTO();
        dto.setEventId(UUID.randomUUID().toString());
        dto.setDescription("Pausando....");
        dto.setPaused(true);
        dto.setDateEvent(LocalDateTime.now());

        producer.send(dto);
        return "Kafka paused..";
    }

    @GET()
    @Path("/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public String resume() {
        StatusDTO dto = new StatusDTO();
        dto.setEventId(UUID.randomUUID().toString());
        dto.setDescription("Retomando....");
        dto.setPaused(false);
        dto.setDateEvent(LocalDateTime.now());

        producer.send(dto);

        return "Kafka resumed..";
    }

}
