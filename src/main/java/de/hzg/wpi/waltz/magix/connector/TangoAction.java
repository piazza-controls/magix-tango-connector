package de.hzg.wpi.waltz.magix.connector;

import fr.esrf.Tango.DevError;
import io.reactivex.rxjava3.core.Observable;
import org.tango.utils.DevFailedUtils;

import java.util.Arrays;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public class TangoAction {
    private final ActionExecutor executor;
    private final TangoPayload payload;

    public TangoAction(ActionExecutor executor, TangoPayload payload) {
        this.executor = executor;
        this.payload = payload;
    }

    Observable<TangoPayload> observe() {
        try {
            return Observable
                    .fromPublisher(executor.execute(payload))
                    .map(o -> new TangoPayload() {
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
                            return (T) o;
                        }

                        @Override
                        public long getTimestamp() {
                            return timestamp;
                        }

                        @Override
                        public String getQuality() {
                            return "VALID";
                        }
                    });
        } catch (Exception e) {
            return Observable.just(new TangoPayload() {
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
            });

        }
    }
}
