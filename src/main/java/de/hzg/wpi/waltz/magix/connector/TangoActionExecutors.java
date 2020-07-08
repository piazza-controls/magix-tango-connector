package de.hzg.wpi.waltz.magix.connector;

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
    ActionExecutor newInstance(String action) {
        switch (action) {
            case "read":

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
        public TangoPayload execute(TangoPayload payload) throws Exception {

        }
    }
}
