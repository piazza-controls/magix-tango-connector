package de.hzg.wpi.waltz.magix.connector;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public class TangoWriteResponsePayload<T> extends TangoWritePayload<T> {
    public String quality;
    public long timestamp;
}
