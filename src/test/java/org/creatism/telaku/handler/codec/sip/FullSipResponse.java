package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;

/**
 * combine the {@link SipResponse} and {@link FullSipMessage}, so the request is a complete SIP response
 */
public interface FullSipResponse extends SipResponse, FullSipMessage {
    @Override
    FullSipMessage copy();

    @Override
    FullSipMessage duplicate();

    @Override
    FullSipMessage retainedDuplicate();

    @Override
    FullSipMessage replace(ByteBuf content);

    @Override
    FullSipMessage retain(int increment);

    @Override
    FullSipMessage retain();

    @Override
    FullSipMessage touch();

    @Override
    FullSipMessage touch(Object hint);

    @Override
    SipResponse setStatus(SipResponseStatus status);

    @Override
    SipResponse setProtocolVersion(SipVersion version);
}
