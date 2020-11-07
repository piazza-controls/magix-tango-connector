package de.hzg.wpi.waltz.magix.connector;

import de.hzg.wpi.waltz.magix.client.Magix;
import de.hzg.wpi.waltz.magix.client.Message;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.07.2020
 */
@Path("/connector")
public class MagixTangoConnectorEndpoint {
    public static final String ORIGIN_TANGO = "tango";
    private final Logger logger = LoggerFactory.getLogger(MagixTangoConnectorEndpoint.class);

    private final Magix magix;

    public MagixTangoConnectorEndpoint(Magix magix) {
        this.magix = magix;
        this.magix.observe()
                .observeOn(Schedulers.io())
                .map(inboundSseEvent -> inboundSseEvent.readData(Message.class, MediaType.APPLICATION_JSON_TYPE))
                .filter(message -> ORIGIN_TANGO.equalsIgnoreCase(message.target))
                .subscribe(this::onEvent);//TODO dispose
    }

    @GET
    public Response get() {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Message<Object> execute(Message<Object> message) {
        TangoPayload payload = readMessagePayload(message);
        TangoPayload result = new TangoAction(TangoActionExecutors.newInstance(payload.getAction()), payload).execute();
        logger.debug("Broadcasting response...");
        return Message.builder()
                .setId(System.currentTimeMillis())
                .setParent(message.id)
                .setOrigin(ORIGIN_TANGO)
                .setUser(message.user)
                .setPayload(result)
                .build();
    }


    private void onEvent(Message<?> message) {
        TangoPayload payload = readMessagePayload(message);
        logger.debug("Got message with action {}", payload.getAction());

        TangoPayload result = new TangoAction(TangoActionExecutors.newInstance(payload.getAction()), payload).execute();
        logger.debug("Broadcasting response...");
        magix.broadcast(
                Message.builder()
                        .setId(System.currentTimeMillis())
                        .setParent(message.id)
                        .setOrigin(ORIGIN_TANGO)
                        .setUser(message.user)
                        .setPayload(result)
                        .build());
    }

    private TangoPayload readMessagePayload(Message<?> message) {
        Map<String, Object> rawPayload = (Map<String, Object>) message.payload;
        return  (TangoPayload) Proxy.newProxyInstance(TangoPayload.class.getClassLoader(), new Class[]{TangoPayload.class}, (o, method, objects) -> {
            switch (method.getName()) {
                case "getHost":
                    return rawPayload.get("host");
                case "getDevice":
                    return rawPayload.get("device");
                case "getName":
                    return rawPayload.get("name");
                case "getValue":
                    return rawPayload.get("value");
                case "getInput":
                    return rawPayload.get("input");
                default:
                    throw new UnsupportedOperationException(method.getName() + " is not supported!");
            }
        });
    }
}
