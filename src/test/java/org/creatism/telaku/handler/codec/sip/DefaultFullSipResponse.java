package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * Default implementation of a {@link FullSipResponse}
 */
public class DefaultFullSipResponse extends DefaultSipResponse implements FullSipResponse {

    private final ByteBuf content;

    private int hash;

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status) {
        this(version, status, Unpooled.buffer(0));
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status, ByteBuf content) {
        this(version, status, content, true);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders) {
        this(version, status, Unpooled.buffer(0), validateHeaders, false);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders,
                                  boolean singleFieldHeaders) {
        this(version, status, Unpooled.buffer(0), validateHeaders, singleFieldHeaders);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  ByteBuf content, boolean validateHeaders) {
        this(version, status, content, validateHeaders, false);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  ByteBuf content, boolean validateHeaders, boolean singleFieldHeaders) {
        super(version, status, validateHeaders, singleFieldHeaders);
        this.content = checkNotNull(content, "content");
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  ByteBuf content, SipHeaders headers) {
        super(version, status, headers);
        this.content = checkNotNull(content, "content");
    }


    @Override
    public ByteBuf content() {
        return content;
    }

    @Override
    public FullSipMessage copy() {
        return replace(content().copy());
    }

    @Override
    public FullSipMessage duplicate() {
        return replace(content().duplicate());
    }

    @Override
    public FullSipMessage retainedDuplicate() {
        return replace(content().retainedDuplicate());
    }

    @Override
    public FullSipMessage replace(ByteBuf content) {
        FullSipResponse response = new DefaultFullSipResponse(protocolVersion(), status(), content,
                headers().copy());
        response.setDecoderResult(decoderResult());
        return response;
    }

    @Override
    public int refCnt() {
        return content.refCnt();
    }

    @Override
    public FullSipMessage retain(int increment) {
        content.retain(increment);
        return this;
    }

    @Override
    public FullSipMessage retain() {
        content.retain();
        return this;
    }

    @Override
    public FullSipMessage touch() {
        content.touch();
        return this;
    }

    @Override
    public FullSipMessage touch(Object hint) {
        content.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return content.release();
    }

    @Override
    public boolean release(int decrement) {
        return content.release(decrement);
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            if (ByteBufUtil.isAccessible(content())) {
                try {
                    hash = 31 + content().hashCode();
                } catch (IllegalReferenceCountException ignored) {
                    // Handle race condition between checking refCnt() == 0 and using the object.
                    hash = 31;
                }
            } else {
                hash = 31;
            }
            hash = 31 * hash + super.hashCode();
            this.hash = hash;
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultFullSipResponse)) {
            return false;
        }

        DefaultFullSipResponse other = (DefaultFullSipResponse) o;

        return super.equals(other) &&
                content().equals(other.content());
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
    }
}
