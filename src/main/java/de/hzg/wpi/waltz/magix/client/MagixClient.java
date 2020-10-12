package de.hzg.wpi.waltz.magix.client;

import io.reactivex.rxjava3.core.Observable;

import javax.ws.rs.sse.InboundSseEvent;

public interface MagixClient extends AutoCloseable {
    String defaultChannel = "message";

    void connect();

    <T> void broadcast(String channel, T message);
    default <T> void broadcast(T message) { broadcast(defaultChannel, message); }

    Observable<InboundSseEvent> observe(String channel);
    default Observable<InboundSseEvent> observe() { return observe(defaultChannel); }
}
