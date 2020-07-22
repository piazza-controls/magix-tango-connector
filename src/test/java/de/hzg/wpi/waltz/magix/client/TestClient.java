package de.hzg.wpi.waltz.magix.client;

import de.hzg.wpi.waltz.magix.connector.TangoPayload;
import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 09.07.2020
 */
public class TestClient {
    private static final int NUMBER_OF_CLIENTS = 64;
    private static final long FIFTEEN_SECONDS = 15_000;

    @Test
    @Ignore
    public void testMagix() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);

        final AtomicLong counter = new AtomicLong(0L);
        final AtomicLong errors = new AtomicLong(0L);

        final AtomicBoolean finish = new AtomicBoolean(false);

        Client client = ClientBuilder.newClient();

        Magix magix = new Magix("http://localhost:8080", client);
        magix.connect();

        for (int i = 0; i < NUMBER_OF_CLIENTS; ++i) {
            executorService.submit(() -> {
                Disposable disposable = null;
                while (!finish.get()) {
                    final long id = System.currentTimeMillis();
                    magix.broadcast(
                            Message.builder()
                                    .setId(id)
                                    .setAction("write")
                                    .setTarget("tango")
                                    .addPayload(new TangoPayload() {
                                        @Override
                                        public String getHost() {
                                            return "hzgxenvtest:10000";
                                        }

                                        @Override
                                        public String getDevice() {
                                            return "development/benchmark/0";
                                        }

                                        @Override
                                        public String getName() {
                                            return "BenchmarkScalarAttribute";
                                        }

                                        @Override
                                        public Double getValue() {
                                            return 3.14;
                                        }
                                    })
                                    .build()
                    );
                }
                disposable.dispose();
            });
        }

        Thread.sleep(FIFTEEN_SECONDS);
        finish.set(true);

        executorService.shutdownNow();

        System.out.println(String.format("Total writes count: %d", counter.get()));
        System.out.println(String.format("Total errors count: %d", errors.get()));
    }


    @Test
    @Ignore
    public void testDirectHttp() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);

        final AtomicLong counter = new AtomicLong(0L);
        final AtomicLong errors = new AtomicLong(0L);

        final AtomicBoolean finish = new AtomicBoolean(false);

        for (int i = 0; i < NUMBER_OF_CLIENTS; ++i) {
            executorService.submit(() -> {
                Client client = ClientBuilder.newClient();

                while (!finish.get()) {
                    final long id = System.currentTimeMillis();
                    client.target("http://localhost:8080/tango-connector/api/connector")
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .buildPost(Entity.json(Message.builder()
                                    .setId(id)
                                    .setAction("write")
                                    .setTarget("tango")
                                    .addPayload(new TangoPayload() {
                                        @Override
                                        public String getHost() {
                                            return "hzgxenvtest:10000";
                                        }

                                        @Override
                                        public String getDevice() {
                                            return "development/benchmark/0";
                                        }

                                        @Override
                                        public String getName() {
                                            return "BenchmarkScalarAttribute";
                                        }

                                        @Override
                                        public Double getValue() {
                                            return 3.14;
                                        }
                                    })
                                    .build()))
                            .submit();
                }
            });
        }

        Thread.sleep(FIFTEEN_SECONDS);
        finish.set(true);

        executorService.shutdownNow();

        System.out.println(String.format("Total writes count: %d", counter.get()));
        System.out.println(String.format("Total errors count: %d", errors.get()));
    }

}
