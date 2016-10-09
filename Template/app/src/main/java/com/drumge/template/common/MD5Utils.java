package com.drumge.template.common;

import com.drumge.template.log.MLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Zhanghuiping on 14/6/5.
 */

public class MD5Utils {
    private static final String TAG = "MD5Utils";

    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            MLog.error(TAG, "get message digest failed! " + e.toString());
        }
    }

    public static String getFileMd5String(String path) throws IOException {
        if (path == null || path.length() == 0) {
            return null;
        }
        File big = new File(path);
        return getFileMD5String(big);
    }

    public static String getFileMD5String(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }
        String md5 = null;
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        if (messagedigest != null) {
            messagedigest.update(byteBuffer);
            md5 = bufferToHex(messagedigest.digest());
        }
        ch.close();
        in.close();
        return md5;
    }

    public static String getMD5String(String s) {
        if(s == null) {
            return null;
        }
            return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        String md5 = null;
        try {
            if (messagedigest != null) {
                messagedigest.update(bytes);
                md5 = bufferToHex(messagedigest.digest());
            }
        }catch (Throwable ex){
            MLog.error(TAG, ex);
        }
        return md5;
    }


    private static String bufferToHex(byte bytes[]) {
        if (bytes == null) {
            return null;
        }
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int start, int len) {
        if (bytes == null || start < 0 || len < 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(2 * len);
        int max = start + len;
        for (int i = start; i < max; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

}
