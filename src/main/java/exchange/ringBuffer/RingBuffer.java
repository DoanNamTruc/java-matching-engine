package exchange.ringBuffer;

import java.util.function.Supplier;

public class RingBuffer<T> {

    private final T[] buffer;
    private final int mask;

    public final Sequence producerSeq = new Sequence();
    public final Sequence consumerSeq = new Sequence();

    public RingBuffer(int size, Supplier<T> factory) {
        if (Integer.bitCount(size) != 1) {
            throw new IllegalArgumentException("size must be power of 2");
        }
        buffer = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            buffer[i] = factory.get();
        }
        mask = size - 1;
    }

    public long next() {
        long next = producerSeq.incrementAndGet();

        // backpressure
        while (next - buffer.length > consumerSeq.get()) {
            Thread.onSpinWait();
        }

        return next;
    }

    public T get(long seq) {
        return buffer[(int) (seq & mask)];
    }

    public void publish(long seq) {
        // memory barrier
    }

    public Sequence consumerSequence() {
        return consumerSeq;
    }
}
