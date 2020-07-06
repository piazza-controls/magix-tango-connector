package de.hzg.wpi.waltz.magix.connector;

import de.hzg.wpi.waltz.magix.client.Magix;

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
        this.magix.observe("message", this::onEvent);
    }

    @GET
    public Response get() {
        return Response.ok(magix.getStatus()).build();
    }


    private void onEvent(String t) {
        System.out.println(t);
    }
}
