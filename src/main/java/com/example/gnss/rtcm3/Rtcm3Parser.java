package com.example.gnss.rtcm3;

import com.example.gnss.util.ByteUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Rtcm3Parser {

    private int type;
    private int stationId;

    public static void parse(byte[] data) {
        String bits = ByteUtils.bytesToBits(data);
        int type = ByteUtils.readBitsAsInt(bits, 0, 12);
        int station = ByteUtils.readBitsAsInt(bits, 12, 12);
        log.info("type: {}, station: {}", type, station);

        /**
         * 信息类型编号中前三位 107 表示 GPS，108 表示 GLONASS，109 表示 GALILEO， 111 表示 QZSS，112 表示 BDS；
         * 最后一位表示数据类型的内容种类 1~7。
         */
        if(type == 1005) { //基站基准位置数据

        } else if(type == 1007) { //天线描述符和ID

        } else if(type == 1019) { //GPS星历

        } else if(type == 1020) { //格洛纳斯星历

        } else if(type == 1033) { //接收机及天线描述

        } else if(type == 1042) { //北斗星历

        } else if(type == 1045) { //伽利略星历

        } else if(type == 1046) { //伽利略星历

        } else if(type == 1074) { //GPS伪距载波相位和载噪比
            int tow = ByteUtils.readBitsAsInt(bits, 24, 30);
//            int msm = ByteUtils.readBitsAsInt(bits, 54, 1);
//            int iods = ByteUtils.readBitsAsInt(bits, 55, 3);
//            int reserve = ByteUtils.readBitsAsInt(bits, 58, 7);
//            int flag1 = ByteUtils.readBitsAsInt(bits, 65, 2);
//            int flag2 = ByteUtils.readBitsAsInt(bits, 67, 2);
//            int flag3 = ByteUtils.readBitsAsInt(bits, 69, 1);
//            int flag4 = ByteUtils.readBitsAsInt(bits, 70, 3);
//            int satelliteMask = ByteUtils.readBitsAsInt(bits, 73, 64);
            String nSatBits = bits.substring(73, 73 + 64); // 验证正确
            log.info("station: {}, GPS: {}", station, nSatBits);



        } else if(type == 1084) { //全GLONASS伪距和载波相位加信号强度 Russia
            String nSatBits = bits.substring(73, 73 + 64);
            log.info("station: {}, glonass sat: {}", station, nSatBits);
        } else if(type == 1094) { //全伽利略伪距和载波相位加信号强度 Eur
            String nSatBits = bits.substring(73, 73 + 64);
            log.info("station: {}, jll sat: {}", station, nSatBits);
        } else if(type == 1124) { //全北斗伪距和载波相位加信号强度
            String nSatBits = bits.substring(73, 73 + 64); // 验证正确
            log.info("station: {}, BDS: {}", station, nSatBits);  // China
        } else if(type == 1230) { //GLONASS相位偏差

        }

    }
}
