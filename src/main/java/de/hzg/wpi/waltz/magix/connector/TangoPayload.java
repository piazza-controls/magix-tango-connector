package de.hzg.wpi.waltz.magix.connector;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public interface TangoPayload {
    public String getHost();

    public String getDevice();

    public String getName();
}
