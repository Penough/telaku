package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
/**
 * Decodes {@link ByteBuf}s into {@link SipResponse}s and {@link SipContent}s.
 * <h3>Parameters that prevents excessive memory consumption</h3>
 * <table border="1">
 * <tr>
 * <th>Name</th><th>Meaning</th>
 * </tr>
 * <tr>
 * <td>{@code maxInitialLineLength}</td>
 * <td>The maximum length of the initial line (e.g. {@code "SIP/1.0 200 OK"})
 *     If the length of the initial line exceeds this value, a
 *     {@link TooLongSipLineException} will be raised.</td>
 * </tr>
 * <tr>
 * <td>{@code maxHeaderSize}</td>
 * <td>The maximum length of all headers.  If the sum of the length of each
 *     header exceeds this value, a {@link TooLongSipHeaderException} will be raised.</td>
 * </tr>
 * <tr>
 * </table>
 *
 * <h3>arameters that control parsing behavior</h3>
 * <table border="1">
 * <tr>
 * <td>{@code allowDuplicateContentLengths}</td>
 * <td>{@value #DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS}</td>
 * <td>When set to {@code false}, will reject any messages that contain multiple Content-Length header fields.
 *     When set to {@code true}, will allow multiple Content-Length headers only if they are all the same decimal value.
 *     The duplicated field-values will be replaced with a single valid Content-Length field.
 *     See <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">RFC 7230, Section 3.3.2</a>.</td>
 * </tr>
 * </table>
 */
public class SipResponseDecoder extends SipObjectDecoder {

    private static final SipResponseStatus UNKNOWN_STATUS = new SipResponseStatus(999, "Unknown");

    /**
     * Creates a new instance with the default maxInitialLineLength (4096), maxHeaderSize (8192), and maxChunkSize (8192).
     */
    public SipResponseDecoder() {
    }

    public SipResponseDecoder(int maxInitialLineLength, int maxHeaderSize) {
        super(maxInitialLineLength, maxHeaderSize);
    }

    public SipResponseDecoder(
            int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, validateHeaders);
    }

    public SipResponseDecoder(
            int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders,
            int initialBufferSize) {
        super(maxInitialLineLength, maxHeaderSize, validateHeaders, initialBufferSize);
    }

    protected SipResponseDecoder(
            int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders,
            int initialBufferSize, boolean allowDuplicateContentLengths) {
        super(maxInitialLineLength, maxHeaderSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }

    @Override
    protected SipMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultSipResponse(
                SipVersion.valueOf(initialLine[0]),
                SipResponseStatus.valueOf(Integer.parseInt(initialLine[1]), initialLine[2]), validateHeaders);
    }

    @Override
    protected SipMessage createInvalidMessage() {
        return new DefaultFullSipResponse(SipVersion.SIP_1_0, UNKNOWN_STATUS, validateHeaders);
    }
}
