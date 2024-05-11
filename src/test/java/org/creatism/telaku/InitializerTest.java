package org.creatism.telaku;

import org.creatism.telaku.unit.initializer.HttpAggregatorInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class InitializerTest {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup elg = new NioEventLoopGroup();

        bootstrap.group(elg)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new HttpAggregatorInitializer(false));
        ChannelFuture future = bootstrap.bind(81);
        System.err.println("1111");
        future.syncUninterruptibly();
    }
}
