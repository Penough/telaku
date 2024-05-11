package org.creatism.telaku.practice.rtp;


import org.creatism.telaku.practice.configuration.properties.SipProperties;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sip.message.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;

@Slf4j
@Component
public class RtpListener {

    @Resource
    private SipProperties sipProperties;

    @EventListener(RtpEvent.class)
    public void rtpConnect(RtpEvent event) throws InterruptedException, ParseException, SdpException {
        log.info("trigger rtp event...");

        // analysis response for host and port
        Response resp = (Response)event.getData();
        String s = new String(resp.getRawContent());
        log.info("raw content:{}", s);
        SDPAnnounceParser announceParser = new SDPAnnounceParser(s);
        SessionDescription sdp = announceParser.parse();
        String remoteAddr = sdp.getOrigin().getAddress();
        MediaDescription md = (MediaDescription)sdp.getMediaDescriptions(false).get(0);
        int port = md.getMedia().getMediaPort();
        int rtcpPort = ++port;

        // todo send rtcp request for rtp session

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 2048 * 2)
//                .remoteAddress(remoteAddr, rtcpPort)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        channel.pipeline().addLast(new RtcpHandler(remoteAddr, rtcpPort, 115732020, "496652bf768203c1"));

                    }
                });

        Channel channel = bootstrap.bind(4000).channel();
        // todo connnect to rtp server
//        ChannelFuture channelFuture = bootstrap.connect(sipProperties.getHost(), port).sync();
        // listen channel close
        channel.closeFuture().sync();
    }

    /**
     * Byte数组转对象
     * @param bytes
     * @return
     */
    public static Object byteArrayToObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            log.error("byteArrayToObject failed, " + e);
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    log.error("close byteArrayInputStream failed, " + e);
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    log.error("close objectInputStream failed, " + e);
                }
            }
        }
        return obj;
    }
}
