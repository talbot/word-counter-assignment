package com.example.javainterview.service;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@BenchmarkMode({ Mode.AverageTime, Mode.Throughput })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class WordBatchServiceBenchmark {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Benchmark
    public void baseline(WordBatchServiceState state) {
        for (int i = 0; i < state.words; i++) {
            get();
        }
    }

    @Benchmark
    public void benchmarkAccept(WordBatchServiceState state) {
        for (int i = 0; i < state.words; i++) {
            state.accept(get());
        }
    }

    @Benchmark
    public void benchmarkAcceptAndFlush(WordBatchServiceState state) {
        for (int i = 0; i < state.words; i++) {
            state.accept(get());
        }
        var size = state.get();
        assert size.get() > 0;
    }

    public String get() {
        return apply(RANDOM.nextLong(Integer.MAX_VALUE >> 16));
    }

    public String apply(Long value) {
        if (value % 15 == 0) {
            return "FizzBuzz";
        }
        if (value % 3 == 0) {
            return "Fizz";
        }
        if (value % 5 == 0) {
            return "Buzz";
        }
        if (value % 2 == 0) {
            return "Even_" + value;
        }
        return "Odd_" + value;
    }
}
