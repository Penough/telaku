package org.creatism.telaku;

import org.creatism.telaku.handler.codec.sip.DefaultSipRequest;
import org.creatism.telaku.handler.codec.sip.SipHeaderUtil;
import org.creatism.telaku.handler.codec.sip.SipMethod;
import org.creatism.telaku.handler.codec.sip.SipVersion;
import org.creatism.telaku.unit.initializer.SipClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class SipClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup grp = new NioEventLoopGroup();
        InetSocketAddress address = new InetSocketAddress("10.0.192.182",5060);

        bootstrap.group(grp)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .remoteAddress(address)
                .handler(new SipClientInitializer());
        ChannelFuture future = bootstrap.connect().sync();
        future.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                System.out.println("Connection established");
                InetSocketAddress local = (InetSocketAddress) f.channel().localAddress();
                SipVersion version = SipVersion.SIP_2_0;
                String uri = "sip:1001@10.0.192.182:5060";
                String sipAgentHost = "10.0.192.182";
                String fromDisplayName = "pe";
                String fromAccount = "1000";
                String toAccount = "1001";
                String fromUri = "sip:" + fromAccount + "@" + sipAgentHost;
                String toUri = "sip:" + toAccount + "@" + sipAgentHost;
                String contactUri = "sip:" + fromAccount + "@"
                        + local.getHostString() + ":" + local.getPort() + ";ob";

                DefaultSipRequest request = new DefaultSipRequest(version, SipMethod.INVITE, uri);
                SipHeaderUtil.addVia(request, local.getHostString(), local.getPort(), "UDP", "test");
                SipHeaderUtil.addFrom(request, fromDisplayName, fromUri, "tag=72b875865bf04860a127807b9f2b4466");
                SipHeaderUtil.addTo(request, null, toUri);
                SipHeaderUtil.addMaxForwards(request, 70);
                SipHeaderUtil.addCallId(request, "1231");
                SipHeaderUtil.addCSeq(request, 1, SipMethod.INVITE);
                SipHeaderUtil.addContentLength(request, 0);
                SipHeaderUtil.addContact(request, fromDisplayName, contactUri);
                f.channel().writeAndFlush(request);
            } else {
                System.err.println("Connection attempt failed");
                f.cause().printStackTrace();
            }
        });
        Channel channel = future.channel();
        future.channel().closeFuture().sync();
        grp.shutdownGracefully().sync();
    }
}
