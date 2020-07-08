package de.hzg.wpi.waltz.magix.connector;

import org.tango.client.rx.RxTango;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public interface ActionExecutor {
    <T> RxTango<T> execute(TangoPayload payload) throws Exception;
}
