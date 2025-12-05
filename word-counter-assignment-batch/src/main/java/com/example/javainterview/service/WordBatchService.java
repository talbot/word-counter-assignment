package com.example.javainterview.service;

import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WordBatchService implements WordBatchConsumer {

    private final AtomicReference<Map<String, Long>> batch = new AtomicReference<>(new ConcurrentHashMap<>());

    @Override
    public void accept(@Nonnull String word) {
        batch.get().compute(word, (k, v) -> v == null ? 1L : ++v);
    }

    @Override
    public void flush(@Nonnull Consumer<Collection<Counter>> sink) {
        sink.accept(batch.getAndSet(new ConcurrentHashMap<>())
                            .entrySet()
                            .stream()
                            .map(e -> new Counter(e.getKey(), e.getValue()))
                            .toList());
    }
}
