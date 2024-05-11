package org.creatism.telaku.handler.codec.sip;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * the default {@link SipRequest} implementation
 */
public class DefaultSipRequest extends DefaultSipMessage implements SipRequest {
    private static final int HASH_CODE_PRIME = 63;
    private SipMethod method;
    private String uri;

    public DefaultSipRequest(SipVersion version, SipMethod method, String uri) {
        this(version, method, uri, true);
    }

    public DefaultSipRequest(SipVersion version, SipMethod method, String uri, boolean validateHeaders) {
        super(version, validateHeaders, false);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
    }

    /**
     * Creates a new instance
     * @param version the SIP version of the request
     * @param method the SIP method of the request
     * @param uri the URI or path of the request
     * @param headers the Headers for this Request
     */
    public DefaultSipRequest(SipVersion version, SipMethod method, String uri, SipHeaders headers) {
        super(version, headers);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
    }

    @Override
    public SipMethod method() {
        return method;
    }

    @Override
    public SipRequest setMethod(SipMethod method) {
        this.setMethod(method);
        return this;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public SipRequest setUri(String uri) {
        this.setUri(uri);
        return this;
    }

    @Override
    public SipRequest setProtocolVersion(SipVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + method.hashCode();
        result = HASH_CODE_PRIME * result + uri.hashCode();
        result = HASH_CODE_PRIME * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipRequest)) {
            return false;
        }

        DefaultSipRequest other = (DefaultSipRequest) o;

        return method().equals(other.method()) &&
                uri().equalsIgnoreCase(other.uri()) &&
                super.equals(o);
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendRequest(new StringBuilder(256), this).toString();
    }
}
