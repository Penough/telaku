package org.creatism.telaku.handler.codec.sip;

public interface SipResponse extends SipMessage{
    SipResponseStatus status();
    SipResponse setStatus(SipResponseStatus status);

    @Override
    SipMessage setProtocolVersion(SipVersion version);
}
