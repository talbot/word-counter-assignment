# Exercise: Implement a Concurrent WordBatchConsumer
## Context

You are given an interface representing a word-processing batch consumer.
Multiple producer threads submit words for processing, while a separate thread periodically flushes accumulated word counts.

```java
public interface WordBatchConsumer extends Consumer<String> {
    void flush(Consumer<Collection<Counter>> sink);
}
```
A `Counter` class is defined as:
```java
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
```

**Your task is to implement this interface in a thread-safe and performant manner.**

## Requirements

### 1. Functionality

* `accept(String word)` is invoked concurrently by multiple producer threads.
* `flush(Consumer<Collection<Counter>> sink)` is invoked externally from a separate thread (e.g., scheduled executor).
* You must aggregate per-word counts.
* Once flushed, the returned counters contain counts since the previous flush only.

**Example:*
```java
accept("a"); accept("a"); accept("b");
flush(...) → [("a",2),("b",1)]

accept("a"); accept("b"); accept("b"); accept("b");
flush(...) → [("a",1),("b",3)]
```

### 2. Concurrency and Memory Safety

* Must support high concurrency (millions of records).
* No data may be lost between accepts and flushes.
* When flush runs:
  - Implementation may block producer threads, OR
  - Allow concurrent accepts.
* If blocking is used, minimize blocked time.
* After flushing, internal storage may be cleared or replaced.

Correct behavior must be preserved under Java Memory Model (JMM).
Use `volatile`, `synchronized`, atomics, or high-level concurrency constructs as appropriate.

### 3. Design Freedom

You may choose:
* Data structures
* Locking strategy
* Whether to swap internal buffers on flush
* Supporting blocking vs. non-blocking accept
* Atomic or striped counters

You **may not** change the interface.

### 4. Performance Expectations

* Target is Java 8 (for compatibility reasons)
* Must scale for millions of words
* Minimize memory overhead where possible
* Minimize latency impact of flushing

### 5. Testing

Your solution will be validated using:
* JCStress tests (data races, memory consistency)
* A JMH benchmark

You are encouraged to:
* Test functional correctness
* Write microbenchmarks (pending JMH setup)

## Suggested Implementation Topics

To help you design your solution, consider researching:
* `AtomicLong` and `ConcurrentHashMap<>`
* `ConcurrentHashMap.compute(…)`
* Double-buffering / map rotation
* Read-write locks

No single approach is required.

## Example Usage Scenario

Multiple producer threads submit words:
```java
consumer.accept("foo");
consumer.accept("bar");
consumer.accept("foo");
```

Flusher thread runs periodically (e.g., via `ScheduledExecutorService`):
```java
consumer.flush(counters -> counters.forEach(System.out::println));
```

Console might show:
```shell
("foo", 2)
("bar", 1)
```

## Deliverables

1. A class implementing WordBatchConsumer.
2. Optional notes describing your concurrency strategy.
3. Correctness validated by JCStress test.
4. (Later) Performance validated via JMH benchmark.

## Evaluation Criteria
| Category         | Notes                                |
|------------------|--------------------------------------|
| Correctness      | No data loss; correct flush behavior |
| Thread-safety    | Works under heavy concurrency        |
| Performance      | Minimal contention; scalable         |
| JMM compliance   | No illegal races                     |
| Elegance         | Clear & maintainable design          |
| Knowledge depth  | Use of proper concurrency tool       |
