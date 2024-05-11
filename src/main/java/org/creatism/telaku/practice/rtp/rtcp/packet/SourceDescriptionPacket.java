package org.creatism.telaku.practice.rtp.rtcp.packet;

import org.creatism.telaku.practice.rtp.NettyProtocol;
import org.creatism.telaku.practice.rtp.RtcpPacketTypes;
import org.creatism.telaku.practice.util.ByteArrayUtil;

import java.nio.charset.StandardCharsets;

public class SourceDescriptionPacket extends NettyProtocol {
    private static final byte[] HEADER = new byte[]{ DEF_HEADER, RtcpPacketTypes.SD.getCode()};
    public SourceDescriptionPacket(int ssrc, String cname) {
        byte[] lengthBytes = ByteArrayUtil.shortToByteArray((short)6);
        byte[] ssrcBytes = ByteArrayUtil.intToByteArray(ssrc);

        byte[] cnameBytes = cname.getBytes(StandardCharsets.UTF_8);
        byte len = (byte) (cnameBytes.length + 2);
        byte[] sdesTypeAndLen = new byte[]{1, len};
        byte[] endBytes = new byte[]{0, 0};
        bytes = ByteArrayUtil.concatByteArrays(HEADER, lengthBytes, ssrcBytes , sdesTypeAndLen, cnameBytes, endBytes);
    }
}
