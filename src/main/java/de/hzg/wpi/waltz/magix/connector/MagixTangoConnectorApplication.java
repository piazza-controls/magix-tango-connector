package de.hzg.wpi.waltz.magix.connector;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.hzg.wpi.waltz.magix.client.Magix;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.07.2020
 */
@ApplicationPath("/api")
public class MagixTangoConnectorApplication extends Application {

    public static final String MAGIX_HOST = "http://localhost:8080";

    public static final String MAINTENANCE = "maintenance";
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("MagixTangoConnectorApplication-heartbeat-%d")
                    .build());
    private final Magix magix;

    {
        Client client = ResteasyClientBuilder.newClient();
        magix = new Magix(MAGIX_HOST, client);
        magix.connect();
        executorService.scheduleAtFixedRate(() -> {
            magix.broadcast(MAINTENANCE, new HeartbeatMessage());
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();


        singletons.add(new MagixTangoConnectorEndpoint(magix));


        return singletons;
    }

    private static class HeartbeatMessage {
        public long id = System.currentTimeMillis();
        public String origin = "magix-tango-connector";
        public String action = "heartbeat";
    }
}
