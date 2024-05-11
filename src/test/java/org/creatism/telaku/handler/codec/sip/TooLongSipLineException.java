package org.creatism.telaku.handler.codec.sip;

import io.netty.handler.codec.TooLongFrameException;

/**
 * An {@link TooLongFrameException} which is thrown when the length of the line decoded is greater than the allowed maximum.
 */
public class TooLongSipLineException extends TooLongFrameException {

    private static final long serialVersionUID = -3469006706246299056L;

    public TooLongSipLineException() {}

    public TooLongSipLineException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TooLongSipLineException(String msg) {
        super(msg);
    }

    public TooLongSipLineException(Throwable cause) {
        super(cause);
    }
}
