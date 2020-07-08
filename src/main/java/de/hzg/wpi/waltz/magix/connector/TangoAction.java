package de.hzg.wpi.waltz.magix.connector;

import io.reactivex.rxjava3.core.Observable;

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
        return Observable.fromPublisher(executor.execute(payload)).map();
    }
}
