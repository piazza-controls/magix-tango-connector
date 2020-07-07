package de.hzg.wpi.waltz.magix.connector;

import de.hzg.wpi.waltz.magix.client.Magix;
import de.hzg.wpi.waltz.magix.client.Message;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.07.2020
 */
@Path("/connector")
public class MagixTangoConnectorEndpoint {

    private final Magix magix;

    public MagixTangoConnectorEndpoint(Magix magix) {
        this.magix = magix;
        this.magix.observe("tango")
                .subscribe(this::onEvent);//TODO dispose
    }

    @GET
    public Response get() {
        return Response.ok(magix.getStatus()).build();
    }


    private void onEvent(Message<?> message) {
        System.out.println(message);
    }
}
