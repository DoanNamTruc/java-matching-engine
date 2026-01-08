package exchange.netty;

import exchange.core.OrderEvent;
import exchange.ringBuffer.RingBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class OrderHandler extends SimpleChannelInboundHandler<OrderEvent> {

    private final RingBuffer<OrderEvent> ring;

    public OrderHandler(RingBuffer<OrderEvent> ring) {
        this.ring = ring;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OrderEvent msg) {
        OrderEvent slot = ring.get(ring.next());
        slot.orderId = msg.orderId;
        slot.symbol = msg.symbol;
        slot.price = msg.price;
        slot.qty = msg.qty;
        slot.side = msg.side;
    }
}
