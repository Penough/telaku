package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.util.internal.ObjectUtil.checkNonEmptyAfterTrim;
import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;

/**
 * According to RFC 3261, I can only know the SIP/2.0 version
 *
 *
 * @author penough
 */
public class SipVersion implements Comparable<SipVersion> {
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");

    private static final String SIP_1_0_STRING = "SIP/1.0";
    private static final String SIP_2_0_STRING = "SIP/2.0";


    private final String protocolName;
    private final int majorVersion;
    private final int minorVersion;
    private final String text;
    private final byte[] bytes;

    /**
     * SIP/2.0
     */
    public static final SipVersion SIP_1_0 = new SipVersion("SIP", 1, 0,  true);

    /**
     * SIP/2.0
     */
    public static final SipVersion SIP_2_0 = new SipVersion("SIP", 2, 0,   true);


    public SipVersion(String text) {
        text = checkNonEmptyAfterTrim(text, "text").toUpperCase();

        Matcher m = VERSION_PATTERN.matcher(text);
        if (!m.matches()) {
            throw new IllegalArgumentException("invalid version format: " + text);
        }

        protocolName = m.group(1);
        majorVersion = Integer.parseInt(m.group(2));
        minorVersion = Integer.parseInt(m.group(3));
        this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
        bytes = null;
    }


    public SipVersion(String protocolName, int majorVersion, int minorVersion) {
        this(protocolName, majorVersion, minorVersion,false);
    }

    private SipVersion(
            String protocolName, int majorVersion, int minorVersion, boolean bytes) {
        protocolName = checkNonEmptyAfterTrim(protocolName, "protocolName").toUpperCase();

        for (int i = 0; i < protocolName.length(); i ++) {
            if (Character.isISOControl(protocolName.charAt(i)) ||
                    Character.isWhitespace(protocolName.charAt(i))) {
                throw new IllegalArgumentException("invalid character in protocolName");
            }
        }

        checkPositiveOrZero(majorVersion, "majorVersion");
        checkPositiveOrZero(minorVersion, "minorVersion");

        this.protocolName = protocolName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        text = protocolName + '/' + majorVersion + '.' + minorVersion;

        if (bytes) {
            this.bytes = text.getBytes(CharsetUtil.US_ASCII);
        } else {
            this.bytes = null;
        }
    }

    public static SipVersion valueOf(String text) {
        ObjectUtil.checkNotNull(text, "text");

        text = text.trim();

        if (text.isEmpty()) {
            throw new IllegalArgumentException("text is empty (possibly SIP/0.9)");
        }

        // Try to match without convert to uppercase first as this is what 99% of all clients
        // will send anyway. Also there is a change to the RFC to make it clear that it is
        // expected to be case-sensitive
        //
        // See:
        // * https://trac.tools.ietf.org/wg/httpbis/trac/ticket/1
        // * https://trac.tools.ietf.org/wg/httpbis/trac/wiki
        //
        SipVersion version = version0(text);
        if (version == null) {
            version = new SipVersion(text);
        }
        return version;
    }
    private static SipVersion version0(String text) {
        if (SIP_1_0_STRING.equals(text)) {
            return SIP_1_0;
        }
        if (SIP_2_0_STRING.equals(text)) {
            return SIP_2_0;
        }
        return null;
    }


    public String protocolName() {
        return protocolName;
    }

    public int majorVersion() {
        return majorVersion;
    }

    public int minorVersion() {
        return minorVersion;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text();
    }

    @Override
    public int hashCode() {
        return (protocolName().hashCode() * 31 + majorVersion()) * 31 +
                minorVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SipVersion)) {
            return false;
        }

        SipVersion that = (SipVersion) o;
        return minorVersion() == that.minorVersion() &&
                majorVersion() == that.majorVersion() &&
                protocolName().equals(that.protocolName());
    }

    @Override
    public int compareTo(SipVersion o) {
        int v = protocolName().compareTo(o.protocolName());
        if (v != 0) {
            return v;
        }

        v = majorVersion() - o.majorVersion();
        if (v != 0) {
            return v;
        }

        return minorVersion() - o.minorVersion();
    }

    void encode(ByteBuf buf) {
        if (bytes == null) {
            buf.writeCharSequence(text, CharsetUtil.US_ASCII);
        } else {
            buf.writeBytes(bytes);
        }
    }
}
