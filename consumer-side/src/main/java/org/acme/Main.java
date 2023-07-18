package org.acme;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.acme.events.IncomingConsumer;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    IncomingConsumer incomingConsumer;

    @Override
    public int run(String... args) {
        System.out.println("Initialize consumers");

        incomingConsumer.subscribe();
        incomingConsumer.runListener();

        return 0;
    }
}
