package com.iot.nero.middleware.dfs.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/6
 * Time   10:34 AM
 */
public class ByteUtils {

    public static String bytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }

    //注意高低位问题
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static byte[] toByteArray(int i) {
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    public static byte[] toByteArray(Long i) {
        byte[] bt = new byte[8];
        bt[0] = (byte) (0xffL & i);
        bt[1] = (byte) ((0xff00L & i) >> 8);
        bt[2] = (byte) ((0xff0000L & i) >> 16);
        bt[3] = (byte) ((0xff000000L & i) >> 24);
        bt[4] = (byte) ((0xff00000000L & i) >> 32);
        bt[5] = (byte) ((0xff0000000000L & i) >> 40);
        bt[6] = (byte) ((0xff000000000000L & i) >> 48);
        bt[7] = (byte) ((0xff00000000000000L & i) >> 56);
        return bt;
    }

    /**
     * 分隔byte数组
     *
     * @param src
     * @param spin
     * @return
     */
    public static List<byte[]> split(byte[] src, byte spin) {
        List<byte[]> bytes = new ArrayList<>();
        int lastIndex = 0;  // 上一次匹配spin位置
        int num = 0;        // 分隔得到个数

        for (int i = 0; i < src.length; i++) {
            if (src[i] == spin) {
                byte[] dataTmp;
                if (lastIndex == 0) {
                    dataTmp = new byte[i - lastIndex - 1];
                    for (int index = lastIndex; index < i - 1; index++) {
                        dataTmp[index - lastIndex] = src[index];
                    }
                } else {
                    dataTmp = new byte[i - lastIndex - 2];
                    for (int index = lastIndex; index < i - 2; index++) {
                        dataTmp[index - lastIndex] = src[index + 1];
                    }
                }
                lastIndex = i;
                bytes.add(dataTmp);
                num += 1;
            }
        }
        return bytes;
    }


    public static byte[] toPrimitives(Byte[] oBytes)
    {
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; // Autoboxing

        return bytes;
    }

    public static List<Byte> toByteList(byte[] data){
        List<Byte> bytes = new ArrayList<>();

        int i = 0;
        for (byte b : data) bytes.add(b); // Autoboxing

        return bytes;
    }
}
