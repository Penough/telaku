package org.creatism.telaku.handler.codec.sip;

public class SipRequestDecoder extends SipObjectDecoder{
    protected SipRequestDecoder(int maxInitialLineLength, int maxHeaderSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths) {
        super(maxInitialLineLength, maxHeaderSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }

    @Override
    protected SipMessage createMessage(String[] initialLine) throws Exception {
        return null;
    }

    @Override
    protected SipMessage createInvalidMessage() {
        return null;
    }
}
