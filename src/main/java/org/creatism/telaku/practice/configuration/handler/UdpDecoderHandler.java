package org.creatism.telaku.practice.configuration.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * To be considerable, it should be really careful when using @Sharable.
 * It should be guaranteed that thread safety.
 */
@Slf4j
@ChannelHandler.Sharable
@Service
public class UdpDecoderHandler extends MessageToMessageDecoder<DatagramPacket>  {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket byteBuf, List<Object> list) throws Exception {
        ByteBuf byteBuf1 = byteBuf.content();
        int size = byteBuf1.readableBytes();
        byte[] data = new byte[size];
        byteBuf1.readBytes(data);
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().localAddress();
        log.info("port:{} recieved {}", socketAddress.getPort(), new String(data));
    }
}

