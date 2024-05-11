package org.creatism.telaku.practice.configuration;

import org.creatism.telaku.practice.configuration.handler.TcpDecoderHandler;
import org.creatism.telaku.practice.configuration.handler.UdpDecoderHandler;
import org.creatism.telaku.practice.configuration.properties.TransferLayerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;
import reactor.netty.udp.UdpServer;

import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@Configuration
public class TransferServerConfiguration {

    @Resource
    TransferLayerProperties properties;

    /**
     * use CommandLineRunner to init tcp/udp server after springboot application initialization.
     * @param udpDecoderHandler
     * @param tcpDecoderHandler
     * @return
     */
    @Bean
    CommandLineRunner serverRunner(UdpDecoderHandler udpDecoderHandler, TcpDecoderHandler tcpDecoderHandler) {
        return strings -> {
//            createUdpServer(udpDecoderHandler, properties.getRtpServerPort());
//            createUdpServer(udpDecoderHandler, properties.getRtcpServerPort());
            createTcpServer(tcpDecoderHandler);
        };
    }

    /**
     * 创建UDP Server
     * @param udpDecoderHandler： 解析UDP Client上报数据handler
     */
    private void createUdpServer(UdpDecoderHandler udpDecoderHandler, int serverPort) {
        UdpServer.create()
                .handle((in,out) -> {
                    in.receive()
                            .asByteArray()
                            .subscribe();
                    return Flux.never();
                })
                .port(serverPort)
                .doOnBound(conn -> conn.addHandlerLast("decoder",udpDecoderHandler)) //可以添加多个handler
                .bindNow(Duration.ofSeconds(30));
        log.info("udp server initialized, port:{}", serverPort);
    }

    /**
     * 创建TCP Server
     * @param tcpDecoderHandler： 解析TCP Client上报数据的handler
     */
    private void createTcpServer(TcpDecoderHandler tcpDecoderHandler) {
        TcpServer.create()
                .handle((in,out) -> {
                    in.receive()
                            .asByteArray()
                            .subscribe();
                    return Flux.never();

                })
                .doOnConnection(conn ->
                        conn.addHandlerFirst(tcpDecoderHandler)) //实例只写了如何添加handler,可添加delimiter，tcp生命周期，decoder，encoder等handler
                .port(properties.getTcpServerPort())
                .bindNow();
        log.info("tcp server initialized, port:{}", properties.getTcpServerPort());
    }
}
