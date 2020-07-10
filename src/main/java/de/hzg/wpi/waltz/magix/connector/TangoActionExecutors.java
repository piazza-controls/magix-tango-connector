package de.hzg.wpi.waltz.magix.connector;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.tango.client.ez.proxy.TangoProxies;
import org.tango.client.ez.proxy.TangoProxy;
import org.tango.client.ez.proxy.TangoProxyException;
import org.tango.client.rx.RxTango;
import org.tango.client.rx.RxTangoAttribute;
import org.tango.client.rx.RxTangoAttributeWrite;

import java.util.concurrent.TimeUnit;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public class TangoActionExecutors {
    private static final LoadingCache<String, TangoProxy> CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(CacheLoader.from((url) -> {
                try {
                    return TangoProxies.newDeviceProxyWrapper(url);
                } catch (TangoProxyException e) {
                    throw new UncheckedExecutionException(e);
                }
            }));

    private TangoActionExecutors() {
    }

    /**
     * @param action
     * @return
     * @throws IllegalArgumentException
     */
    public static ActionExecutor newInstance(String action) {
        switch (action) {
            case "read":
                return new Read();
            case "write":
                return new Write();
            case "exec":
            case "pipe":
            case "subscribe":
                return null;
            default:
                throw new IllegalArgumentException("Unsupported action " + action);
        }
    }

    public static class Read implements ActionExecutor {
        @Override
        public <T> RxTango<T> execute(TangoPayload payload) throws Exception {
            String url = String.format("tango://%s/%s", payload.getHost(), payload.getDevice());
            TangoProxy proxy = CACHE.get(url);
            return new RxTangoAttribute<T>(proxy, payload.getName());
        }
    }

    public static class Write implements ActionExecutor {
        @Override
        public <T> RxTango<T> execute(TangoPayload payload) throws Exception {
            String url = String.format("tango://%s/%s", payload.getHost(), payload.getDevice());
            TangoProxy proxy = CACHE.get(url);

            return new RxTangoAttributeWrite<T>(proxy, payload.getName(), payload.getValue());
        }
    }
}
