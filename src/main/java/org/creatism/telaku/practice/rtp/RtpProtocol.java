package org.creatism.telaku.practice.rtp;

import org.creatism.telaku.practice.util.ByteArrayUtil;

import java.util.Arrays;

public class RtpProtocol extends NettyProtocol{

    public RtpProtocol(boolean mark, short seqnum, int timeStp, int ssrc){
        byte[] header;
        if(mark) {
            header = new byte[]{(byte) 0x80, (byte)0x88};
        } else {
            header = new byte[]{(byte) 0x80, (byte)0x08};
        }
        byte[] sn = ByteArrayUtil.shortToByteArray(seqnum);
        byte[] stmp = ByteArrayUtil.intToByteArray(timeStp);
        byte[] id = ByteArrayUtil.intToByteArray(ssrc);
        byte[] silence = new byte[160];
        Arrays.fill(silence, (byte)0x55);
        bytes = ByteArrayUtil.concatByteArrays(header, sn, stmp, id, silence);
    }


}
