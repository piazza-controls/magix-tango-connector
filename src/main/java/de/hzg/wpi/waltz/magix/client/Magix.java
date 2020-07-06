package de.hzg.wpi.waltz.magix.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 03.07.2020
 */
public class Magix implements AutoCloseable {
    private final String host;
    private final Client client;
    private final AtomicReference<SseEventSource> sseEventSource = new AtomicReference<>(null);


    private final Object lock = new Object();

    public Magix(String endpoint, Client client) {
        this.host = endpoint;
        this.client = client;
    }

    /**
     * Indefinitely attempts to connect to this endpoint using this client
     */
    public void connect() {
        WebTarget target = client.target(UriBuilder.fromPath(String.format("%s/magix/api/subscribe", host)));
        SseEventSource sseEventSource;
        do {
            sseEventSource = SseEventSource.target(target)
                    .reconnectingEvery(3, TimeUnit.SECONDS)
                    .build();

            sseEventSource.register(this::onEvent, this::onError, this::onComplete);

            sseEventSource.open();
        } while (!sseEventSource.isOpen());
        this.sseEventSource.compareAndSet(null, sseEventSource);
    }

    public String getStatus() {
        if (sseEventSource.get().isOpen())//TODO may throw NPE
            return "OPEN";
        else return "PENDING";
    }

    private void onEvent(InboundSseEvent event) {
        System.out.println(event.readData());
    }

    private void onError(Throwable throwable) {
        System.err.println(throwable.getMessage());
    }

    private void onComplete() {
        System.out.println("onComplete");
    }

    public <T> void broadcast(String channel, T message) {
        WebTarget target = client.target(UriBuilder.fromUri(String.format("%s/magix/api/broadcast?channel=%s", host, channel)));

        target.request().buildPost(Entity.json(message)).submit();
    }

    public <T> void observe(String channel, Consumer<T> consumer) {

    }

    @Override
    public void close() throws Exception {
        SseEventSource sse = sseEventSource.get();
        sse.close();//TODO timeout
        sseEventSource.compareAndSet(sse, null);
    }
}
