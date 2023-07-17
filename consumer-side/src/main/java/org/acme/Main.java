package org.acme;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.acme.events.IncomingConsumer;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    IncomingConsumer consumer;

    @Override
    public int run(String... args) {
        System.out.println("Initialize consumers");

        consumer.subscribe();
        consumer.runListener();

        return 0;
    }
}
