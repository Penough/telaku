package org.creatism.telaku.handler.codec.sip;

public interface SipRequest extends SipMessage{

    /**
     * Returns the {@link SipMethod} of this {@link SipRequest}.
     */
    SipMethod method();

    /**
     * set SIP method
     */
    SipRequest setMethod(SipMethod method);

    /**
     * return SIP uri
     */
    String uri();

    /**
     * set SIP uri
     */
    SipRequest setUri(String uri);

    @Override
    SipRequest setProtocolVersion(SipVersion version);
}
