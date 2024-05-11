package org.creatism.telaku.practice.rtp;

import io.netty.buffer.ByteBuf;

public interface Protocol {
    byte[] getBytes();
    ByteBuf getByteBuf();
}
