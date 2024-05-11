package org.creatism.telaku.handler.codec.sip;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;

public class DefaultSipObject implements SipObject{
    private static final int HASH_CODE_PRIME = 63;
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    protected DefaultSipObject() {
        // Disallow direct instantiation
    }

    @Override
    public DecoderResult decoderResult() {
        return decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
        this.decoderResult = ObjectUtil.checkNotNull(decoderResult, "decoderResult");
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + decoderResult.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipObject)) {
            return false;
        }

        DefaultSipObject other = (DefaultSipObject) o;

        return decoderResult().equals(other.decoderResult());
    }
}
