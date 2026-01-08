package exchange.core;

public final class OrderEvent {
    public long orderId;
    public int symbol;
    public long price;
    public long qty;
    public byte side; // 0=BUY, 1=SELL

    public void from(OrderEvent src) {
        this.orderId = src.orderId;
        this.price   = src.price;
        this.qty     = src.qty;
        this.side    = src.side;
    }
}
