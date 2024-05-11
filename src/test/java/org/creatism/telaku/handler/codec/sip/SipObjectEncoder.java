package org.creatism.telaku.handler.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AsciiString;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.creatism.telaku.handler.codec.sip.SipConstants.CR;
import static org.creatism.telaku.handler.codec.sip.SipConstants.LF;

/**
 * Sip Object Encoder
 * tips:
 * SIP doesn't support chunked trunsfer.
 * @param <H>
 */
public abstract class SipObjectEncoder<H extends SipMessage> extends MessageToMessageEncoder<Object> {
    static final int CRLF_SHORT = (CR << 8) | LF;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf buf = null;
        if(msg instanceof SipMessage) {
            @SuppressWarnings({ "unchecked", "CastConflictsWithInstanceof" })
            H m = (H) msg;
            addViaHeader(ctx.channel(), (SipMessage)msg);
            buf = ctx.alloc().buffer();
            encodeInitialLine(buf, m);
            encodeHeaders(m.headers(), buf);
            ByteBufUtil.writeShortBE(buf, CRLF_SHORT);
        }
        if (msg instanceof SipContent || msg instanceof ByteBuf) {
            final long contentLength = contentLength(msg);
            if (contentLength > 0) {
                if (buf != null && buf.writableBytes() >= contentLength && msg instanceof SipContent) {
                    // merge into other buffer for performance reasons
                    buf.writeBytes(((SipContent) msg).content());
                    out.add(buf);
                } else {
                    if (buf != null) {
                        out.add(buf);
                    }
                    out.add(encodeAndRetain(msg));
                }
            }
        } else if (buf != null) {
            out.add(buf);
        }
    }

    /**
     * Encode request line
     */
    protected abstract void encodeInitialLine(ByteBuf buf, H message) throws Exception;

    /**
     * add a via Header by transfer protocol
     */
    protected void addViaHeader(Channel channel, SipMessage msg) {
        if(msg.headers().get(SipHeaderNames.VIA) != null) return;
        InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
        AsciiString transferProtocol = channel instanceof DatagramChannel?SipHeaderValues.UDP: SipHeaderValues.TCP;
        // todo need to create branch for this communication,maybe kill it by a TransactionManager
        SipHeaderUtil.addVia(msg, address.getHostString(), address.getPort(), String.valueOf(transferProtocol), null);
    }

    /**
     * Encode the {@link SipHeaders} into a {@link ByteBuf}.
     */
    protected void encodeHeaders(SipHeaders headers, ByteBuf buf) {
        Iterator<Map.Entry<CharSequence, CharSequence>> iter = headers.iteratorCharSequence();
        while (iter.hasNext()) {
            Map.Entry<CharSequence, CharSequence> header = iter.next();
            SipHeadersEncoder.encoderHeader(header.getKey(), header.getValue(), buf);
        }
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof SipObject || msg instanceof ByteBuf;
    }

    private static Object encodeAndRetain(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).retain();
        }
        if (msg instanceof SipContent) {
            return ((SipContent) msg).content().retain();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }

    private static long contentLength(Object msg) {
        if (msg instanceof SipContent) {
            return ((SipContent) msg).content().readableBytes();
        }
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }
}
