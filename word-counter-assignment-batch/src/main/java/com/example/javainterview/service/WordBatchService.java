package com.example.javainterview.service;

import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WordBatchService implements WordBatchConsumer {

    private Map<String, Counter> batch = new HashMap<>();

    @Override
    public void accept(@Nonnull String word) {
        synchronized (this) {
            if (batch.containsKey(word)) {
                long count = batch.get(word).count() + 1L;
                batch.put(word, new Counter(word, count));
            } else {
                batch.put(word, new Counter(word, 1L));
            }
        }
    }

    @Override
    public void flush(@Nonnull Consumer<Collection<Counter>> sink) {
        synchronized (this) {
            sink.accept(batch.values());
            batch = new HashMap<>();
        }
    }
}
