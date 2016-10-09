package com.drumge.template.common;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BasicFileUtils {

    public static boolean isSDCardMounted() {
        return availableMemInSDcard();
    }
    
    public static boolean availableMemInSDcard() {
        if (!externalStorageExist()) {
            return false;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdcard.getPath());
        long blockSize = statFs.getBlockSize();
        long avaliableBlocks = statFs.getAvailableBlocks();
        long total = avaliableBlocks * blockSize / 1024;
        if (total < 10) {
            return false;
        }
        return true;
    }
    
    public static boolean externalStorageExist() {
        boolean ret = false;
        ret = Environment.getExternalStorageState().equalsIgnoreCase(
            Environment.MEDIA_MOUNTED);
        return ret;
    }
    
    /*public static String getRootDir() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator 
            + BasicConfig.getExternalFolderName();
    }*/
    
    public static String getFileExt(String fileName) {
        final int pos = fileName.lastIndexOf(".");
        return pos == -1 ? "" : fileName.toLowerCase().substring(pos);
    }
    
    public static String getFileName(String filePath) {
        if (filePath != null) {
            final String slash = "/";
            final int pos = filePath.lastIndexOf(slash) + 1;
            if (pos > 0) {
                return filePath.substring(pos);
            }
        }
        return null;
    }
    
    public static final String ZIP_EXT = ".zip";
    public static final String JPG_EXT = ".jpg";
    public static final String SPEEX_EXT = ".aud";
    
    private static Map<String, String> FILE_MIMES = new HashMap<String, String>();
    static {
        FILE_MIMES.put(ZIP_EXT, "application/zip");
        FILE_MIMES.put(".bmp", "image/bmp");
        FILE_MIMES.put(".gif", "image/gif");
        FILE_MIMES.put(".jpe", "image/jpeg");
        FILE_MIMES.put(".jpeg", "image/jpeg");
        FILE_MIMES.put(JPG_EXT, "image/jpeg");
        FILE_MIMES.put(".png", "image/png");
        FILE_MIMES.put(".speex", "audio/speex");
        FILE_MIMES.put(".spx", "audio/speex");
        FILE_MIMES.put(SPEEX_EXT, "audio/speex");
    }

    public static String getFileMime(String fileName) {
        String mime = FILE_MIMES.get(getFileExt(fileName));
        if (mime != null) {
            return mime;
        }
        return "*/*";
    }
    
    public static void ensureDirExists(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
    }
    
    public static void createDir(String dirPath, boolean nomedia) {
        ensureDirExists(dirPath);
        if (nomedia) {
            File nomediafile = new File(dirPath + "/.nomedia");
            try {
                nomediafile.createNewFile();
            } catch (IOException e) {
            }
        }
    }
    
    public static File createFileOnSD(String dir, String name) {
        File file = null;
        if (isSDCardMounted()) {
            createDir(dir, true);
            String path = dir + File.separator + name;
            file = new File(path);
            try {
                if (!file.exists() && !file.createNewFile()) {
                    file = null;
                }
            } catch (IOException e) {
                //YLog.error("YYFileUtils", "can not create file on SD card, path = " + path);
                file = null;
            }
        }
        return file;
    }
    
    public static boolean isFileExisted(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            return (file.exists() && file.length() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getDirOfFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int sepPos = filePath.lastIndexOf(File.separatorChar);
        if (sepPos == -1) {
            return null;
        }
        return filePath.substring(0, sepPos);
    }
    
    public static void renameFile(String oldFile, String newFile) {
        try {
            File file = new File(oldFile);
            file.renameTo(new File(newFile));
        } catch (Exception e) {
            //YLog.error("YYFileUtils", "renameFile fail, oldFile = %s, %s", oldFile, e);
        }
    }
    
    public static void removeFile(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            try {
                File file = new File(filename);
                file.delete();
            } catch (Exception e) {
            }
        }
    }

    public static void removeDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    /**
     * different from removeDir(path), this is a recursive ver. And it's silent
     * if fname doesn't exist.
     */
    public static void rm(String fname) {
        rm(new File(fname));
    }

    public static void rm(File f) {
        if (f.exists()) {
            if (f.isDirectory())
                for (File i : f.listFiles())
                    rm(i);
            else
                f.delete();
        }
    }
    
    private static final int MAX_BUFF_SIZE = 1024 * 1024;
    private static final int MIN_BUFF_SIZE = 4096;

    public static void copyFile(File src, File des) throws IOException {
        if (des.exists()) {
            des.delete();
        }
        des.createNewFile();

        FileInputStream in = new FileInputStream(src);
        int length = in.available();
        if (length == 0) {
            length = MIN_BUFF_SIZE;
        } else if (length >= MAX_BUFF_SIZE) {
            length = MAX_BUFF_SIZE;
        }
        FileOutputStream out = new FileOutputStream(des);
        byte[] buffer = new byte[length];
        while (true) {
            int ins = in.read(buffer);
            if (ins == -1) {
                in.close();
                out.flush();
                out.close();
                return;
            } else {
                out.write(buffer, 0, ins);
            }
        }
    }

    public static boolean copyFile(String inFileName, String outFileName) {
        try {
            copyFile(new File(inFileName), new File(outFileName));
            return true;
        } catch (Exception e) {
            //YLog.error("YYFileUtils", "lcy copy file failed: %s", e);
            return false;
        }
    }
    
}
