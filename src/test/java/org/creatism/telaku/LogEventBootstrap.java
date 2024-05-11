package org.creatism.telaku;

import org.creatism.telaku.unit.encoder.LogEventEncoder;
import org.creatism.telaku.unit.pojo.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * this bootstrap uses {@link LogEventEncoder}
 * broadcast the udp msg to the specific port.
 *
 */
public class LogEventBootstrap {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBootstrap(InetSocketAddress address, File file){
        this.group = new NioEventLoopGroup();
        this.file = file;
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class) // with udp channel
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
    }

    public void run() throws Exception {
        Channel ch = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        for(;;) {
            long len = file.length();
            if(len < pointer) {
                pointer = len;
            } else if (len > pointer) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e ) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    /**
     * launch the client;
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // use 255.255.255.255 broadcast ip, otherwise local ip like ::0 will be used.
        LogEventBootstrap bootstrap = new LogEventBootstrap(
                new InetSocketAddress("255.255.255.255",4102), new File(""));
        try {
            bootstrap.run();
        } finally {
            bootstrap.stop();
        }
    }
}
