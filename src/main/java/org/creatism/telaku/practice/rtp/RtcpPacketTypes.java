package org.creatism.telaku.practice.rtp;

import lombok.Getter;

@Getter
public enum RtcpPacketTypes {
    RR((byte)0xc9),// 201
    SD((byte)0xca);// 202
    byte code;
    RtcpPacketTypes(byte code) {
        this.code = code;
    }
}
