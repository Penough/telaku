package org.creatism.telaku.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
	public static void main(String[] args) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress("10.0.192.152", 81))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new EchoClientHandler());
						}
					});
//			bootstrap.bind(845);
			ChannelFuture f = bootstrap.connect().sync();
			f.channel().closeFuture().sync();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			group.shutdownGracefully().sync();
		}
	}
}
