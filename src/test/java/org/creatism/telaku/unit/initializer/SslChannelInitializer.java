package org.creatism.telaku.unit.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.AllArgsConstructor;

import javax.net.ssl.SSLEngine;

@AllArgsConstructor
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final OpenSslContext sslContext;
    private final boolean startTls;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }
}
