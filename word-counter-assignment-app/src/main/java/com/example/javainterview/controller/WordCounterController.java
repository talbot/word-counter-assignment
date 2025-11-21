package com.example.javainterview.controller;

import com.example.javainterview.service.WordBatchConsumer;
import com.example.javainterview.service.WordBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class WordCounterController {

    private static final Logger log = LoggerFactory.getLogger(WordCounterController.class);

    private final WordBatchConsumer consumer = new WordBatchService();

    private final Map<String, Long> words = new ConcurrentHashMap<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public WordCounterController() {
        executor.scheduleAtFixedRate(() -> consumer.flush(c -> log.info("Flush collected words: size={}", c.size())), 1L, 10L, TimeUnit.SECONDS);
    }

    @PostMapping("/{word}")
    public ResponseEntity<Long> accept(@PathVariable String word) {
        consumer.accept(word);
        var count = words.compute(word, (key, value) -> value == null ? 1L : ++value);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{words}")
    public ResponseEntity<Long> get(@PathVariable String word) {
        return ResponseEntity.ok(words.getOrDefault(word, 0L));
    }
}
