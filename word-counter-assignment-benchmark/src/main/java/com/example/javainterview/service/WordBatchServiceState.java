package com.example.javainterview.service;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@State(Scope.Group)
public class WordBatchServiceState implements Consumer<String>, Supplier<Integer> {

    @Param({ "1024", "2048", "4096", })
    public int words;

    private WordBatchConsumer service;

    @Setup(Level.Invocation)
    public void setUp() {
        service = new WordBatchService();
    }

    @Override
    public void accept(String word) {
        service.accept(word);
    }

    @Override
    public Integer get() {
        var size = new AtomicInteger();
        service.flush(c -> size.set(c.size()));
        return size.get();
    }
}