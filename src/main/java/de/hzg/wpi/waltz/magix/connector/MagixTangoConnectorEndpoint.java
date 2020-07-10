package de.hzg.wpi.waltz.magix.connector;

import de.hzg.wpi.waltz.magix.client.Magix;
import de.hzg.wpi.waltz.magix.client.Message;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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


    private void onEvent(Message<?> message) {
        logger.info("Got message with action {}", message.action);

        Map<String, Object> payload = (Map<String, Object>) message.payload.iterator().next();//TODO

        new TangoAction(
                TangoActionExecutors.newInstance(message.action),
                (TangoPayload) Proxy.newProxyInstance(TangoPayload.class.getClassLoader(), new Class[]{TangoPayload.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        switch (method.getName()) {
                            case "getHost":
                                return payload.get("host");
                            case "getDevice":
                                return payload.get("device");
                            case "getName":
                                return payload.get("name");
                            case "getValue":
                                return payload.get("value");
                            case "getInput":
                                return payload.get("input");
                            default:
                                throw new UnsupportedOperationException(method.getName() + " is not supported!");
                        }
                    }
                })
        )
                .observe()
                .subscribe(response -> {
                    logger.info("Broadcasting response...");
                    magix.broadcast(
                            Message.builder()
                                    .setId(System.currentTimeMillis())
                                    .setParent(message.id)
                                    .setOrigin(ORIGIN_TANGO)
                                    .setUser(message.user)
                                    .addPayload(response)
                                    .build());
                });
    }
}
