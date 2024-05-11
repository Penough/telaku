package org.creatism.telaku.unit.encoder;

import org.creatism.telaku.unit.pojo.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * logEvent encoder
 * just encode outbound msg to "file name:msg"
 * and sen UDP packets
 *
 * tip: mtm encoder super class is a ChannelOutboundHandlerAdapter
 *
 * after all, we should build a bootstrap with this encoder
 */
@AllArgsConstructor
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress;

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent event, List<Object> out) throws Exception {
        byte[] file = event.getFile().getBytes(StandardCharsets.UTF_8);
        byte[] msg = event.getMsg().getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length + 1);
        buf.writeBytes(file);
        buf.writeByte(LogEvent.SEP);
        buf.writeBytes(msg);
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}
