package com.example.javainterview.service;

import org.junit.jupiter.api.Test;

import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordBatchServiceTest {

    private final WordsGenerator generator = new WordsGenerator();

    @Test
    void testFlush() {
        WordBatchService service = new WordBatchService();
        service.flush(batch -> assertTrue(batch.isEmpty()));
        long counters = 1000L;
        LongStream.range(0, counters).mapToObj(generator::apply).forEach(service);
        service.flush(batch -> {
            assertFalse(batch.isEmpty());
            long count = batch.stream().map(Counter::count).reduce(Long::sum).orElse(0L);
            assertEquals(counters, count);
        });
        service.flush(batch -> assertTrue(batch.isEmpty()));
    }

    @Test
    void testAccept() {
        WordBatchService service = new WordBatchService();
        service.accept(generator.get());
        service.flush(batch -> assertEquals(1, batch.size()));
    }
}