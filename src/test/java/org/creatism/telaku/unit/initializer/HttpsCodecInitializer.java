package org.creatism.telaku.unit.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.SslHandler;
import lombok.AllArgsConstructor;

import javax.net.ssl.SSLEngine;

@AllArgsConstructor
public class HttpsCodecInitializer extends ChannelInitializer<Channel> {
    private final OpenSslContext context;
    private final boolean isClient;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(engine));
        if(isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }
}
