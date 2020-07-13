package de.hzg.wpi.waltz.magix.connector;

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

    TangoPayload execute() {
        return executor.execute(payload);
    }
}
