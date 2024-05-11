package org.creatism.telaku.practice.rtp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;

@Slf4j
@Data
@AllArgsConstructor
public class RtcpHandler extends ChannelInboundHandlerAdapter {

    private String remoteAddr;
    private int port;
    private int ssrc;
    private String cname;
    short seq = 8695;
    int stmp = 160;
    boolean mark = true;
    RtpProtocol rtp;
    File file;
    FileOutputStream outputStream;

    public RtcpHandler(String remoteAddr, int port, int ssrc, String cname) throws FileNotFoundException {
        this.remoteAddr = remoteAddr;
        this.port = port;
        this.ssrc = ssrc;
        this.cname = cname;

        file = new File("C:\\MyFolder\\" + ssrc + "_" + cname);
        outputStream = new FileOutputStream(file);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // todo channel active and send a rctp packet
        log.info("try to send udp package...");
        System.err.println(ctx.channel().remoteAddress());
//        InetSocketAddress rtcpAddress = new InetSocketAddress(remoteAddr, port);

//        ctx.executor().parent().execute(() -> {
//            ctx.writeAndFlush(new DatagramPacket(new Rtcp(ssrc, cname).getByteBuf(), rtcpAddress));
//            log.info("sent...");
//        });
//        Thread.sleep(20);
        InetSocketAddress rtpAddress = new InetSocketAddress(remoteAddr, port-1);

        for (int i = 0; i < 10000; i++) {
            ctx.executor().parent().execute(() -> {
                rtp = new RtpProtocol(mark, seq, stmp, ssrc);
                ctx.writeAndFlush(new DatagramPacket(rtp.getByteBuf(), rtpAddress));
//                log.info("sented:{},{},{},{}",mark, seq, stmp, ssrc);
                mark = false;
                seq++;
                stmp+=60;
            });
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("channelRead"); // 消息传入调用
        ByteBuf frame = ((DatagramPacket)msg).content();
        byte[] a = new byte[frame.readableBytes()];
        frame.readBytes(a);
        // 需要剔除前12个字节，即RTP头部
        byte[] b = new byte[a.length - 12];
        byte[] seqByte = new byte[2];
        System.arraycopy(a, 2, seqByte, 0, 2);
        // 无符号处理
        log.info("seqnum:{}",  (seqByte[0]&0xff)<<8 | (seqByte[1]&0xff));
        System.arraycopy(a, 12, b, 0, b.length);
        outputStream.write(b);
        super.channelRead(ctx, msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        log.info("channelReadComplete"); // 最后一次channelRead
//        byte[] c = new byte[con.size()];
//        for (int i = 0; i < con.size(); i++) {
//            c[i] = con.get(i);
//        }
//        File f = new File("C:\\MyFolder\\audio");
//        f.createNewFile();
//        FileOutputStream out = new FileOutputStream(f);
//        out.write(c);
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public static void main(String[] args) {
//        byte[] a = "test".getBytes(StandardCharsets.UTF_8);
        // 116,101,115,116
//        System.err.println(a.length);
        byte[] s = new byte[]{(byte)0x7e, (byte)0xe5};
            int x = s[0]&0xff;
            System.err.println(x<<8|s[1]);
            System.err.println((s[0]&0xff)<<8 | s[1]&0xff);
            System.err.println(Integer.toBinaryString(s[0]));

    }
}
