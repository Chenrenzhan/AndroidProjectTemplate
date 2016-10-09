package com.drumge.template.common;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chenjie3 on 2015/10/8.
 */
public class FileUtil {

    /**
     * 使用给定密码解压指定的ZIP压缩文件到当前目录
     *
     * @param zip    指定的ZIP压缩文件
     * @param passwd ZIP文件的密码
     * @return 解压后文件数组
     * @throws IOException 压缩文件有损坏或者解压缩失败抛出
     */
    public static File[] unzip(String zip, String passwd) throws IOException {// ZipException {
        File zipFile = new File(zip);
        File parentDir = zipFile.getParentFile();
        return unzip(zipFile, parentDir.getAbsolutePath(), passwd);
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到当前目录
     *
     * @param zip    指定的ZIP压缩文件
     * @param passwd ZIP文件的密码
     * @param dest   解压到的目录
     * @return 解压后文件数组
     * @throws IOException 压缩文件有损坏或者解压缩失败抛出
     */
    public static File[] unzip(String zip, String dest, String passwd) throws IOException {//ZipException {
        File zipFile = new File(zip);
        return unzip(zipFile, dest, passwd);
    }

    /**
     * 使用系统指定的ZIP解压文件到指定目录
     * <p/>
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
     *
     * @param zipFile 指定的ZIP压缩文件
     * @param dest    解压目录
     * @param passwd  ZIP文件的密码
     * @return 解压后文件数组
     * @throws IOException 有问题即抛此异常
     *                     warning: java自带的zip功能不支持密码，所以请勿使用带密码的压缩包，还有路径含有中文名也要注意 modify by wangsong 6/17/2016
     */
    public static File[] unzip(File zipFile, String dest, String passwd) throws IOException {
        if (TextUtils.isEmpty(dest)) {
            throw new IOException();
        }
        if (!dest.endsWith(File.separator)) {
            dest = dest + File.separator;
        }
        File destDir = new File(dest);
        if (!destDir.isDirectory() || !destDir.exists()) {//fix bug, 下面注释unzip方法里面的判断是错误的
            destDir.mkdir();
        }
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile.getPath()));
        ZipEntry zipEntry;
        String szName = "";
        List<File> extractedFileList = new ArrayList<File>();
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(destDir + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(destDir + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                extractedFileList.add(file);
            }
        }
        inZip.close();
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }

    /**
     * 使用系统指定的ZIP解压文件到指定目录
     * <p/>
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
     *
     * @param context context
     * @param assetName asset下资源名
     * @param dest    解压目录
     * @return 解压后文件数组
     * @throws IOException 有问题即抛此异常
     *                     warning: java自带的zip功能不支持密码，所以请勿使用带密码的压缩包，还有路径含有中文名也要注意 modify by wangsong 6/17/2016
     */
    public static File[] unzip(Context context, String assetName, String dest) throws IOException {
        if (TextUtils.isEmpty(dest)) {
            throw new IOException();
        }
        if (!dest.endsWith(File.separator)) {
            dest = dest + File.separator;
        }
        File destDir = new File(dest);
        if (!destDir.isDirectory() || !destDir.exists()) {//fix bug, 下面注释unzip方法里面的判断是错误的
            destDir.mkdir();
        }
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream inZip = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        String szName = "";
        List<File> extractedFileList = new ArrayList<File>();
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(destDir + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(destDir + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                extractedFileList.add(file);
            }
        }
        inZip.close();
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }

//    /**
//     * 使用给定密码解压指定的ZIP压缩文件到指定目录
//     * <p>
//     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
//     * @param zipFile 指定的ZIP压缩文件
//     * @param dest 解压目录
//     * @param passwd ZIP文件的密码
//     * @return  解压后文件数组
//     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出
//     */
//    public static File[] unzip(File zipFile, String dest, String passwd) throws ZipException {
//        ZipFile zFile = new ZipFile(zipFile);
//        zFile.setFileNameCharset("utf-8");
//        if (!zFile.isValidZipFile()) {
//            throw new ZipException("压缩文件不合法,可能被损坏.");
//        }
//        File destDir = new File(dest);
//        if (destDir.isDirectory() && !destDir.exists()) {
//            destDir.mkdir();
//        }
//        if (zFile.isEncrypted()) {
//            zFile.setPassword(passwd.toCharArray());
//        }
//        zFile.extractAll(dest);
//
//        List<FileHeader> headerList = zFile.getFileHeaders();
//        List<File> extractedFileList = new ArrayList<File>();
//        for(FileHeader fileHeader : headerList) {
//            if (!fileHeader.isDirectory()) {
//                extractedFileList.add(new File(destDir,fileHeader.getFileName()));
//            }
//        }
//        File [] extractedFiles = new File[extractedFileList.size()];
//        extractedFileList.toArray(extractedFiles);
//        return extractedFiles;
//    }

    /**
     * 删除指定目录下的所有文件和文件夹
     *
     * @param dirPath，绝对路径
     */
    public static void removeDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    if (file.isDirectory()) {
                        removeDir(file.getAbsolutePath());
                    }
                    file.delete();
                }
            }
        }
    }
}
