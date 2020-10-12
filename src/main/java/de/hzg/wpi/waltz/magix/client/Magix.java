package de.hzg.wpi.waltz.magix.client;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
public class Magix implements MagixClient {
    private final Logger logger = LoggerFactory.getLogger(Magix.class);

    private final String host;
    private final Client client;
    private final AtomicReference<SseEventSource> sseEventSource = new AtomicReference<>(null);
    private final ConcurrentMap<String, Subject<InboundSseEvent>> channels = new ConcurrentHashMap<>();


    public Magix(String endpoint, Client client) {
        this.host = endpoint;
        this.client = client;
    }

    /**
     * Indefinitely attempts to connect to this endpoint using this client
     */
    @Override
    public void connect() {
        logger.debug("Connecting to {}", this.host);
        WebTarget target = client.target(UriBuilder.fromPath(String.format("%s/magix/api/subscribe", host)));

        SseEventSource sseEventSource = SseEventSource.target(target)
                .reconnectingEvery(3, TimeUnit.SECONDS)
                .build();

        sseEventSource.register(this::onEvent, this::onError, this::onComplete);
        sseEventSource.open();
        this.sseEventSource.compareAndSet(null, sseEventSource);
    }

    private void onEvent(InboundSseEvent event) {
        logger.debug("Got message in channel {}", event.getName());
        try {
            String channel = event.getName();
            channels.getOrDefault(channel, PublishSubject.create())
                    .onNext(event);
        } catch (Throwable t) {
            logger.warn("SSE onEvent failed {}. Ignoring...", t.getMessage());
        }
    }

    private void onError(Throwable throwable) {
        logger.warn("SSE onError {}", throwable.getMessage());
        this.sseEventSource.set(null);
        connect();
    }

    private void onComplete() {
        System.out.println("onComplete");
    }

    @Override
    public <T> void broadcast(String channel, T message) {
        WebTarget target = client.target(UriBuilder.fromUri(String.format("%s/magix/api/broadcast?channel=%s", host, channel)));

        target.request().buildPost(Entity.json(message)).submit();
    }

    @Override
    public Observable<InboundSseEvent> observe(String channel) {
        Subject<InboundSseEvent> subject = PublishSubject
                .create();

        Subject<InboundSseEvent> oldSubject = channels.putIfAbsent(channel, subject);

        return Objects.requireNonNullElse(oldSubject, subject);
    }

    @Override
    public void close() throws Exception {
        logger.debug("Closing...");
        SseEventSource sse = sseEventSource.get();
        sse.close();//TODO timeout, NPE
        sseEventSource.compareAndSet(sse, null);
    }
}
