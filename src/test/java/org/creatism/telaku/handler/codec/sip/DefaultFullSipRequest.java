package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * Default implementation of {@link FullSipRequest}
 */
public class DefaultFullSipRequest extends DefaultSipRequest implements FullSipRequest {
    private final ByteBuf content;

    /**
     * Used to cache the value of the hash code and avoid {@link IllegalReferenceCountException}.
     */
    private int hash;

    public DefaultFullSipRequest(SipVersion version, SipMethod method, String uri) {
        this(version, method, uri, Unpooled.buffer(0));
    }

    public DefaultFullSipRequest(SipVersion version, SipMethod method, String uri, ByteBuf content) {
        this(version, method, uri, content, true);
    }

    public DefaultFullSipRequest(SipVersion version, SipMethod method, String uri, boolean validateHeaders) {
        this(version, method, uri, Unpooled.buffer(0), validateHeaders);
    }

    public DefaultFullSipRequest(SipVersion version, SipMethod method, String uri,
                                  ByteBuf content, boolean validateHeaders) {
        super(version, method, uri, validateHeaders);
        this.content = checkNotNull(content, "content");
    }

    public DefaultFullSipRequest(SipVersion version, SipMethod method, String uri,
                                  ByteBuf content, SipHeaders headers) {
        super(version, method, uri, headers);
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
        return null;
    }

    @Override
    public FullSipMessage replace(ByteBuf content) {
        FullSipRequest request = new DefaultFullSipRequest(protocolVersion(), method(), uri(), content, headers().copy());
        request.setDecoderResult(decoderResult());
        return request;
    }

    @Override
    public FullSipMessage retain(int increment) {
        content.retain(increment);
        return this;
    }

    @Override
    public int refCnt() {
        return content.refCnt();
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
        if(!(o instanceof DefaultFullSipRequest)) return false;
        DefaultFullSipRequest other = (DefaultFullSipRequest) o;
        return super.equals(other) && content().equals(other.content());
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
    }
}
