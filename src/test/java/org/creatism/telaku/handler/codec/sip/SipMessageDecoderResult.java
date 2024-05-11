package org.creatism.telaku.handler.codec.sip;

import io.netty.handler.codec.DecoderResult;

public class SipMessageDecoderResult extends DecoderResult {

    private final int initialLineLength;
    private final int headerSize;

    SipMessageDecoderResult(int initialLineLength, int headerSize) {
        super(SIGNAL_SUCCESS);
        this.initialLineLength = initialLineLength;
        this.headerSize = headerSize;
    }

    /**
     * The decoded initial line length (in bytes), as controlled by {@code maxInitialLineLength}.
     */
    public int initialLineLength() {
        return initialLineLength;
    }

    /**
     * The decoded header size (in bytes), as controlled by {@code maxHeaderSize}.
     */
    public int headerSize() {
        return headerSize;
    }

    /**
     * The decoded initial line length plus the decoded header size (in bytes).
     */
    public int totalSize() {
        return initialLineLength + headerSize;
    }
}
