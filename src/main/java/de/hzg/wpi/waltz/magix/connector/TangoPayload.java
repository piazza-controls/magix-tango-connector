package de.hzg.wpi.waltz.magix.connector;

import fr.esrf.Tango.DevError;

import java.util.Collections;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public interface TangoPayload {

    String getHost();

    String getDevice();

    String getName();

    default <T> T getValue() {
        return null;
    }

    default String getAction() {
        return null;
    }

    default long getTimestamp() {
        return System.currentTimeMillis();
    }

    default String getQuality() {
        return "VALID";
    }

    default String getEvent() {
        return null;
    }

    default <T> T getInput() {
        return null;
    }

    default <T> T getOutput() {
        return null;
    }

    default Iterable<? extends DevError> getErrors() {
        return Collections.emptyList();
    }
}
