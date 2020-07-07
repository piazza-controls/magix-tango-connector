package de.hzg.wpi.waltz.magix.client;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 03.07.2020
 */
public class Magix implements AutoCloseable {
    private final String host;
    private final Client client;
    private final AtomicReference<SseEventSource> sseEventSource = new AtomicReference<>(null);
    private final ConcurrentMap<String, Subject<Message<?>>> channels = new ConcurrentHashMap<>();


    public Magix(String endpoint, Client client) {
        this.host = endpoint;
        this.client = client;
    }

    /**
     * Indefinitely attempts to connect to this endpoint using this client
     */
    public void connect() {
        System.out.println("Connecting...");
        WebTarget target = client.target(UriBuilder.fromPath(String.format("%s/magix/api/subscribe", host)));

        SseEventSource sseEventSource = SseEventSource.target(target)
                .reconnectingEvery(3, TimeUnit.SECONDS)
                .build();

        sseEventSource.register(this::onEvent, this::onError, this::onComplete);
        sseEventSource.open();
        this.sseEventSource.compareAndSet(null, sseEventSource);
    }

    public String getStatus() {
        if (sseEventSource.get().isOpen())//TODO may throw NPE
            return "OPEN";
        else return "PENDING";
    }

    private void onEvent(InboundSseEvent event) {
        try {
            final Message<?> message = event.readData(Message.class, MediaType.APPLICATION_JSON_TYPE);
            String channel = event.getName();
            channels.getOrDefault(channel, PublishSubject.create())
                    .onNext(message);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }

    private void onError(Throwable throwable) {
        System.out.println("onError: " + throwable.getMessage());
        this.sseEventSource.set(null);
        connect();
    }

    private void onComplete() {
        System.out.println("onComplete");
    }

    public <T> void broadcast(String channel, T message) {
        WebTarget target = client.target(UriBuilder.fromUri(String.format("%s/magix/api/broadcast?channel=%s", host, channel)));

        target.request().buildPost(Entity.json(message)).submit();
    }

    public Observable<Message<?>> observe(String channel) {
        Subject<Message<?>> subject = PublishSubject.create();

        Subject<Message<?>> oldSubject = channels.putIfAbsent(channel, subject);

        return Objects.requireNonNullElse(oldSubject, subject);
    }

    @Override
    public void close() throws Exception {
        System.out.println("Closing");
        SseEventSource sse = sseEventSource.get();
        sse.close();//TODO timeout
        sseEventSource.compareAndSet(sse, null);
    }
}
