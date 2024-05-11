package org.creatism.telaku;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;
import java.nio.charset.Charset;

public class ServerTest {
    public static void main(String[] args) throws InterruptedException {
        ByteBuf buf = Unpooled.copiedBuffer("Hi", Charset.forName("UTF-8"));
        System.err.println(ByteBufUtil.hexDump(buf));
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup elg = new NioEventLoopGroup();
        try {

            server.group(elg)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8080)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf in = (ByteBuf) msg;
                                    System.err.println("Server received: "+ in.toString(CharsetUtil.UTF_8));
                                    ctx.write(in);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                                            .addListener(ChannelFutureListener.CLOSE);
                                    System.err.println("-----");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);
//                                    ctx.writeAndFlush(buf.duplicate())
//                                            .addListener(ChannelFutureListener.CLOSE);

                                }
                            });

                        }
                    });
            ChannelFuture f = server.bind().sync();
            f.channel().closeFuture().sync();
        }finally {
            elg.shutdownGracefully().sync();
        }
    }
}
