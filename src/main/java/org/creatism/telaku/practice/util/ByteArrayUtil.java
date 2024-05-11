package org.creatism.telaku.practice.util;

public class ByteArrayUtil {
    public static byte[] longToByteArray(long a) {
        return new byte[] {
                (byte) ((a >> 56) & 0xFF),
                (byte) ((a >> 48) & 0xFF),
                (byte) ((a >> 40) & 0xFF),
                (byte) ((a >> 32) & 0xFF),
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] shortToByteArray(short a) {
        return new byte[] {
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }


    public static byte[] concatByteArrays(byte[]... byteArray) {
        int total = 0;
        for (int i = 0; i < byteArray.length; i++) {
            total += byteArray[i].length;
        }
        int cur = 0;
        byte[] con = new byte[total];
        for (int i = 0; i < byteArray.length; i++) {
            System.arraycopy(byteArray[i], 0, con, cur, byteArray[i].length);
            cur += byteArray[i].length;
        }
        return con;
    }
}
