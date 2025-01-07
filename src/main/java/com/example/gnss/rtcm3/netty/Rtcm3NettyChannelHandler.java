package com.example.gnss.rtcm3.netty;

import com.example.gnss.rtcm3.Rtcm3Parser;
import com.example.gnss.configuration.Rtcm3Properties;
import com.example.gnss.rtcm3.Rtcm3ProtocolMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class Rtcm3NettyChannelHandler extends SimpleChannelInboundHandler<Rtcm3ProtocolMessage> {

    private Rtcm3NettyTcpClient rtcm3NettyTcpClient;
    private Rtcm3Properties rtcm3Properties;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Rtcm3ProtocolMessage msg) {
          // 数据采集+解析
//        IHtSignalService iHtSignalService = SpringUtil.getBean(HtSignalServiceImpl.class);
//        iHtSignalService.parseThenSaveRealTimeMessage(bytes, hongTangProperties);
        Rtcm3Parser.parse(msg.getData());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("rtcm3 {} {}:{} 协议解析错误：{}", rtcm3Properties.getName(),
                rtcm3Properties.getHost(),
                rtcm3Properties.getPort(), cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("rtcm3 {} {}:{} 协议尝试重连", rtcm3Properties.getName(),
                rtcm3Properties.getHost(), rtcm3Properties.getPort());
        ctx.channel().eventLoop().schedule(() -> rtcm3NettyTcpClient.connect(), 60L, TimeUnit.SECONDS); // 尝试重连
        super.channelInactive(ctx);
    }
}
