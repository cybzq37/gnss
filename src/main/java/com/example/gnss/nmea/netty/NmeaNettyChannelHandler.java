package com.example.gnss.nmea.netty;

import com.example.gnss.configuration.NmeaProperties;
import com.example.gnss.nmea.GsvParser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class NmeaNettyChannelHandler extends SimpleChannelInboundHandler<String> {

    private NmeaNettyTcpClient nmeaNettyTcpClient;
    private NmeaProperties nmeaProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) {
          // 数据采集+解析
//        IHtSignalService iHtSignalService = SpringUtil.getBean(HtSignalServiceImpl.class);
//        iHtSignalService.parseThenSaveRealTimeMessage(bytes, hongTangProperties);
        if(msg.contains("GSV")) {
            GsvParser.parser(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NMEA {} {}:{} 协议解析错误：{}", nmeaProperties.getName(),
                nmeaProperties.getHost(),
                nmeaProperties.getPort(), cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("NMEA {} {}:{} 协议尝试重连", nmeaProperties.getName(),
                nmeaProperties.getHost(), nmeaProperties.getPort());
        ctx.channel().eventLoop().schedule(() -> nmeaNettyTcpClient.connect(), 60L, TimeUnit.SECONDS); // 尝试重连
        super.channelInactive(ctx);
    }
}
