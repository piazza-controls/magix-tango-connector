package de.hzg.wpi.waltz.magix.connector;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import fr.esrf.Tango.DevError;
import org.tango.client.ez.proxy.*;
import org.tango.utils.DevFailedUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public class TangoActionExecutors {
    //TODO inject cache via execute method call or constructor
    private static final ThreadLocal<LoadingCache<String, TangoProxy>> CACHE_LOCAL = ThreadLocal.withInitial(() -> CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(CacheLoader.from((url) -> {
                try {
                    return TangoProxies.newDeviceProxyWrapper(url);
                } catch (TangoProxyException e) {
                    throw new UncheckedExecutionException(e);
                }
            })));

    private static LoadingCache<String, TangoProxy> getCache() {
        return CACHE_LOCAL.get();
    }

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
                return new Exec();
            case "pipe":
            case "subscribe":
                return null;
            default:
                throw new IllegalArgumentException("Unsupported action " + action);
        }
    }

    public static class Read implements ActionExecutor {
        @Override
        public TangoPayload execute(TangoPayload payload) {
            String url = String.format("tango://%s/%s", payload.getHost(), payload.getDevice());

            try {
                TangoProxy proxy = getCache().get(url);
                ValueTimeQuality<Object> result = proxy.readAttributeValueTimeQuality(payload.getName());
                return new TangoPayload() {
                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public <T> T getValue() {
                        return (T) result.value;
                    }

                    @Override
                    public long getTimestamp() {
                        return result.time;
                    }

                    @Override
                    public String getQuality() {
                        return result.quality.toString();
                    }
                };
            } catch (ExecutionException | ReadAttributeException | NoSuchAttributeException e) {
                return new TangoPayload() {
                    private long timestamp = System.currentTimeMillis();

                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public long getTimestamp() {
                        return timestamp;
                    }

                    @Override
                    public String getQuality() {
                        return "FAILURE";
                    }

                    @Override
                    public Iterable<? extends DevError> getErrors() {
                        return Arrays.asList(DevFailedUtils.newDevFailed(e).errors);
                    }
                };
            }
        }
    }

    public static class Write implements ActionExecutor {
        @Override
        public TangoPayload execute(TangoPayload payload) {
            String url = String.format("tango://%s/%s", payload.getHost(), payload.getDevice());
            try {
                TangoProxy proxy = getCache().get(url);
                proxy.writeAttribute(payload.getName(), payload.getValue());
                return new TangoPayload() {
                    private long timestamp = System.currentTimeMillis();

                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public <T> T getValue() {
                        return (T) payload.getValue();
                    }

                    @Override
                    public long getTimestamp() {
                        return timestamp;
                    }

                    @Override
                    public String getQuality() {
                        return "PENDING";
                    }
                };
            } catch (ExecutionException | NoSuchAttributeException | WriteAttributeException e) {
                return new TangoPayload() {
                    private long timestamp = System.currentTimeMillis();

                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public long getTimestamp() {
                        return timestamp;
                    }

                    @Override
                    public String getQuality() {
                        return "FAILURE";
                    }

                    @Override
                    public Iterable<? extends DevError> getErrors() {
                        return Arrays.asList(DevFailedUtils.newDevFailed(e).errors);
                    }
                };
            }
        }
    }

    public static class Exec implements ActionExecutor {
        @Override
        public TangoPayload execute(TangoPayload payload) {
            String url = String.format("tango://%s/%s", payload.getHost(), payload.getDevice());
            try {
                TangoProxy proxy = getCache().get(url);
                Object result = (payload.getInput() == null) ?
                        proxy.executeCommand(payload.getName()) :
                        proxy.executeCommand(payload.getName(), payload.getInput());


                return new TangoPayload() {
                    private long timestamp = System.currentTimeMillis();

                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public <T> T getValue() {
                        return (T) result;
                    }

                    @Override
                    public long getTimestamp() {
                        return timestamp;
                    }

                    @Override
                    public String getQuality() {
                        return "VALID";
                    }
                };
            } catch (ExecutionException | ExecuteCommandException | NoSuchCommandException e) {
                return new TangoPayload() {
                    private long timestamp = System.currentTimeMillis();

                    @Override
                    public String getHost() {
                        return payload.getHost();
                    }

                    @Override
                    public String getDevice() {
                        return payload.getDevice();
                    }

                    @Override
                    public String getName() {
                        return payload.getName();
                    }

                    @Override
                    public long getTimestamp() {
                        return timestamp;
                    }

                    @Override
                    public String getQuality() {
                        return "FAILURE";
                    }

                    @Override
                    public Iterable<? extends DevError> getErrors() {
                        return Arrays.asList(DevFailedUtils.newDevFailed(e).errors);
                    }
                };
            }
        }
    }
}
