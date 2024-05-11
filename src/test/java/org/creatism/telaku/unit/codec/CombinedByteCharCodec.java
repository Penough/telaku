package org.creatism.telaku.unit.codec;

import org.creatism.telaku.unit.decoder.ByteToCharDecoder;
import org.creatism.telaku.unit.encoder.CharToByteEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

public class CombinedByteCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CombinedByteCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
}
