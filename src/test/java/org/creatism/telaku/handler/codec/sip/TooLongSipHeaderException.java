package org.creatism.telaku.handler.codec.sip;


import io.netty.handler.codec.TooLongFrameException;

/**
 * An {@link TooLongFrameException} which is thrown when the length of the
 * header decoded is greater than the allowed maximum.
 */
public final class TooLongSipHeaderException extends TooLongFrameException {

    private static final long serialVersionUID = -8336051981929869862L;

    /**
     * Creates a new instance.
     */
    public TooLongSipHeaderException() {
    }

    /**
     * Creates a new instance.
     */
    public TooLongSipHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public TooLongSipHeaderException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public TooLongSipHeaderException(Throwable cause) {
        super(cause);
    }
}
