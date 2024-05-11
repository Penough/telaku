package org.creatism.telaku.practice.util;

/**
 * Alaw algorithm util
 * <p>
 *     The util just tries to encode and decode binary file/stream by alaw algorithm.
 *     Alaw algorithm compression ratio is 50%.Of course is a kind of lossy compression algorithm.
 *     It try to compress 16 bits to 8 bits.It looks like transfer a short type number to byte.
 * </p>
 * <p>
 *     Caculation process:
 *     <ul>
 *         <li>If the short type number is negtive, you should inverse it at first.</li>
 *         <li>Divide the number into two parts namely high 8 bits and low 8 bits.</li>
 *         <li>
 *             Sampling intensity bits(3 bits) after sign bit.All operations are completed in range rare 7 bits.
 *             0000000 is replaced with 000<br>
 *             0000001 is replaced by 001<br>
 *             000001x is replaced by 010<br>
 *             00001xx is replaced by 011<br>
 *             ...(and so on)
 *         </li>
 *         <li>
 *             Sampling the sample bits(4 bits).The series of bits is the next 4 bits that follow the
 *             intensity bits range namely 'x' in last step.It may be sampled in low 8 bits if there is enough bits in
 *             remained high bits.
 *         </li>
 *         <li>
 *             Finally inverse the even bits and the sign bit.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     Because of the loss when compression,it will be discovered that it's a not lossless algorithm on decoding progress.
 * </p>
 * @author penough
 * @since 0.0.1
 */
public class Alaw {

    public static byte[] encode(byte[] bytes) {
        if(bytes.length%2!=0) throw new RuntimeException("Unresolvable bytes length...");
        byte[] encrypt = new byte[bytes.length/2];
        byte[] pcmBytes = new byte[2];
        for (int i = 0; i < encrypt.length; i++) {
            System.arraycopy(bytes, i*2, pcmBytes, 0, 2);
            byte encoded = encodePcmCode(pcmBytes);
            encrypt[i] = encoded;
        }
        return encrypt;
    }

    public static byte encodePcmCode(byte[] pcmBytes) {
        if(pcmBytes.length != 2)
            throw new RuntimeException("Unresolvable pcm code length...");
        byte high = pcmBytes[0];
        byte low = pcmBytes[1];
        // get sign bit
        int sign = high&0x80;
        if(sign!=0) {
            int tmp = -((high&0xff)<<8 | (low&0xff));
            low = (byte)(tmp&0xf);
            high = (byte)((tmp>>8)&0xf);
        };
        // eliminate sign bit effect and sample the intensity bits.
        int unsigned = high&0x7f;
        byte strength = 0;
        while(unsigned != 0) {
            unsigned = (byte)(unsigned>>1);
            strength++;
        }
        // merge sign bit and intensity bits
        byte cmb = (byte)((strength<<4)|sign);
        // 计算采样位
        int sho = ((high<<8)&0xff00) | (low&0xff);
        int rMov = 4 + (strength>1?strength-1:0);
        int smp = sho >> rMov & 0xf;
        cmb = (byte) (cmb|smp);
        // inverse even bits and sign bit
        return (byte)(cmb^0xd5);
    }

    public static byte[] decode(byte[] encrypt) {
        byte[] buffer = new byte[encrypt.length*2];
        for (int i = 0; i < encrypt.length; i++) {
            byte[] decode = decode(encrypt[i]);
            buffer[2*i] = decode[0];
            buffer[2*i+1] = decode[1];
        }
        return buffer;
    }

    public static byte[] decode(byte encoded){
        // restore the alaw code
        byte cmb = (byte)(encoded^0xd5);
        // get sign bit
        int sign = cmb&0x80;
        // get intensity bits
        int exp = (cmb&0x70)>>4;
        // get sampling bits
        int smp = cmb & 0xf;
        smp <<= 4;
        smp += 8;
        if(exp != 0 ) smp += 0x100;
        if(exp > 1) smp <<= (exp-1);
        short data = (short)(sign==0?smp:-smp);
        return ByteArrayUtil.shortToByteArray(data);
    }

//    public static void main(String[] args) throws IOException {
//        long cur = System.currentTimeMillis();
//        File file = new File("C:\\MyFolder\\audio");
//        FileInputStream inputStream = new FileInputStream(file);
//        File pcm = new File("C:\\MyFolder\\audio-common.pcm");
//        FileOutputStream outputStream = new FileOutputStream(pcm);
//        byte[] encrypt = new byte[inputStream.available()];
//        inputStream.read(encrypt);
//        byte[] decode = decode(encrypt);
//        outputStream.write(decode);
//        outputStream.flush();
//        inputStream.close();
//        outputStream.close();
//        long now = System.currentTimeMillis();
//        System.err.println(now-cur);
//
//        cur = System.currentTimeMillis();
//        FileInputStream pcmIs = new FileInputStream(pcm);
//        File enc = new File("C:\\MyFolder\\audio-common-rec.enc");
//        FileOutputStream encOut = new FileOutputStream(enc);
//        byte[] pcmBuffer = new byte[pcmIs.available()];
//        pcmIs.read(pcmBuffer);
//        byte[] encBuffer = encode(pcmBuffer);
//        encOut.write(encBuffer);
//        encOut.flush();
//        pcmIs.close();
//        encOut.close();
//        now = System.currentTimeMillis();
//        System.err.println(now-cur);
//
//
//        cur = System.currentTimeMillis();
//        FileInputStream encIn = new FileInputStream(enc);
//        File out = new File("C:\\MyFolder\\audio-common-rec-out.pcm");
//        FileOutputStream outs = new FileOutputStream(out);
//        byte[] encbb = new byte[encIn.available()];
//        encIn.read(encbb);
//        byte[] dec = decode(encbb);
//        outs.write(dec);
//        outs.flush();
//        encIn.close();
//        outs.close();
//        now = System.currentTimeMillis();
//        System.err.println(now-cur);
//    }
}
