package com.example.javainterview.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public class WordsGenerator implements Supplier<String>, Function<Long, String> {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public String get() {
        return apply(RANDOM.nextLong(Integer.MAX_VALUE >> 16));
    }

    @Override
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
            return "Even";
        }
        return "Odd";
    }
}
