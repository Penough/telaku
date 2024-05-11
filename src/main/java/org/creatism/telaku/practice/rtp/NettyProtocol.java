package org.creatism.telaku.practice.rtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class NettyProtocol implements Protocol{
    protected byte[] bytes;
    protected final static byte DEF_HEADER = -127;
    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public ByteBuf getByteBuf() {
        return Unpooled.copiedBuffer(bytes);
    }
}
