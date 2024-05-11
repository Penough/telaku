package org.creatism.telaku;

import static org.junit.Assert.assertTrue;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    @Test
    public void logicCacu() {
        int mask = 2, onlyMask = 510,executionMask = 98576;
        System.err.println(executionMask & (onlyMask | mask));
    }
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup elg = new NioEventLoopGroup();
        bootstrap.group(elg)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("10.0.192.141", 8080))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {


                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                super.channelActive(ctx);
                                ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", StandardCharsets.UTF_8));
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

                                System.out.println("Client received0: " + msg.toString(CharsetUtil.UTF_8));
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx,
                                                       Throwable cause) {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });
                    }
                });
        ChannelFuture future = bootstrap.connect().sync();
        future.channel().closeFuture().sync();
        elg.shutdownGracefully().sync();
    }
}
