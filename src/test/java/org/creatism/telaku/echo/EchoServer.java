package org.creatism.telaku.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
	public static void main(String[] args) throws InterruptedException {
		final EchoServerHandler handler = new EchoServerHandler();
		EventLoopGroup group = new NioEventLoopGroup();
		EventLoopGroup group1 = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap
					.group(group, group1)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(81))
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(handler);
						}
					});
			ChannelFuture f = serverBootstrap.bind().sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully().sync();
		}
	}
}
