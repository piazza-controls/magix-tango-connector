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
    public T payload;

    public static <T> Message.Builder<T> builder() {
        return new Builder<>() {
            private Message<T> message = new Message<>();

            @Override
            public Message<T> build() {
                return message;
            }

            @Override
            public Builder<T> setId(long id) {
                message.id = id;
                return this;
            }

            @Override
            public Builder<T> setParent(long parent) {
                message.parent = parent;
                return this;
            }

            @Override
            public Builder<T> setOrigin(String origin) {
                message.origin = origin;
                return this;
            }

            @Override
            public Builder<T> setTarget(String target) {
                message.target = target;
                return this;
            }

            @Override
            public Builder<T> setUser(String user) {
                message.user = user;
                return this;
            }

            @Override
            public Builder<T> setPayload(T payload) {
                message.payload = payload;
                return this;
            }
        };
    }

    public static interface Builder<T> {
        Message<T> build();

        Builder<T> setId(long id);

        Builder<T> setParent(long parent);

        Builder<T> setOrigin(String origin);

        Builder<T> setTarget(String target);

        Builder<T> setUser(String user);

        Builder<T> setPayload(T payload);
    }
}
