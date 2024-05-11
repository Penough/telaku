package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

import static io.netty.util.ByteProcessor.FIND_ASCII_SPACE;
import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;
import static java.lang.Integer.parseInt;

/**
 * The response code and its description of HTTP or its derived protocols, according to
 * <a href="https://www.rfc-editor.org/rfc/rfc3261.html#section-21">Response Codes</a>
 */
public class SipResponseStatus implements Comparable<SipResponseStatus>{


    /**
     * 100 Trying
     */
    public static final SipResponseStatus TRYING = newStatus(100, "Trying");

    /**
     * 180 Ringing
     */
    public static final SipResponseStatus RINGING = newStatus(180, "Ringing");

    /**
     * 181 Call Is Being Forwarded
     */
    public static final SipResponseStatus CALL_IS_BEING_FORWARDED = newStatus(181, "Call Is Being Forwarded");

    /**
     * 182 Queued
     */
    public static final SipResponseStatus QUEUED = newStatus(182, "Queued");

    /**
     * 183 Session Progress
     */
    public static final SipResponseStatus SESSION_PROGRESS = newStatus(183, "Session Progress");

    /**
     * 200 OK
     */
    public static final SipResponseStatus OK = newStatus(200, "OK");

    /**
     * 300 Multiple Choices
     */
    public static final SipResponseStatus MULTIPLE_CHOICES = newStatus(300, "Multiple Choices");

    /**
     * 301 Moved Permanently
     */
    public static final SipResponseStatus MOVED_PERMANENTLY = newStatus(301, "Moved Permanently");

    /**
     * 302 Moved Temporarily
     */
    public static final SipResponseStatus MOVED_TEMPORARILY = newStatus(300, "Moved Temporarily");

    /**
     * 305 Use Proxy
     */
    public static final SipResponseStatus USE_PROXY = newStatus(305, "Use Proxy");

    /**
     * 380 Alternative ServiceUnauthorized
     */
    public static final SipResponseStatus ALTERNATIVE_SERVICE = newStatus(380, "Alternative Service");

    /**
     * 400 Bad Request
     */
    public static final SipResponseStatus BAD_REQUEST = newStatus(400, "Bad Request");

    /**
     * 401 Unauthorized
     */
    public static final SipResponseStatus UNAUTHORIZED = newStatus(401, "Unauthorized");

    /**
     * 402 Unauthorized
     */
    public static final SipResponseStatus PAYMENT_REQUIRED = newStatus(402, "Payment Required");

    /**
     * 403 Forbidden
     */
    public static final SipResponseStatus FORBIDDEN = newStatus(403, "Forbidden");

    /**
     * 404 Not Found
     */
    public static final SipResponseStatus NOT_FOUND = newStatus(404, "Not Found");

    /**
     * 405 Method Not Allowed
     */
    public static final SipResponseStatus METHOD_NOT_ALLOWED = newStatus(405, "Method Not Allowed");

    /**
     * 406 Not Acceptable
     */
    public static final SipResponseStatus NOT_ACCEPTABLE = newStatus(406, "Not Acceptable");

    /**
     * 407 Proxy Authentication Required
     */
    public static final SipResponseStatus PROXY_AUTHENTICATION_REQUIRED = newStatus(407, "Proxy Authentication Required");

    /**
     * 408 Request Timeout
     */
    public static final SipResponseStatus REQUEST_TIMEOUT = newStatus(408, "Request Timeout");

    /**
     * 410 Gone
     */
    public static final SipResponseStatus GONE = newStatus(410, "Gone");

    /**
     * 413 Request Entity Too Large
     */
    public static final SipResponseStatus REQUEST_ENTITY_TOO_LARGE = newStatus(413, "Request Entity Too Large");

    /**
     * 414 Request-URI Too Long
     */
    public static final SipResponseStatus REQUEST_URI_TOO_LONG = newStatus(414, "Request-URI Too Long");

    /**
     * 415 Unsupported Media Type
     */
    public static final SipResponseStatus UNSUPPORTED_MEDIA_TYPE = newStatus(415, "Unsupported Media Type");

    /**
     * 416 Unsupported URI Scheme
     */
    public static final SipResponseStatus UNSUPPORTED_URI_SCHEME = newStatus(416, "Unsupported URI Scheme");

    /**
     * 420 Bad Extension
     */
    public static final SipResponseStatus BAD_EXTENSION = newStatus(420, "Bad Extension");

    /**
     * 421 Extension Required
     */
    public static final SipResponseStatus EXTENSION_REQUIRED = newStatus(421, "Extension Required");

    /**
     * 423 Interval Too Brief
     */
    public static final SipResponseStatus INTERVAL_TOO_BRIEF = newStatus(423, "Interval Too Brief");

    /**
     * 480 Temporarily Unavailable
     */
    public static final SipResponseStatus TEMPORARILY_UNAVAILABLE = newStatus(480, "Temporarily Unavailable");

    /**
     * 481 Call/Transaction Does Not Exist
     */
    public static final SipResponseStatus CALL_TRANSACTION_DOES_NOT_EXIST = newStatus(481, "Call/Transaction Does Not Exist");

    /**
     * 483 Too Many Hops
     */
    public static final SipResponseStatus TOO_MANY_HOPS = newStatus(483, "Too Many Hops");

    /**
     * 484 Address Incomplete
     */
    public static final SipResponseStatus ADDRESS_INCOMPLETE = newStatus(484, "Address Incomplete");

    /**
     * 485 Ambiguous
     */
    public static final SipResponseStatus AMBIGUOUS = newStatus(485, "Ambiguous");

    /**
     * 486 Busy Here
     */
    public static final SipResponseStatus BUSY_HERE = newStatus(486, "Busy Here");

    /**
     * 487 Request Terminated
     */
    public static final SipResponseStatus REQUEST_TERMINATED = newStatus(487, "Request Terminated");

    /**
     * 488 Not Acceptable Here
     */
    public static final SipResponseStatus NOT_ACCEPTABLE_HERE = newStatus(488, "Not Acceptable Here");

    /**
     * 491 Request Pending
     */
    public static final SipResponseStatus REQUEST_PENDING = newStatus(491, "Request Pending");

    /**
     * 493 Undecipherable
     */
    public static final SipResponseStatus UNDECIPHERABLE = newStatus(493, "Undecipherable");

    /**
     * 500 Server Internal Error
     */
    public static final SipResponseStatus SERVER_INTERNAL_ERROR = newStatus(500, "Server Internal Error");

    /**
     * 501 Not Implemented
     */
    public static final SipResponseStatus NOT_IMPLEMENTED = newStatus(501, "Not Implemented");

    /**
     * 502 Bad Gateway
     */
    public static final SipResponseStatus BAD_GATEWAY = newStatus(502, "Bad Gateway");

    /**
     * 503 Service Unavailable
     */
    public static final SipResponseStatus SERVICE_UNAVAILABLE = newStatus(503, "Service Unavailable");

    /**
     * 504 Server Time-out
     */
    public static final SipResponseStatus SERVER_TIMEOUT = newStatus(504, "Server Time-out");

    /**
     * 505 Version Not Supported
     */
    public static final SipResponseStatus VERSION_NOT_SUPPORTED = newStatus(505, "Version Not Supported");

    /**
     * 513 Message Too Large
     */
    public static final SipResponseStatus MESSAGE_TOO_LARGE = newStatus(513, "Message Too Large");

    /**
     * 600 Busy Everywhere
     */
    public static final SipResponseStatus BUSY_EVERYWHERE = newStatus(600, "Busy Everywhere");

    /**
     * 603 Decline
     */
    public static final SipResponseStatus DECLINE = newStatus(603, "Decline");

    /**
     * 604 Does Not Exist Anywhere
     */
    public static final SipResponseStatus DOES_NOT_EXIST_ANYWHERE = newStatus(604, "Does Not Exist Anywhere");

    /**
     * 606 Not Acceptable
     */
    public static final SipResponseStatus NOT_ACCEPTABLE_FOR_BUSY = newStatus(606, "Not Acceptable");

    /**
     * Returns the {@link SipResponseStatus} represented by the specified code.
     * If the specified code is a standard SIP status code, a cached instance
     * will be returned.  Otherwise, a new instance will be returned.
     */
    public static SipResponseStatus valueOf(int code) {
        SipResponseStatus status = valueOf0(code);
        return status != null ? status : new SipResponseStatus(code);
    }


    private static SipResponseStatus valueOf0(int code) {
        switch (code) {
            case 100:
                return TRYING;
        }
        return null;
    }

    /**
     * Returns the {@link SipResponseStatus} represented by the specified {@code code} and {@code reasonPhrase}.
     * If the specified code is a standard SIP status {@code code} and {@code reasonPhrase}, a cached instance
     * will be returned. Otherwise, a new instance will be returned.
     * @param code The response code value.
     * @param reasonPhrase The response code reason phrase.
     * @return the {@link SipResponseStatus} represented by the specified {@code code} and {@code reasonPhrase}.
     */
    public static SipResponseStatus valueOf(int code, String reasonPhrase) {
        SipResponseStatus responseStatus = valueOf0(code);
        return responseStatus != null && responseStatus.reasonPhrase().contentEquals(reasonPhrase) ? responseStatus :
                new SipResponseStatus(code, reasonPhrase);
    }


    /**
     * Parses the specified SIP status line into a {@link SipResponseStatus}. The expected formats of the line are:
     * <ul>
     * <li>{@code statusCode} (e.g. 200)</li>
     * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
     * </ul>
     *
     * @throws IllegalArgumentException if the specified status line is malformed
     */
    public static SipResponseStatus parseLine(CharSequence line) {
        return (line instanceof AsciiString) ? parseLine((AsciiString) line) : parseLine(line.toString());
    }

    /**
     * Parses the specified SIP status line into a {@link SipResponseStatus}. The expected formats of the line are:
     * <ul>
     * <li>{@code statusCode} (e.g. 200)</li>
     * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
     * </ul>
     *
     * @throws IllegalArgumentException if the specified status line is malformed
     */
    public static SipResponseStatus parseLine(String line) {
        try {
            int space = line.indexOf(' ');
            return space == -1 ? valueOf(parseInt(line)) :
                    valueOf(parseInt(line.substring(0, space)), line.substring(space + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("malformed status line: " + line, e);
        }
    }

    /**
     * Parses the specified Sip status line into a {@link SipResponseStatus}. The expected formats of the line are:
     * <ul>
     * <li>{@code statusCode} (e.g. 200)</li>
     * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
     * </ul>
     *
     * @throws IllegalArgumentException if the specified status line is malformed
     */
    public static SipResponseStatus parseLine(AsciiString line) {
        try {
            int space = line.forEachByte(FIND_ASCII_SPACE);
            return space == -1 ? valueOf(line.parseInt()) : valueOf(line.parseInt(0, space), line.toString(space + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("malformed status line: " + line, e);
        }
    }

    private final int code;
    private final AsciiString codeAsText;
    private SipStatusClass codeClass;

    private final String reasonPhrase;
    private final byte[] bytes;

    /**
     * Creates a new instance with the specified {@code code} and the auto-generated default reason phrase.
     */
    private SipResponseStatus(int code) {
        this(code, SipStatusClass.valueOf(code).defaultReasonPhrase() + " (" + code + ')', false);
    }

    /**
     * Creates a new instance with the specified {@code code} and its {@code reasonPhrase}.
     */
    public SipResponseStatus(int code, String reasonPhrase) {
        this(code, reasonPhrase, false);
    }

    private SipResponseStatus(int code, String reasonPhrase, boolean bytes) {
        checkPositiveOrZero(code, "code");
        ObjectUtil.checkNotNull(reasonPhrase, "reasonPhrase");

        for (int i = 0; i < reasonPhrase.length(); i ++) {
            char c = reasonPhrase.charAt(i);
            // Check prohibited characters.
            switch (c) {
                case '\n': case '\r':
                    throw new IllegalArgumentException(
                            "reasonPhrase contains one of the following prohibited characters: " +
                                    "\\r\\n: " + reasonPhrase);
            }
        }

        this.code = code;
        String codeString = Integer.toString(code);
        codeAsText = new AsciiString(codeString);
        this.reasonPhrase = reasonPhrase;
        if (bytes) {
            this.bytes = (codeString + ' ' + reasonPhrase).getBytes(CharsetUtil.US_ASCII);
        } else {
            this.bytes = null;
        }
    }


    /**
     * Returns the code of this {@link SipResponseStatus}.
     */
    public int code() {
        return code;
    }

    /**
     * Returns the status code as {@link AsciiString}.
     */
    public AsciiString codeAsText() {
        return codeAsText;
    }

    /**
     * Returns the reason phrase of this {@link SipResponseStatus}.
     */
    public String reasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Returns the class of this {@link SipResponseStatus}
     */
    public SipStatusClass codeClass() {
        SipStatusClass type = this.codeClass;
        if (type == null) {
            this.codeClass = type = SipStatusClass.valueOf(code);
        }
        return type;
    }

    @Override
    public int hashCode() {
        return code();
    }

    private static SipResponseStatus newStatus(int statusCode, String reasonPhrase) {
        return new SipResponseStatus(statusCode, reasonPhrase, true);
    }

    @Override
    public int compareTo(SipResponseStatus o) {
        return code() - o.code();
    }

    @Override
    public String toString() {
        return new StringBuilder(reasonPhrase.length() + 4)
                .append(codeAsText)
                .append(' ')
                .append(reasonPhrase)
                .toString();
    }

    void encode(ByteBuf buf) {
        if (bytes == null) {
            ByteBufUtil.copy(codeAsText, buf);
            buf.writeByte(SipConstants.SP);
            buf.writeCharSequence(reasonPhrase, CharsetUtil.US_ASCII);
        } else {
            buf.writeBytes(bytes);
        }
    }
}
