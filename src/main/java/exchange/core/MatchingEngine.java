package exchange.core;

import exchange.ringBuffer.RingBuffer;
import exchange.snapshot.SnapshotService;
import exchange.waiting.SleepWaitStrategy;
import exchange.waiting.WaitStrategy;

import java.io.IOException;
import java.nio.file.Paths;

public final class MatchingEngine implements Runnable {

    private final RingBuffer<OrderEvent> ring;
    private final OffHeapOrderBook book;
    private final WaitStrategy waitStrategy = new SleepWaitStrategy();

    public MatchingEngine(RingBuffer<OrderEvent> ring, OffHeapOrderBook orderBook) {
        this.ring = ring;
        this.book = orderBook;
    }

    @Override
    public void run() {
        long next = ring.consumerSequence().get() + 1;
        long start = System.nanoTime();
        long timestop = start;
        //TODO tune batch size for better throughput
        int batchSize = 1;
        long processedTotal = 0;
        while (true) {
            int processed = 0;

            while (next <= ring.producerSeq.get() && processed < batchSize) {
                OrderEvent e = ring.get(next);
                match(e);
                ring.consumerSequence().set(next);
                next++;
                processed++;
                processedTotal++;
                if (processedTotal % 100_000 == 0) {

                    // ~1M orders
                    System.out.println("Processed :" + processedTotal + " in " + (System.nanoTime() - timestop) / 1_000_000 + " ms");
                    timestop = System.nanoTime();
                    try {
                        SnapshotService.save(Paths.get("orderbook.snapshot"), this.book);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (processed == 0) {
                waitStrategy.idle();
            }
        }

    }

    private void match(OrderEvent e) {
        if (e.side == 0) {
            matchBuy(e);
        } else {
            matchSell(e);
        }
    }

    private void matchBuy(OrderEvent e) {
        long qty = e.qty;

        while (qty > 0 && book.bestAskPrice() <= e.price) {
            int ask = book.bestAskHead();
            long traded = Math.min(qty, book.qty(ask));

            qty -= traded;
            book.setQty(ask, book.qty(ask) - traded);
            if (book.qty(ask) == 0) {
                book.removeEmptyAskLevel();
            }
        }

        if (qty > 0) {
            int ptr = book.alloc(e.orderId, e.price, qty);
            book.addBid(ptr);
        }
    }

    private void matchSell(OrderEvent e) {
        long qty = e.qty;

        while (qty > 0 && book.bestBidPrice() >= e.price) {
            int bid = book.bestBidHead();
            long traded = Math.min(qty, book.qty(bid));

            qty -= traded;
            book.setQty(bid, book.qty(bid) - traded);
            if (book.qty(bid) == 0) {
                book.removeEmptyBidLevel();
            }
        }

        if (qty > 0) {
            int ptr = book.alloc(e.orderId, e.price, qty);
            book.addAsk(ptr);
        }
    }
}
