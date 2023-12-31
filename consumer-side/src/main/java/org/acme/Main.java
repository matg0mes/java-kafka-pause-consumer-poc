package org.acme;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.acme.events.IncomingConsumer;
import org.acme.events.StatusConsumer;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    IncomingConsumer incomingConsumer;

    @Inject
    StatusConsumer statusConsumer;

    @Inject
    ManagedExecutor managedExecutor;

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Initialize consumers");

        statusConsumer.subscribe();
        incomingConsumer.subscribe();

        // Multiple Consumers Runners
        managedExecutor.runAsync(() -> statusConsumer.runListener());
        managedExecutor.runAsync(() -> incomingConsumer.runListener());

        // keep the main thread open
        managedExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return 0;
    }

}
