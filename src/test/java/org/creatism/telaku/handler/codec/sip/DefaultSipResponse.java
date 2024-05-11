package org.creatism.telaku.handler.codec.sip;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * The default {@link SipResponse} implementation
 */
public class DefaultSipResponse extends DefaultSipMessage implements SipResponse {

    private SipResponseStatus status;

    /**
     * Creates a new instance
     * @param version the SIP version of this response
     * @param status the status of this response
     */
    public DefaultSipResponse(SipVersion version, SipResponseStatus status) {
        this(version, status, true, false);
    }

    /**
     * Creates a new instance.
     * @param version the SIP version of this response
     * @param status the status of this response
     * @param validateHeaders validate the header names and values when adding them to the SipHeaders
     */
    public DefaultSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders) {
        this(version, status, validateHeaders, false);
    }

    /**
     * Create a new instance
     * @param version the SIP version of this response
     * @param status the status of this response
     * @param validateHeaders validate the header names and values when adding them to the {@link SipHeaders}
     * @param singleFieldHeaders {@code true} to check and enforce that headers with the same name are appended
     * to the same entry and comma separated.
     * See <a href="https://tools.ietf.org/html/rfc3261#section-7.3">RFC 3261, 7.3</a>.
     * {@code false} to allow multiple header entries with the same name to
     *  coexist.
     */
    public DefaultSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
        super(version, validateHeaders, singleFieldHeaders);
        this.status = checkNotNull(status, "status");
    }

    /**
     * Creates a new instance
     * @param version the SIP version of this response
     * @param status the status of this response
     * @param headers the headers for this SIP Response
     */
    public DefaultSipResponse(SipVersion version, SipResponseStatus status, SipHeaders headers) {
        super(version, headers);
        this.status = checkNotNull(status, "status");
    }

    @Override
    public SipResponse setProtocolVersion(SipVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public SipResponseStatus status() {
        return status;
    }

    @Override
    public SipResponse setStatus(SipResponseStatus status) {
        this.status = checkNotNull(status, "status");
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + status.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof DefaultSipResponse)) {
            return false;
        }
        DefaultSipResponse other = (DefaultSipResponse) o;
        return status.equals(other.status()) && super.equals(o);
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendResponse(new StringBuilder(256), this).toString();
    }
}
