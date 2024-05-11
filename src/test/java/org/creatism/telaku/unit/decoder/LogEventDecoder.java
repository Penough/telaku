package org.creatism.telaku.unit.decoder;

import org.creatism.telaku.unit.pojo.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket data, List<Object> out) throws Exception {
        ByteBuf buf = data.content();
        int idx = buf.indexOf(0, buf.readableBytes(), LogEvent.SEP);
        String file = buf.slice(0, idx).toString(StandardCharsets.UTF_8);
        String msg = buf.slice(idx+1, buf.readableBytes()).toString(StandardCharsets.UTF_8);
        LogEvent event = new LogEvent(file, msg);
        out.add(event);
    }
}
