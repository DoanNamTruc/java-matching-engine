package exchange.waiting;

public class YieldWaitStrategy implements WaitStrategy {
    public void idle() {
        Thread.yield();
    }
}
