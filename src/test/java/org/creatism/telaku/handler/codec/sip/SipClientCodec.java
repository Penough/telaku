package org.creatism.telaku.handler.codec.sip;

import io.netty.channel.CombinedChannelDuplexHandler;

public class SipClientCodec extends CombinedChannelDuplexHandler<SipResponseDecoder, SipRequestEncoder> {
    public SipClientCodec() {
        this(SipObjectDecoder.DEFAULT_MAX_INITIAL_LINE_LENGTH, SipObjectDecoder.DEFAULT_MAX_HEADER_SIZE, SipObjectDecoder.DEFAULT_VALIDATE_HEADERS, SipObjectDecoder.DEFAULT_INITIAL_BUFFER_SIZE, SipObjectDecoder.DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS);
    }

    public SipClientCodec(int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths) {
        init(new SipResponseDecoder(maxInitialLineLength, maxHeaderSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths), new SipRequestEncoder());
    }
}
