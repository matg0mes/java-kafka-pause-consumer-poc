package org.acme.resources;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.events.IncomingConsumer;

@Path("/incoming")
@ApplicationScoped
public class IncomingResource {

    @Inject
    IncomingConsumer consumer;

    @GET()
    @Path("/pause")
    @Produces(MediaType.APPLICATION_JSON)
    public String pause() {
        consumer.pause();
        return "Kafka paused..";
    }

    @GET()
    @Path("/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public String resume() {
        consumer.resume();
        return "Kafka resumed..";
    }

}
