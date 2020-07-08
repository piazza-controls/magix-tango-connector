package de.hzg.wpi.waltz.magix.connector;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public interface TangoPayload {
    String getHost();

    String getDevice();

    String getName();

    <T> T getValue();

    long getTimestamp();

    String getQuality();

    String getEvent();

    <T> T getInput();
}
