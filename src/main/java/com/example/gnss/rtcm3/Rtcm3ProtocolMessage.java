package com.example.gnss.rtcm3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rtcm3ProtocolMessage {

    private final byte syn;
    private final int length;
    private final byte[] data;
    private final byte[] crc;

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
