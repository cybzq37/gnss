package com.example.gnss.rtcm3.netty;

import com.example.gnss.configuration.Rtcm3Properties;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Rtcm3NettyTcpClient {

    private Rtcm3Properties rtcm3Properties;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;

    public Rtcm3NettyTcpClient(Rtcm3Properties rtcm3Properties) {
        this.rtcm3Properties = rtcm3Properties;
    }

    /**
     * 初始化 `Bootstrap` 客户端引导程序
     */
    public Bootstrap init(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15*1000);    // 设置超时时间
        bootstrap.group(group)
                .channel(NioSocketChannel.class)//通道连接者
                .handler(new ChannelInitializer<SocketChannel>() { //通道处理者
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("idleStateHandler", new IdleStateHandler(15,0,0, TimeUnit.MINUTES))
                                .addLast(new Rtcm3NettyDecoder())
                                .addLast("clientChannelHandler", new Rtcm3NettyChannelHandler(Rtcm3NettyTcpClient.this, rtcm3Properties));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE,true); // 心跳报活
        return bootstrap;
    }

    /**
     *  建立连接，获取连接通道对象
     */
    public void connect(){
        channelFuture = bootstrap.connect(rtcm3Properties.getHost(), rtcm3Properties.getPort());
        channelFuture.addListener((ChannelFutureListener) f -> {
            if (f != null && f.isSuccess()) {
                log.info("{} rtcm3 tcp服务 {}:{} 连接成功", rtcm3Properties.getName(),
                        rtcm3Properties.getHost(), rtcm3Properties.getPort());
            }else {
                Throwable cause = f.cause();
                log.error("{} rtcm3 tcp服务 {}:{} 连接失败，错误原因：{}", rtcm3Properties.getName(),
                        rtcm3Properties.getHost(), rtcm3Properties.getPort(), cause);
                // 尝试重连
                f.channel().eventLoop().schedule(() -> this.connect(), 30L, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * 向服务器发送消息
     */
    public void sendMessage(byte[] msg) throws InterruptedException {
        if (channelFuture.channel() != null) {
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(msg)).sync();
        }else {
            log.error("消息发送失败，连接尚未建立");
            throw new RuntimeException("消息发送失败，连接尚未建立");
        }
    }
}
