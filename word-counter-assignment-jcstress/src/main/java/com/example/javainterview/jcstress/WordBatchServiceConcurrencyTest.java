package com.example.javainterview.jcstress;

import com.example.javainterview.service.WordBatchConsumer;
import com.example.javainterview.service.WordBatchService;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.LLL_Result;

import java.util.concurrent.atomic.AtomicLong;

public class WordBatchServiceConcurrencyTest {

    @JCStressTest
    @Outcome(id = "1, 2, 0", expect = Expect.ACCEPTABLE, desc = "Flush after first A and both B")
    @Outcome(id = "2, 1, 0", expect = Expect.ACCEPTABLE, desc = "Flush after both A and first B")
    @Outcome(id = "2, 2, 0", expect = Expect.ACCEPTABLE, desc = "Flush after all accepts")
    @State
    public static class BatchingWordConsumerTest {
        private final AtomicLong flushA = new AtomicLong();
        private final AtomicLong flushB = new AtomicLong();
        private final AtomicLong flushUnknown = new AtomicLong();

        private final WordBatchConsumer service = new WordBatchService();

        @Actor
        void producerA() {
            service.accept("A");
            service.accept("A");
            service.flush(counters -> counters.forEach(c -> {
                if ("A".equals(c.key())) {
                    flushA.getAndAdd(c.count());
                } else if ("B".equals(c.key())) {
                    flushB.getAndAdd(c.count());
                } else {
                    flushUnknown.getAndAdd(c.count());
                }
            }));
        }

        @Actor
        void producerB() {
            service.accept("B");
            service.accept("B");
            service.flush(counters -> counters.forEach(c -> {
                if ("A".equals(c.key())) {
                    flushA.getAndAdd(c.count());
                } else if ("B".equals(c.key())) {
                    flushB.getAndAdd(c.count());
                } else {
                    flushUnknown.getAndAdd(c.count());
                }
            }));
        }

        @Arbiter
        public void arbiter(LLL_Result r) {
            r.r1 = flushA.get();
            r.r2 = flushB.get();
            r.r3 = flushUnknown.get(); // Unknown counters
        }
    }
}
