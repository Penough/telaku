package org.creatism.telaku.handler.codec.sip;

import io.netty.util.internal.ObjectUtil;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

public abstract class DefaultSipMessage extends DefaultSipObject implements SipMessage {
    private static final int HASH_CODE_PRIME = 63;
    private SipVersion version;
    private final SipHeaders headers;

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version) {
        this(version, true, false);
    }

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version, boolean validateHeaders, boolean singleFieldHeaders) {
        this(version,
                singleFieldHeaders ? new CombinedSipHeaders(validateHeaders)
                        : new DefaultSipHeaders(validateHeaders));
    }

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version, SipHeaders headers) {
        this.version = checkNotNull(version, "version");
        this.headers = checkNotNull(headers, "headers");
    }

    @Override
    public SipHeaders headers() {
        return headers;
    }

    @Override
    public SipVersion protocolVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + headers.hashCode();
        result = HASH_CODE_PRIME * result + version.hashCode();
        result = HASH_CODE_PRIME * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipMessage)) {
            return false;
        }

        DefaultSipMessage other = (DefaultSipMessage) o;

        return headers().equals(other.headers()) &&
                protocolVersion().equals(other.protocolVersion()) &&
                super.equals(o);
    }

    @Override
    public SipMessage setProtocolVersion(SipVersion version) {
        this.version = ObjectUtil.checkNotNull(version, "version");
        return this;
    }
}
