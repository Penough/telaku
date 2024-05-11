package org.creatism.telaku.practice.util;

import java.util.concurrent.ThreadLocalRandom;

public class SdpUtil {
    /**
     * randomly generate a 16 bits hex
     * @return
     */
    public static String generateCname(){
        StringBuilder cname = new StringBuilder();
        cname.append(Integer.toHexString(ThreadLocalRandom.current().nextInt(15)+1));
        for (int i = 1; i < 16; i++) {
            cname.append(Integer.toHexString(ThreadLocalRandom.current().nextInt(16)));
        }
        return cname.toString();
    }

    public static int generateSsrc(){
        return ThreadLocalRandom.current().nextInt(0x7a0a1eff)+0x5f5e100;
    }

}
