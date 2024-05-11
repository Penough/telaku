package org.creatism.telaku.practice.rtp.rtcp;

import org.creatism.telaku.practice.rtp.NettyProtocol;
import org.creatism.telaku.practice.rtp.rtcp.packet.ReceiverReportPacket;
import org.creatism.telaku.practice.util.ByteArrayUtil;
import org.creatism.telaku.practice.rtp.rtcp.packet.SourceDescriptionPacket;

import java.util.ArrayList;

public class Rtcp extends NettyProtocol {
    // rtp headers collection
    private ArrayList<byte[]> packetHeaders;
    public Rtcp(int ssrc, String cname) {
        bytes = ByteArrayUtil.concatByteArrays(new ReceiverReportPacket(ssrc).getBytes(),
                new SourceDescriptionPacket(ssrc, cname).getBytes());
    }
}
