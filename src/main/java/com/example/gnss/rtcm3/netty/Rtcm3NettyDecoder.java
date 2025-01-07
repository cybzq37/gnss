package com.example.gnss.rtcm3.netty;

import com.example.gnss.rtcm3.Rtcm3ProtocolMessage;
import com.example.gnss.util.CRC24;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Rtcm3NettyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否至少有一个完整的包头
        // 同步码8bit一个字节 保留位6bit和信息长度10bit占用2个字节
        // 头部至少3个字节
        if (in.readableBytes() < 3) {
            return; // 不够数据，等待更多数据
        }

        int markedReaderIndex = in.readerIndex();
        in.markReaderIndex(); // 标记当前读取位置

        // 读取同步码（8个bit，即1字节）
        byte syncCode = in.readByte();
        if (syncCode != (byte) 0b11010011) {
            // 如果同步码不正确，则丢弃此字节并重新读取
            in.resetReaderIndex(); // 回到标记位置
            in.readByte(); //读取（丢弃）当前字节
            return;
        }
        // 读取保留位和信息长度（共2个字节）
        short reserved = in.readShort(); //
        int high6Bits = (reserved >> 10) & 0b111111; // 提取高 6 位（右移 10 位）
        if (high6Bits != 0) { // 检查高6位是否为0
            return; // 保留位错误，丢弃数据
        }
        // 保留位前6个bit为0，所以这两个字节就是信息长度
        int dataLength = reserved; // 信息长度以字节为单位
        // 检查剩余数据是否足够
        if (in.readableBytes() < dataLength + 3) { // 3 字节 CRC
            in.resetReaderIndex();
            return; // 等待更多数据
        }
        // 读取数据信息
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // 获取从标记位置到当前位置的字节数组
        int bytesFromMarkToCurrent = in.readerIndex() - markedReaderIndex;
        byte[] markedToCurrentBytes = new byte[bytesFromMarkToCurrent];
        in.getBytes(markedReaderIndex, markedToCurrentBytes);
        byte[] crc24 = CRC24.checksum(markedToCurrentBytes);

        // 读取 CRC 校验位（24个bit，即3字节）
        byte[] crc = new byte[3];
        in.readBytes(crc);

        // 检查校验位
        if(crc[0] != crc24[0] || crc[1] != crc24[1] || crc[2] != crc24[2]) {
            return;
        }

        // 将解码结果添加到输出列表中
        out.add(new Rtcm3ProtocolMessage(syncCode, dataLength, data, crc));
    }
}