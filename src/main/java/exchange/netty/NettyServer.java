package exchange.netty;

import exchange.ringBuffer.RingBuffer;
import exchange.core.OrderEvent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class NettyServer {

    private final RingBuffer<OrderEvent> ring;

    public NettyServer(RingBuffer<OrderEvent> ring) {
        this.ring = ring;
    }

    public void start(int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new OrderDecoder())
                                .addLast(new OrderHandler(ring));
                    }
                });

        bootstrap.bind(port).sync();
        System.out.println("Netty listening on " + port);
    }
}
