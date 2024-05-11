package org.creatism.telaku.unit.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if(frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer!");
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (internalBuffer().readableBytes() >= frameLength) {
            ByteBuf buf = internalBuffer().readBytes(frameLength);
            list.add(buf);
        }
    }
}
