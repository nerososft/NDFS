package com.iot.nero.middleware.dfs.common.utils;

import java.security.MessageDigest;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/29
 * Time   2:05 PM
 */
public class MD5Utils {

    public static  byte[] toMD5Bytes(byte[] s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(s);

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static  String toMD5String(byte[] s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s);
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}
