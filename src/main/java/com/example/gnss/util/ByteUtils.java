package com.example.gnss.util;

public class ByteUtils {

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return byteArray;
    }


    public static String bytesToBits(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (byte b : data) {
            for (int i = 7; i >= 0; i--) {
                result.append((b & (1 << i)) != 0 ? '1' : '0');
            }
        }
        return result.toString();
    }

    public static int readBitsAsInt(String bitString, int startIndex, int length) {
        // 验证参数合法性
        if (bitString == null || startIndex < 0 || length <= 0 || startIndex + length > bitString.length()) {
            throw new IllegalArgumentException("参数非法：请检查比特字符串、索引或长度是否正确");
        }
        // 提取指定范围的比特子串
        String subBits = bitString.substring(startIndex, startIndex + length);
        // 将比特子串转换为整数
        return Integer.parseInt(subBits, 2);
    }

    public static long readBitsAsLong(String bitString, int startIndex, int length) {
        // 验证参数合法性
        if (bitString == null || startIndex < 0 || length <= 0 || startIndex + length > bitString.length()) {
            throw new IllegalArgumentException("参数非法：请检查比特字符串、索引或长度是否正确");
        }
        // 提取指定范围的比特子串
        String subBits = bitString.substring(startIndex, startIndex + length);
        // 将比特子串转换为整数
        return Long.parseLong(subBits, 2);
    }
}
