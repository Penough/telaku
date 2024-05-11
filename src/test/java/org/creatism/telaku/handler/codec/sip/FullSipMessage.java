package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;

/**
 * Combines {@link SipMessage} and {@link SipContent} into one
 * message. So it represent a <i>complete</i> http message.
 * <p>
 *     Because SIP doesn't support chunked transfer encoding
 *     refer to section <a href="https://www.rfc-editor.org/rfc/rfc3261.html#section-7.4.2">RFC 3261, 7.4.2</a>,
 *     we needn't LastSipContent with trailing headers like HTTP.
 * </p>
 */
public interface FullSipMessage extends SipMessage, SipContent {
    @Override
    FullSipMessage copy();

    @Override
    FullSipMessage duplicate();

    @Override
    FullSipMessage retainedDuplicate();

    @Override
    FullSipMessage replace(ByteBuf content);

    @Override
    FullSipMessage retain(int increment);

    @Override
    FullSipMessage retain();

    @Override
    FullSipMessage touch();

    @Override
    FullSipMessage touch(Object hint);
}
