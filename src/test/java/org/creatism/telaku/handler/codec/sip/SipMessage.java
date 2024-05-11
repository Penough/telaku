package org.creatism.telaku.handler.codec.sip;

/**
 * An interface that defines an SIP message, providing common properties for
 * {@link SipRequest} and {@link SipResponse}.
 *  SIP is a text-based protocol and uses the UTF-8 charset (RFC 2279[7])(ISO 10646),
 * thus we can use the feature that english words' ascii is the same as unicode.
 * So we can use {@link io.netty.util.AsciiString} to cache constants.
 *
 * @see SipResponse
 * @see SipRequest
 * @see SipHeaders
 *
 * @author penough
 */
public interface SipMessage extends SipObject {

    /**
     * Returns the protocol version of this {@link SipMessage}
     */
    SipVersion protocolVersion();

    /**
     * Set the protocol version of this {@link SipMessage}
     */
    SipMessage setProtocolVersion(SipVersion version);

    /**
     * Returns the headers of this message.
     */
    SipHeaders headers();
}
