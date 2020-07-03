package de.hzg.wpi.waltz.magix.connector;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.07.2020
 */
@Path("/connector")
public class MagixTangoConnectorEndpoint {

    private final SseEventSource sseEventSource;

    {
        Client client = ResteasyClientBuilder
                .newBuilder().connectTimeout(0, TimeUnit.DAYS)
                .build();
        WebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/magix/api/subscribe"));//TODO inject path from properties
        sseEventSource = SseEventSource.target(target)
                .reconnectingEvery(3, TimeUnit.SECONDS)
                .build();

        sseEventSource.register(this::onEvent, this::onError, this::onComplete);

        sseEventSource.open();
    }


    @GET
    public Response get() {
        return Response.ok(sseEventSource.isOpen()).build();
    }


    private void onError(Throwable t) {
        t.printStackTrace();
        System.err.println(t.getMessage());
    }

    private void onEvent(InboundSseEvent event) {
        System.out.println(event.readData());
    }

    private void onComplete() {
        System.out.println("onComplete");
    }
}
