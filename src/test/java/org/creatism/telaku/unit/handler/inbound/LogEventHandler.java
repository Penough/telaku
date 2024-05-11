package org.creatism.telaku.unit.handler.inbound;

import org.creatism.telaku.unit.pojo.LogEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent evt) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("[");
        sb.append(evt.getFile());
        sb.append("]:");
        sb.append(evt.getMsg());
        System.out.println(sb);
    }
}
