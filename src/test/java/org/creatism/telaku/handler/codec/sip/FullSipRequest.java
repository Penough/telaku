package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;

/**
 * combine the {@link SipRequest} and {@link FullSipMessage}, so the request is a complete SIP request
 */
public interface FullSipRequest extends SipRequest, FullSipMessage {
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
    SipRequest setMethod(SipMethod method);

    @Override
    SipRequest setUri(String uri);

    @Override
    SipRequest setProtocolVersion(SipVersion version);
}
