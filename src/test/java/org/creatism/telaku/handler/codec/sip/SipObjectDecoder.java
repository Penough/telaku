package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

import java.util.List;

import static io.netty.util.internal.ObjectUtil.checkPositive;

public abstract class SipObjectDecoder extends ByteToMessageDecoder {
    public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    public static final boolean DEFAULT_VALIDATE_HEADERS = true;
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
    public static final boolean DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS = false;

    private static final String EMPTY_VALUE = "";

    protected final boolean validateHeaders;
    private final boolean allowDuplicateContentLengths;
    private final SipObjectDecoder.HeaderParser headerParser;
    private final SipObjectDecoder.LineParser lineParser;

    private SipMessage message;
    private long contentLength = Long.MIN_VALUE;

    private volatile boolean resetRequested;

    // These will be updated by splitHeader(...)
    private CharSequence name;
    private CharSequence value;


    /**
     * The internal state of {@link SipObjectDecoder}.
     * <em>Internal use only</em>.
     */
    private enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER,
        READ_FIXED_LENGTH_CONTENT,
        BAD_MESSAGE
    }

    private State currentState = State.SKIP_CONTROL_CHARS;


    /**
     * Creates a new instance with the default
     * {@code maxInitialLineLength (4096}}, {@code maxHeaderSize (8192)}, and
     * {@code maxChunkSize (8192)}.
     */
    protected SipObjectDecoder() {
        this(DEFAULT_MAX_INITIAL_LINE_LENGTH, DEFAULT_MAX_HEADER_SIZE);
    }

    protected SipObjectDecoder(int maxInitialLineLength, int maxHeaderSize) {
        this(maxInitialLineLength, maxHeaderSize, DEFAULT_VALIDATE_HEADERS);
    }

    protected SipObjectDecoder(int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders) {
        this(maxInitialLineLength, maxHeaderSize, validateHeaders,
                DEFAULT_INITIAL_BUFFER_SIZE);
    }

    protected SipObjectDecoder(
            int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders, int initialBufferSize) {
        this(maxInitialLineLength, maxHeaderSize, validateHeaders, initialBufferSize,
                DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS);
    }

    protected SipObjectDecoder(
            int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders, int initialBufferSize,
            boolean allowDuplicateContentLengths) {
        checkPositive(maxInitialLineLength, "maxInitialLineLength");
        checkPositive(maxHeaderSize, "maxHeaderSize");

        AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);
        lineParser = new SipObjectDecoder.LineParser(seq, maxInitialLineLength);
        headerParser = new SipObjectDecoder.HeaderParser(seq, maxHeaderSize);
        this.validateHeaders = validateHeaders;
        this.allowDuplicateContentLengths = allowDuplicateContentLengths;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (resetRequested) {
            resetNow();
        }

        switch (currentState) {
            case SKIP_CONTROL_CHARS:
                // Fall-through
            case READ_INITIAL:
                try {
                    AppendableCharSequence line = lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    String[] initialLine = splitInitialLine(line);
                    if (initialLine.length < 3) {
                        // Invalid initial line - ignore.
                        currentState = State.SKIP_CONTROL_CHARS;
                        return;
                    }

                    message = createMessage(initialLine);
                    currentState = State.READ_HEADER;
                    // fall-through
                } catch (Exception e) {
                    out.add(invalidMessage(buffer, e));
                    return;
                }
            case READ_HEADER: try {
                State nextState = readHeaders(buffer, ctx.channel());
                currentState = nextState;
            } catch (Exception e) {
                out.add(invalidMessage(buffer, e));
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                int toRead = (int)contentLength;
                ByteBuf content = buffer.readRetainedSlice(toRead);
//                out.add(new DefaultSipContent(content, validateHeaders));
                return;
            }
            default:
                break;
        }
    }

    private void resetNow() {
//        SipMessage message = this.message;
        this.message = null;
        name = null;
        value = null;
        contentLength = Long.MIN_VALUE;
        lineParser.reset();
        headerParser.reset();
//        trailer = null;
//        if (!isDecodingRequest()) {
//            HttpResponse res = (HttpResponse) message;
//            if (res != null && isSwitchingToNonHttp1Protocol(res)) {
//                currentState = SipObjectDecoder.State.UPGRADED;
//                return;
//            }
//        }

        resetRequested = false;
        currentState = State.SKIP_CONTROL_CHARS;
    }

    protected abstract boolean isDecodingRequest();
    protected abstract SipMessage createMessage(String[] initialLine) throws Exception;
    protected abstract SipMessage createInvalidMessage();

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;

        aStart = findNonSPLenient(sb, 0);
        aEnd = findSPLenient(sb, aStart);

        bStart = findNonSPLenient(sb, aEnd);
        bEnd = findSPLenient(sb, bStart);

        cStart = findNonSPLenient(sb, bEnd);
        cEnd = findEndOfString(sb);

        return new String[] {
                sb.subStringUnsafe(aStart, aEnd),
                sb.subStringUnsafe(bStart, bEnd),
                cStart < cEnd? sb.subStringUnsafe(cStart, cEnd) : "" };
    }

    private static int findNonSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            // See https://tools.ietf.org/html/rfc7230#section-3.5
            if (isSPLenient(c)) {
                continue;
            }
            if (Character.isWhitespace(c)) {
                // Any other whitespace delimiter is invalid
                throw new IllegalArgumentException("Invalid separator");
            }
            return result;
        }
        return sb.length();
    }

    private static int findSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (isSPLenient(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static boolean isSPLenient(char c) {
        // See https://tools.ietf.org/html/rfc7230#section-3.5
        return c == ' ' || c == (char) 0x09 || c == (char) 0x0B || c == (char) 0x0C || c == (char) 0x0D;
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }

    private static class HeaderParser implements ByteProcessor {
        private final AppendableCharSequence seq;
        private final int maxLength;
        int size;

        HeaderParser(AppendableCharSequence seq, int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            final int oldSize = size;
            seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return seq;
        }

        public void reset() {
            size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char) (value & 0xFF);
            if (nextByte == SipConstants.LF) {
                int len = seq.length();
                // Drop CR if we had a CRLF pair
                if (len >= 1 && seq.charAtUnsafe(len - 1) == SipConstants.CR) {
                    -- size;
                    seq.setLength(len - 1);
                }
                return false;
            }

            increaseCount();

            seq.append(nextByte);
            return true;
        }

        protected final void increaseCount() {
            if (++ size > maxLength) {
                // TODO: Respond with Bad Request and discard the traffic
                //    or close the connection.
                //       No need to notify the upstream handlers - just log.
                //       If decoding a response, just throw an exception.
                throw newException(maxLength);
            }
        }

        protected TooLongFrameException newException(int maxLength) {
            return new TooLongSipHeaderException("SIP header is larger than " + maxLength + " bytes.");
        }
    }

    private final class LineParser extends SipObjectDecoder.HeaderParser {

        LineParser(AppendableCharSequence seq, int maxLength) {
            super(seq, maxLength);
        }

        @Override
        public AppendableCharSequence parse(ByteBuf buffer) {
            // Suppress a warning because HeaderParser.reset() is supposed to be called
            reset();    // lgtm[java/subtle-inherited-call]
            return super.parse(buffer);
        }

        @Override
        public boolean process(byte value) throws Exception {
            if (currentState == State.SKIP_CONTROL_CHARS) {
                char c = (char) (value & 0xFF);
                if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                    increaseCount();
                    return true;
                }
                currentState = State.READ_INITIAL;
            }
            return super.process(value);
        }

        @Override
        protected TooLongFrameException newException(int maxLength) {
            return new TooLongSipHeaderException("An SIP line is larger than " + maxLength + " bytes.");
        }
    }

    private SipMessage invalidMessage(ByteBuf in, Exception cause) {
        currentState = State.BAD_MESSAGE;

        // Advance the readerIndex so that ByteToMessageDecoder does not complain
        // when we produced an invalid message without consuming anything.
        in.skipBytes(in.readableBytes());

        if (message == null) {
            message = createInvalidMessage();
        }
        message.setDecoderResult(DecoderResult.failure(cause));

        SipMessage ret = message;
        message = null;
        return ret;
    }

    private State readHeaders(ByteBuf buffer, Channel channel) {
        final SipMessage message = this.message;
        final SipHeaders headers = message.headers();

        AppendableCharSequence line = headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAtUnsafe(0);
                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
                    //please do not make one line from below code
                    //as it breaks +XX:OptimizeStringConcat optimization
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf(value);
                    value = valueStr + ' ' + trimmedLine;
                } else {
                    if (name != null) {
                        headers.add(name, value);
                    }
                    splitHeader(line);
                }

                line = headerParser.parse(buffer);
                if (line == null) {
                    return null;
                }
            } while (line.length() > 0);
        }

        // Add the last header.
        if (name != null) {
            headers.add(name, value);
        }

        // reset name and value fields
        name = null;
        value = null;

        // Done parsing initial line and headers. Set decoder result.
        SipMessageDecoderResult decoderResult = new SipMessageDecoderResult(lineParser.size, headerParser.size);
        message.setDecoderResult(decoderResult);
        //  Header field          where   proxy ACK BYE CAN INV OPT REG
        //  Content-Length                 ar    t   t   t   t   t   t
        // a: A proxy can add or concatenate the header field if not present.
        // r: A proxy must be able to read the header field, and thus this
        //           header field cannot be encrypted.
        // t: The header field SHOULD be sent, but clients/servers need to be
        //           prepared to receive messages without that header field.
        // https://www.rfc-editor.org/rfc/rfc3261.html  Table 2: Summary of header fields, A--O
        // The "chunked" transfer encoding of HTTP/1.1 MUST NOT be used for SIP
        // https://www.rfc-editor.org/rfc/rfc3261.html#section-7.4.2
        List<String> contentLengthFields = headers.getAllWithCompact(SipHeaderNames.CONTENT_LENGTH, SipHeaderNames.L);
        if(!contentLengthFields.isEmpty()) {
            contentLength = SipUtil.normalizeAndGetContentLength(contentLengthFields);
        } else {
            if(!(channel instanceof DatagramChannel)) {
                throw new IllegalArgumentException(
                        "Content-Length is required with Stream-based protocol.");
            }
        }
        if(contentLength > 0) {
            return State.READ_FIXED_LENGTH_CONTENT;
        } else {
            return State.SKIP_CONTROL_CHARS;
        }
    }



    private void splitHeader(AppendableCharSequence sb) {
        final int length = sb.length();
        int nameStart;
        int nameEnd;
        int colonEnd;
        int valueStart;
        int valueEnd;

        nameStart = findNonWhitespace(sb, 0);
        for (nameEnd = nameStart; nameEnd < length; nameEnd ++) {
            char ch = sb.charAtUnsafe(nameEnd);
            // https://tools.ietf.org/html/rfc7230#section-3.2.4
            //
            // No whitespace is allowed between the header field-name and colon. In
            // the past, differences in the handling of such whitespace have led to
            // security vulnerabilities in request routing and response handling. A
            // server MUST reject any received request message that contains
            // whitespace between a header field-name and colon with a response code
            // of 400 (Bad Request). A proxy MUST remove any such whitespace from a
            // response message before forwarding the message downstream.
            if (ch == ':' ||
                    // In case of decoding a request we will just continue processing and header validation
                    // is done in the DefaultHttpHeaders implementation.
                    //
                    // In the case of decoding a response we will "skip" the whitespace.
                    (!isDecodingRequest() && isOWS(ch))) {
                break;
            }
        }

        if (nameEnd == length) {
            // There was no colon present at all.
            throw new IllegalArgumentException("No colon found");
        }

        for (colonEnd = nameEnd; colonEnd < length; colonEnd ++) {
            if (sb.charAtUnsafe(colonEnd) == ':') {
                colonEnd ++;
                break;
            }
        }

        name = sb.subStringUnsafe(nameStart, nameEnd);
        valueStart = findNonWhitespace(sb, colonEnd);
        if (valueStart == length) {
            value = EMPTY_VALUE;
        } else {
            valueEnd = findEndOfString(sb);
            value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (!Character.isWhitespace(c)) {
                return result;
            } else if (!isOWS(c)) {
                // Only OWS is supported for whitespace
                throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed," +
                        " but received a '" + c + "' (0x" + Integer.toHexString(c) + ")");
            }
        }
        return sb.length();
    }

    private static boolean isOWS(char ch) {
        return ch == ' ' || ch == (char) 0x09;
    }
}
