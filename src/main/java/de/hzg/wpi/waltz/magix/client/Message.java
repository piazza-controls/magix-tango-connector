package de.hzg.wpi.waltz.magix.client;

/**
 * Compliant with Waltz-Controls RFC-1
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
public class Message<T> {
    public long id;
    public long parent;
    public String origin;
    public String target;
    public String user;
    public String action;
    public Iterable<T> payload;
}
