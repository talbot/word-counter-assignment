package com.example.javainterview.service;

public final class Counter {

    private final String key;
    private final long count;

    public Counter(String key, long count) {
        this.key = key;
        this.count = count;
    }

    public String key() {
        return key;
    }

    public long count() {
        return count;
    }
}
