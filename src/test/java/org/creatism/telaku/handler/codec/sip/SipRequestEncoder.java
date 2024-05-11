package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Encodes an {@link SipRequest} into a {@link ByteBuf}
 *
 */
@Slf4j
public class SipRequestEncoder extends SipObjectEncoder<SipRequest> {

    /**
     * Encode Request-Line
     * <p>
     * Request-Line  =  Method SP Request-URI SP SIP-Version CRLF
     * </p>
     * <p>
     * Method: like HTTP methods.Sip implements {@link SipMethod}
     * </p>
     * <p>
     * Request-URI: It indicates
     *            the user or service to which this request is being addressed.
     *            The Request-URI MUST NOT contain unescaped spaces or control
     *            characters and MUST NOT be enclosed in "<>"
     * <a href="https://www.rfc-editor.org/rfc/rfc3261.html#section-19.1">
     *     RFC 3261, 19.1
     * </a>
     * </p>
     */
    @Override
    protected void encodeInitialLine(ByteBuf buf, SipRequest request) throws Exception {
        log.debug("encodeInitialLine...");
        ByteBufUtil.copy(request.method().asciiName(), buf);

        String uri = request.uri();
        if(StringUtil.isNullOrEmpty(uri)) {
            throw new IllegalArgumentException("uri is empty");
        }
        CharSequence uriCharSequence = uri;
        buf.writeByte(SipConstants.SP).writeCharSequence(uriCharSequence, CharsetUtil.UTF_8);
        buf.writeByte(SipConstants.SP);
        request.protocolVersion().encode(buf);
        ByteBufUtil.writeShortBE(buf, CRLF_SHORT);
    }
}
