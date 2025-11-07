package com.example.javainterview.service;

import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.function.Consumer;

public class WordBatchService implements WordBatchConsumer {

    @Override
    public void accept(@Nonnull String word) {
    }

    @Override
    public void flush(@Nonnull Consumer<Collection<Counter>> sink) {
    }
}
