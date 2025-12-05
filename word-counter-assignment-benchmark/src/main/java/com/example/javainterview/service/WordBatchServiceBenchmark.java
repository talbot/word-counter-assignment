package com.example.javainterview.service;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 2)
@BenchmarkMode({ Mode.Throughput, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class WordBatchServiceBenchmark {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final String GROUP_NAME = "ConcurrentWordBatch";
    private static final String GROUP_NAME_SINGLE_SHOT = "ConcurrentWordBatchSingleShot";
    private static final int WRITE_THREAD_COUNT = 4;
    private static final int READ_THREAD_COUNT = 2;
    private static final int BOUND = Integer.MAX_VALUE >> 16;

    @Benchmark
    @Group(GROUP_NAME)
    @GroupThreads(WRITE_THREAD_COUNT)
    public void baseline(WordBatchServiceState state, Blackhole blackhole) {
        for (int i = 0; i < state.words; i++) {
            blackhole.consume(apply(i));
        }
    }

    @Benchmark
    @Group(GROUP_NAME)
    @GroupThreads(WRITE_THREAD_COUNT)
    public void benchmarkAccept(WordBatchServiceState state) {
        for (int i = 0; i < state.words; i++) {
            var word = apply(i);
            state.accept(word);
        }
    }

    @Benchmark
    @Group(GROUP_NAME)
    @GroupThreads(READ_THREAD_COUNT)
    public void benchmarkFlush(WordBatchServiceState state, Blackhole blackhole) {
        blackhole.consume(state.get().get());
    }

    @Benchmark
    @Group(GROUP_NAME_SINGLE_SHOT)
    @GroupThreads(WRITE_THREAD_COUNT)
    public void baselineSingleShot(Blackhole blackhole) {
        blackhole.consume(get());
    }

    @Benchmark
    @Group(GROUP_NAME_SINGLE_SHOT)
    @GroupThreads(WRITE_THREAD_COUNT)
    public void benchmarkAcceptSingleShot(WordBatchServiceState state) {
        var word = get();
        state.accept(word);
    }

    @Benchmark
    @Group(GROUP_NAME_SINGLE_SHOT)
    @GroupThreads(READ_THREAD_COUNT)
    public void benchmarkFlushSingleShot(WordBatchServiceState state, Blackhole blackhole) {
        blackhole.consume(state.get().get());
    }

    public String get() {
        return apply(RANDOM.nextInt(BOUND));
    }

    public String apply(Integer value) {
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
