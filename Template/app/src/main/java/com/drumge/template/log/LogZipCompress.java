package com.drumge.template.log;


import android.support.v4.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 用于压缩日志的实现类，zip压缩
 * Created by wangduo on 2016/4/20.
 */
public class LogZipCompress implements LogCompress {

    private static LogZipCompress mInstance;

    private LogZipCompress() {}
    public synchronized static LogZipCompress getInstance() {
        if(mInstance == null) {
            mInstance = new LogZipCompress();
        }
        return mInstance;
    }

    public static final String EXT = ".zip";
    private static final String BASE_DIR = "";

    // 符号"/"用来作为目录标识判断符
    private static final String PATH = "/";
    private static final int BUFFER = 1024;

    /**
     * 压缩
     * @param srcFile
     * @throws Exception
     */
    @Override
    public void compress(File srcFile) throws Exception {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + File.separator + name.substring(0,name.indexOf(".")) + EXT;
        compress(srcFile, destPath);
    }

    /**
     * 压缩
     *
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     * @throws Exception
     */
    public void compress(File srcFile, File destFile) throws Exception {

        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
                destFile), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);

        compress(srcFile, zos, BASE_DIR);

        zos.flush();
        zos.close();
    }

    /**
     * 压缩文件
     *
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public void compress(File srcFile, String destPath) throws Exception {
        compress(srcFile, new File(destPath));
    }

    /**
     * 压缩
     *
     * @param srcFile
     *            源路径
     * @param zos
     *            ZipOutputStream
     * @param basePath
     *            压缩包内相对路径
     * @throws Exception
     */
    private void compress(File srcFile, ZipOutputStream zos,
                          String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    /**
     * 压缩
     *
     * @param srcPath
     * @throws Exception
     */
    public void compress(String srcPath) throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile);
    }

    /**
     * 文件压缩
     *
     * @param srcPath
     *            源文件路径
     * @param destPath
     *            目标文件路径
     *
     */
    public void compress(String srcPath, String destPath)
            throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile, destPath);
    }

    /**
     * 压缩目录
     *
     * @param dir
     * @param zos
     * @param basePath
     * @throws Exception
     */
    private void compressDir(File dir, ZipOutputStream zos,
                             String basePath) throws Exception {

        File[] files = dir.listFiles();

        // 构建空目录
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);

            zos.putNextEntry(entry);
            zos.closeEntry();

            for (File file : files) {

                // 递归压缩
                compress(file, zos, basePath + dir.getName() + PATH);

            }
        }
    }

    /**
     * 文件压缩
     *
     * @param file
     *            待压缩文件
     * @param zos
     *            ZipOutputStream
     * @param dir
     *            压缩文件中的当前路径
     * @throws Exception
     */
    private void compressFile(File file, ZipOutputStream zos, String dir)
            throws Exception {

        /**
         * 压缩包内文件名定义
         *
         * <pre>
         * 如果有多级目录，那么这里就需要给出包含目录的文件名
         * 如果用WinRAR打开压缩包，中文名将显示为乱码
         * </pre>
         */
        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        bis.close();

        zos.closeEntry();
    }
    /**
     * 压缩列表中的文件到压缩包
     * @param files 输入的待压文件列表
     * @param uid 当前用户ID
     * @return 返回pair：first为结果码，0是正常，其他为相应的错误码；second是在结果正常的情况下为压缩包的绝对路径，异常时为空
     */
    public Pair<Integer,String> compressFiles(List<File> files, long uid) {
        if (files.size() <= 0)
            return new Pair<>(-1012, "");
        String zipPath;
        if(uid == 0) {
            zipPath = MLog.getLogOutputPaths().dir + File.separator + "Android_unknown_userId_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
        } else {
            zipPath = MLog.getLogOutputPaths().dir + File.separator + "Android_" + uid + "_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
        }
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        File zipFile = new File(zipPath);
        if (zipFile.exists())
            zipFile.delete();
        try {
            zipFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(-101, "");
        }
        try {
            fos = new FileOutputStream(zipPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Pair<>(-102, "");
        }
        zos = new ZipOutputStream(fos);
        for (File file : files) {
            if (file == null || !file.exists())
                continue;
            //针对SDK日志做的措施，将SDK日志都放在SDK的目录里，再去压缩，不递归压进行压缩了（SDK不会有二级目录，所以没必要）。
            if(file.isDirectory()) {
                File subFiles[] = file.listFiles();
                for(File f : subFiles) {
                    if(f.isDirectory()) {
                        continue;
                    }
                    ZipEntry ze = new ZipEntry(file.getName() + File.separator + f.getName());
                    try {
                        zos.putNextEntry(ze);
                    } catch (IOException e) {
                        e.printStackTrace();
                        deleteFile(zipFile);
                        return new Pair<>(-103, "");
                    }
                    FileInputStream in = null;
                    try {
                        in = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        deleteFile(zipFile);
                        return new Pair<>(-104, "");
                    }
                    int len;
                    try {
                        while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        deleteFile(zipFile);
                        return new Pair<>(-105, "");
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        deleteFile(zipFile);
                        return new Pair<>(-106, "");
                    }
                }
            } else {
                //客户端的日志文件，直接压
                ZipEntry ze = new ZipEntry(file.getName());
                try {
                    zos.putNextEntry(ze);
                } catch (IOException e) {
                    e.printStackTrace();
                    deleteFile(zipFile);
                    return new Pair<>(-107, "");
                }
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    deleteFile(zipFile);
                    return new Pair<>(-108, "");
                }
                int len;
                try {
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    deleteFile(zipFile);
                    return new Pair<>(-109, "");
                }

                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    deleteFile(zipFile);
                    return new Pair<>(-1010, "");
                }
            }
        }
        try {
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(-1011, "");
        }
        return new Pair<>(0, zipFile.getAbsolutePath());
    }

    /**
     * 压缩列表中的文件到压缩包
     * @param commonLogfiles 输入的普通日志待压文件列表
     * @param sdkLogfiles 输入的SDK日志待压文件列表
     * @param uid 当前用户ID
     * @return 返回pair：first为结果码，0是正常，其他为相应的错误码；second是在结果正常的情况下为压缩包的绝对路径，异常时为空
     */
    public Pair<Integer,String> compressFiles(List<File> commonLogfiles, List<File> sdkLogfiles, long uid) {
        if (commonLogfiles.size() + sdkLogfiles.size() <= 0)
            return new Pair<>(-1012, "");
        String zipPath;
        if(uid == 0) {
            zipPath = MLog.getLogOutputPaths().dir + File.separator + "Android_unknown_userId_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
        } else {
            zipPath = MLog.getLogOutputPaths().dir + File.separator + "Android_" + uid + "_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
        }
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        File zipFile = new File(zipPath);
        if (zipFile.exists())
            zipFile.delete();
        try {
            zipFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(-101, "");
        }
        try {
            fos = new FileOutputStream(zipPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Pair<>(-102, "");
        }
        zos = new ZipOutputStream(fos);
        for(File file : commonLogfiles) {
            if (file == null || !file.exists())
                continue;
            //客户端的日志文件，直接压
            ZipEntry ze = new ZipEntry(file.getName());
            try {
                zos.putNextEntry(ze);
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-107, "");
            }
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-108, "");
            }
            int len;
            try {
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-109, "");
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-1010, "");
            }
        }
        for(File f : sdkLogfiles) {
            if (f.isDirectory()) {
                continue;
            }
            ZipEntry ze = new ZipEntry("sdklog" + File.separator + f.getName());
            try {
                zos.putNextEntry(ze);
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-103, "");
            }
            FileInputStream in = null;
            try {
                in = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-104, "");
            }
            int len;
            try {
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-105, "");
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                deleteFile(zipFile);
                return new Pair<>(-106, "");
            }
        }
        try {
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(-1011, "");
        }
        return new Pair<>(0, zipFile.getAbsolutePath());
    }

    private void deleteFile(File zipFile) {
        if (zipFile.exists()) {
            zipFile.delete();
        }
    }
    /**
     * 解压缩
     *
     * @param srcFile
     * @throws Exception
     */
    public void decompress(File srcFile) throws Exception {
        String basePath = srcFile.getParent() + File.separator;
        decompress(srcFile, basePath);
    }

    /**
     * 解压缩
     *
     * @param srcFile
     * @param destFile
     * @throws Exception
     */
    public void decompress(File srcFile, File destFile) throws Exception {

        CheckedInputStream cis = new CheckedInputStream(new FileInputStream(
                srcFile), new CRC32());

        ZipInputStream zis = new ZipInputStream(cis);
        decompress(destFile, zis);


        zis.close();

    }

    /**
     * 解压缩
     *
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public void decompress(File srcFile, String destPath)
            throws Exception {
        decompress(srcFile, new File(destPath));

    }

    /**
     * 文件 解压缩
     *
     * @param destFile
     *            目标文件
     * @param zis
     *            ZipInputStream
     * @throws Exception
     */
    private void decompress(File destFile, ZipInputStream zis)
            throws Exception {

        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {

            // 文件
            String dir = destFile.getPath() + File.separator + entry.getName();

            File dirFile = new File(dir);

            // 文件检查
            fileProber(dirFile);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                decompressFile(dirFile, zis);
            }
            zis.closeEntry();
        }
    }

    /**
     * 文件 解压缩
     *
     * @param srcPath
     *            源文件路径
     *
     * @throws Exception
     */
    public void decompress(String srcPath) throws Exception {
        File srcFile = new File(srcPath);

        decompress(srcFile);
    }

    /**
     * 文件 解压缩
     *
     * @param srcPath
     *            源文件路径
     * @param destPath
     *            目标文件路径
     * @throws Exception
     */
    public void decompress(String srcPath, String destPath)
            throws Exception {

        File srcFile = new File(srcPath);
        decompress(srcFile, destPath);
    }

    /**
     * 文件解压缩
     *
     * @param destFile
     *            目标文件
     * @param zis
     *            ZipInputStream
     * @throws Exception
     */
    private void decompressFile(File destFile, ZipInputStream zis)
            throws Exception {

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = zis.read(data, 0, BUFFER)) != -1) {
            bos.write(data, 0, count);
        }

        bos.write(data);

        bos.close();
    }
    /**
     * 文件探针
     *
     * <pre>
     * 当父目录不存在时，创建目录！
     * </pre>
     *
     * @param dirFile
     */
    private void fileProber(File dirFile) {

        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {

            // 递归寻找上级目录
            fileProber(parentFile);

            parentFile.mkdir();
        }

    }

}
