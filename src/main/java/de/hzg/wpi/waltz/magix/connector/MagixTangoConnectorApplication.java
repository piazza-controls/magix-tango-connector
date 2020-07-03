package de.hzg.wpi.waltz.magix.connector;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 02.07.2020
 */
@ApplicationPath("/api")
public class MagixTangoConnectorApplication extends Application {

    public static final String MAGIX_HOST = "http://localhost:8080";

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();


        singletons.add(new MagixTangoConnectorEndpoint());


        return singletons;
    }
}
