package org.creatism.telaku.practice.rtp.rtcp.packet;

import org.creatism.telaku.practice.rtp.NettyProtocol;
import org.creatism.telaku.practice.rtp.RtcpPacketTypes;
import org.creatism.telaku.practice.util.ByteArrayUtil;

import java.util.Arrays;

public class ReceiverReportPacket extends NettyProtocol {
    private static final byte[] HEADER = new byte[]{ DEF_HEADER, RtcpPacketTypes.RR.getCode()};
    public ReceiverReportPacket(int ssrc) {
        byte[] lengthBytes = ByteArrayUtil.shortToByteArray((short)7);
        byte[] ssrcBytes = ByteArrayUtil.intToByteArray(ssrc);
        byte[] source = new byte[24];
        Arrays.fill(source, (byte) 0);
        bytes = ByteArrayUtil.concatByteArrays(HEADER, lengthBytes, ssrcBytes , source);
    }
}
