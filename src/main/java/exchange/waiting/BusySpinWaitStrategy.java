package exchange.waiting;

public class BusySpinWaitStrategy implements WaitStrategy {
    public void idle() {
        Thread.onSpinWait();
    }
}
