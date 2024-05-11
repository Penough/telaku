package org.creatism.telaku;

public class Tes {

    public static void main(String[] args) {
        String s1 = "com.akulaku.telaku.handler.codec.sip.SipMethod@7d29cd00";
        String s2 = "com.akulaku.telaku.handler.codec.sip.SipMethod@5264ad30";
        System.err.println(s1.hashCode());
        System.err.println(s2.hashCode());
        System.err.println(s1.hashCode()>>>6 & 15);
        System.err.println(s2.hashCode()>>>6 & 15);
    }
}
