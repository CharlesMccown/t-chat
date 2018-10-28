package reactor.intro;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

public class FluxTest {
    @Test
    void firstFlux() {
        Flux.just("A", "B", "C")
                .log()
                .subscribe();
    }
    @Test
    void fluxFromIterable() {
        Flux.fromIterable(Arrays.asList("A", "B", "C"))
                .log()
                .subscribe();
    }
    @Test
    void fluxFromRage() {
        Flux.range(10, 5)
                .log()
                .subscribe();
    }
    @Test
    void fluxFromInterval() throws Exception {
        Flux.interval(Duration.ofMillis(100))
                .log()
                .take(2)
                .subscribe();
        Thread.sleep(1000);
    }
    @Test
    void fluxRequest() {
        Flux.range(1, 5)
                .log()
                .subscribe(null,
                        null,
                        null,
                        s -> s.request(3));
    }
    @Test
    void fluxCustomerSubscriber() {
        Flux.range(1, 10)
                .log()
                .subscribe(new BaseSubscriber<Integer>() {
                    int elementsToProcess = 3;
                    int counter = 0;

                    public void hookOnSubscribe(Subscription subscription){
                        System.out.println("Subscribed!");
                        request(elementsToProcess);
                    }

                    public void hookOnNext(Integer value) {
                        Random r = new Random();
                        elementsToProcess = r.ints(1, 4)
                            .findFirst().getAsInt();
                        request(elementsToProcess);
                    }
                });
    }
}
