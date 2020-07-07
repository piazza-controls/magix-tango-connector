package de.hzg.wpi.waltz.magix.connector;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public class TangoExecutePayload<IN, OUT> extends TangoPayload {
    public IN input;
    public OUT output;
}
