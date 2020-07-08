package de.hzg.wpi.waltz.magix.connector;

import org.tango.client.rx.RxTango;
import org.tango.client.rx.RxTangoAttribute;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public class TangoActionExecutors {
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
            return new RxTangoAttribute<T>(String.format("tango://%s/%s", payload.getHost(), payload.getDevice()), payload.getName());
        }
    }
}
