package org.creatism.telaku.handler.codec.sip;

import io.netty.util.AsciiString;

/**
 * Standard SIP header Values
 */
public class SipHeaderValues {

    /**
     * {@code "TCP"}
     */
    public static final AsciiString TCP = AsciiString.cached("TCP");
    /**
     * {@code "UDP"}
     */
    public static final AsciiString UDP = AsciiString.cached("UDP");
}
