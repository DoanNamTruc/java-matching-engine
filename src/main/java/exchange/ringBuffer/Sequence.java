package exchange.ringBuffer;

import java.util.concurrent.atomic.AtomicLong;

public final class Sequence {
    private final AtomicLong value = new AtomicLong(-1);

    public long get() {
        return value.get();
    }

    public void set(long v) {
        value.set(v);
    }

    public long incrementAndGet() {
        return value.incrementAndGet();
    }
}
