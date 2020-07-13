package de.hzg.wpi.waltz.magix.connector;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 08.07.2020
 */
public interface ActionExecutor {
    TangoPayload execute(TangoPayload payload);
}
