package com.drumge.template.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;


import com.drumge.template.BasicConfig;
import com.drumge.template.log.MLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class YYFileUtils extends BasicFileUtils {
    public static final String TEMP_DIR =  "temp";
    private static final String IMAGE_DIR = "/image";
    private static final String ACT_RECOMMAND_FILE = "temp_act_recomm.txt";
    private static final String RECORD_EXT_HIGH_CPU = ".aac";
    private static final String RECORD_EXT_LOW_CPU = ".wav";
    private static final String RECORD_PUBLISH_EXT = ".m4a";

    private static final int MIN_LEN_OF_VALID_WAV = 128 * 1024;
    private static final int MIN_LEN_OF_VALID_AAC = 8 * 1024;

    static final String[] AUDIO_EXTS = new String[] {
        RECORD_EXT_HIGH_CPU, RECORD_EXT_LOW_CPU, RECORD_PUBLISH_EXT, ".rec",
        ".mp4", ".rec2" };

    private FileOutputStream mFileOutputStream = null;
    private BufferedOutputStream mBufferedOutputStream = null;
    private File mFile;

    /*
     *是否是有效音频文件
     */
    public static boolean isValidAudioFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            String ext = YYFileUtils.getFileExtension(path);
            if (!TextUtils.isEmpty(ext)) {
                for (String extItem : AUDIO_EXTS) {
                    if (ext.equalsIgnoreCase(extItem)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getPkgDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
    /*
     *读取txt文件内容
     */
    public static String getTxtFileContent(Context context, String fileName) {
        String path = fileName;
        String content = "";
        if (TextUtils.isEmpty(fileName)) {
            return content;
        }
        File file = new File(path);
        if (file.isFile()) {
            InputStream instream = null;
            try {
                if (fileName.startsWith(context.getFilesDir().getPath())) {
                    instream = context.openFileInput(YYFileUtils
                        .getFileName(fileName));
                } else {
                    instream = new FileInputStream(file);
                }
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(
                        instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    StringBuffer contentBuffer = new StringBuffer();
                    while ((line = buffreader.readLine()) != null) {
                        contentBuffer.append(line).append("\n");
                    }
                    content = contentBuffer.toString();
                    buffreader.close();
                }
            } catch (Throwable e) {
                MLog.error("YYFileUtils", "getTxtFileContent error! " + e);
                //YLog.error("getTxtFileContent", "read fail, e = " + e);
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return content;
    }
    /*
     *返回文件扩展名
     */
    public static String getFileExtension(String filePath) {
        String fileName = getFileName(filePath);
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index);
        }
        return null;
    }
    /*
     *获取文件名
     */
    public static String getFileName(String filePath) {
        if (filePath != null) {
            final String slash = File.separator;
            final int pos = filePath.lastIndexOf(slash) + 1;
            return filePath.substring(pos);
        }
        return null;
    }

    /* drop the extension of a filename */
    public static String dropExt(String fname) {
        if (!TextUtils.isEmpty(fname)) {
            int pos = fname.lastIndexOf(".");
            if (pos != -1)
                return fname.substring(pos+1);;
        }
        return fname;
    }

    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
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

    /**
     * 重命名文件
     * @param oldFile
     * @param newFile
     */
    public static void renameFile(String oldFile, String newFile) {
        try {
            File file = new File(oldFile);
            file.renameTo(new File(newFile));
        } catch (Exception e) {
            //YLog.error("YYFileUtils", "renameFile fail, oldFile = %s, %s", oldFile, e);
        }
    }
    //移除多个文件
    public static void removeFiles(List<Pair<Integer, String>> fileNames) {
        for (Pair<Integer, String> p : fileNames) {
            if (p.second != null) {
                removeFile(p.second);
            }
        }
    }
    //移除一个文件
    public static void removeFile(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            try {
                File file = new File(filename);
                file.delete();
            } catch (Exception e) {
            }
        }
    }
    //移除目录
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

    /*public static File getFileFromURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int idx = url.lastIndexOf(File.separatorChar);
        return new File(getYYImImageDir() + url.substring(idx + 1));
    }*/

    public static File getFileFromURL(String base, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int idx = url.lastIndexOf(File.separatorChar);
        return new File(base, url.substring(idx + 1));
    }

    public static String getImageFilePathFromUri(Context context, Uri uri) {
        if(uri == null){
            //YLog.debug("xuwakao", "getFilePathFromUri param uri == NULL");
            return null;
        }

        File file = new File(uri.getPath());
        if (file.isFile()) {
            return file.getPath();
        }
        if ("file".equals(uri.getScheme())) {
            String ret = uri.toString().substring(7);
            ret = decodeUri(ret);
            return ret;
        } else if ("content".equals(uri.getScheme())){
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                String ret = cursor.getString(index);
                //YLog.verbose("xuwakao" , "getFilePathFromUri ret = " + ret + ", index = " + index + ", cursor = " + cursor);
                ret = decodeUri(ret);
                cursor.close();
                return ret;
            }
        }
        return null;
    }

    public static String decodeUri(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return uri;
        }
        int index = uri.indexOf('%');
        if (index != -1) {
            uri = Uri.decode(uri);
        }
        return uri;
    }

    public static boolean isSDCardMounted() {
        return BasicFileUtils.availableMemInSDcard();
    }

    public static boolean externalStorageExist() {
        boolean ret = false;
        ret = Environment.getExternalStorageState().equalsIgnoreCase(
            Environment.MEDIA_MOUNTED);
        return ret;
    }

    public static boolean checkFileValidation(String filepath, String md5) throws IOException {
        final String fileMd5 = MD5Utils.getFileMd5String(filepath);
        if(fileMd5==null){
            return false;
        }
        return fileMd5.equals(md5);
    }

    /**
     * 获取一个临时文件
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getTempFile(Context context, String uniqueName){
    	String tempPath = BasicConfig.getInstance().getRootDir().getAbsolutePath()+ File.separator+TEMP_DIR;
    	File tmpFile = new File(tempPath);
    	if(!tmpFile.exists())
			tmpFile.mkdirs();
		return new File(tmpFile.getAbsolutePath() + File.separator + uniqueName);
    }


    /**
     * 已停用，请使用FileRequestManager里的方法
     * 保存图片到 PNG文件
     * @param bitmap
     * @param fileName
     */
    @Deprecated
    public static void saveBitmapToPNG(Bitmap bitmap, String filePath)throws Exception {
    	saveBitmap(bitmap,filePath, Bitmap.CompressFormat.PNG);
    }

    /**
     * 已停用，请使用FileRequestManager里的方法
     * 保存图片到 JPG文件
     * @param bitmap
     * @param fileName
     */
    @Deprecated
    public static void saveBitmapToJPG(Bitmap bitmap, String filePath)throws Exception {
    	saveBitmap(bitmap,filePath, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 已停用，请使用FileRequestManager里的方法
     * @param bitmap
     * @param filePath
     * @param format
     * @throws Exception
     */
    @Deprecated
    public static void saveBitmap(Bitmap bitmap, String filePath, CompressFormat format)throws Exception {
        saveBitmap(bitmap, filePath, format, 50);
    }

        /**
         * 已停用，请使用FileRequestManager里的方法
         * 保存图片到文件
         * @param bitmap
         * @param filePath
         * @param format
         * @throws Exception
         */
    @Deprecated
    public static void saveBitmap(Bitmap bitmap, String filePath, CompressFormat format, int quality)throws Exception {
        if (bitmap == null) {
            return;
        }

        if(format == null){
        	format = Bitmap.CompressFormat.PNG;
        }
        File barcodeFile = new File(filePath);
        if (!barcodeFile.exists() ) {
        	barcodeFile.createNewFile();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(barcodeFile);
            bitmap.compress(format, quality, fos);
        } catch (Exception fnfe) {
            MLog.error(TAG, "Couldn't access file %s due to %s", barcodeFile,fnfe);
            throw fnfe;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
    }



    public static String dropPrefix(String s, String prefix) {
        return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    /**
     * Safe concatenate paths no matter the first one ends with / or the second
     * one starts with /.
     */
    public static String concatPath(String p1, String p2) {
        return p1.endsWith(File.separator) ? p1 + dropPrefix(p2, File.separator) : p1 + File.separator
            + dropPrefix(p2, File.separator);
    }

    public static String concatPaths(String... ss) {
        String path = "";
        for (String s : ss)
            path = concatPath(path, s);
        return path;
    }



    public static String getYYActRecommFilename(Context context) {
        File file = context.getFileStreamPath(ACT_RECOMMAND_FILE);
        return file.getPath();
    }

    public static YYFileUtils createFile(String path) throws Exception {
        String dir = YYFileUtils.getDirOfFilePath(path);
        String name = YYFileUtils.getFileName(path);
        File f = BasicFileUtils.createFileOnSD(dir, name);
        return new YYFileUtils(f, null);
    }

    public static YYFileUtils openFile(String filePath) throws Exception {
        String dirPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
        BasicFileUtils.createDir(dirPath, true);

        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
            file = null;
        }
        return new YYFileUtils(file, null);
    }

    private YYFileUtils(File file, FileOutputStream fileos) throws Exception {
        mFile = file;
        mFileOutputStream = fileos;
        if (mFile != null) {
            if (mFileOutputStream == null) {
                mFileOutputStream = new FileOutputStream(mFile);
            }
            mBufferedOutputStream = new BufferedOutputStream(mFileOutputStream);
        } else {
            throw new Exception(
                "YYFileOutput, can not create file output stream");
        }
    }

    /**
     * Ensure the parent directory of given file path exists. make directories
     * if need.
     *
     * @param filePath
     *            A file path.
     * @return True for success, false otherwise.
     */
    public static boolean ensureFileDirExists(String filePath) {
        String dir = getDirOfFilePath(filePath);
        if (TextUtils.isEmpty(dir)) {
            return false;
        }
        BasicFileUtils.ensureDirExists(dir);
        return true;
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



    public void write(Bitmap bmp) {
        write(bmp, 80);
    }

    public void write(Bitmap bmp, int compressRate) {
        bmp.compress(Bitmap.CompressFormat.JPEG, compressRate, mBufferedOutputStream);
    }

    public void writeYCbCr420SP(byte[] data, int width, int height) {
        YuvImage image = new YuvImage(data, PixelFormat.YCbCr_420_SP, width, height, null);
        image.compressToJpeg(new Rect(0, 0, width, height - 1), 100, mBufferedOutputStream);
    }

    public void write(InputStream is) {
        int bytes = 0;
        byte[] buffer = new byte[4096];
        try {
            while ((bytes = is.read(buffer)) != -1) {
                mBufferedOutputStream.write(buffer, 0, bytes);
            }
        } catch (IOException e) {
            //YLog.error(this, e);
        }
    }

    public void write(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            write(fis);
            fis.close();
        } catch (Exception e) {
            //YLog.error(this, e);
        }
    }

    public void write(byte[] buffer) {
        try {
            mBufferedOutputStream.write(buffer);
        } catch (IOException e) {
            //YLog.error(this, e);
        }
    }

    public void write(byte[] buffer, int offset, int length) {
        try {
            mBufferedOutputStream.write(buffer, offset, length);
        } catch (IOException e) {
            //YLog.error(this, e);
        }
    }

    public void close() {
        try {
            if (mBufferedOutputStream != null) {
                mBufferedOutputStream.flush();
                mBufferedOutputStream.close();
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
        } catch (IOException e) {
            //YLog.error(this, e);
        }
    }

    public File getFile() {
        return mFile;
    }

    public static long getFileLength(String file) {
        File tmp = new File(file);
        return tmp.length();
    }

    public static String fallbackFile(String file) {
        String ext = BasicFileUtils.getFileExt(file);
        int i = file.lastIndexOf(".");
        return i == -1 ? "" : file.substring(0, i + 1) + "bak" + ext;
    }

    /**
     * Check validity of record file, currently only .aac and .wav file are
     * supported, check is based on the file length.
     *
     * @param filePath
     *            Must be end with .aac or .wav.
     */
    public static boolean isValidRecordFile(String filePath) {
        if (!isFileExisted(filePath)) {
            return false;
        }

        String ext = getFileExtension(filePath);
        if (ext == null) {
            return false;
        }

        //YLog.verbose(YYFileUtils.class, "lcy file extension is %s", ext);

        boolean aac = false;
        if (!(aac = ext.equalsIgnoreCase(RECORD_EXT_HIGH_CPU))
            && !ext.equalsIgnoreCase(RECORD_EXT_LOW_CPU)) {
            //YLog.debug(YYFileUtils.class, "lcy record extension check failed.");
            return false;
        }

        final long len = YYFileUtils.getFileLength(filePath);
        final long minLen = aac ? MIN_LEN_OF_VALID_AAC : MIN_LEN_OF_VALID_WAV;
        boolean ret = (len >= minLen);
        //YLog.debug(YYFileUtils.class, "lcy file length invalid %d, %d, %s.",len, minLen, ext);
        return ret;
    }

    /**
     * Get file size, if it is a directory, will accumulate the size of the
     * inner files recursively.
     *
     * @param file
     * @return 0 if no file, the total size of the files.
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        File fileList[] = file.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFileSize(fileList[i]);
                } else {
                    // YLog.verbose("Simon", "file: " + fileList[i] + "  size: " +
                    // fileList[i].length());
                    size = size + fileList[i].length();
                }
            }
        }
        return size;
    }

    /**
     * Get human readable file size.
     *
     * @param bytes
     *            Num of bytes.
     */
    public static String getHumanReadableFileSize(long bytes) {
        // less than 1K, show it in Bs, less than 1M, show it in KBs, otherwise
        // show in MBs.
        if (bytes < 1024) {
            return getFileSizeInBytes(bytes);
        }
        return (bytes >> 20) == 0 ? getFileSizeInKBytes(bytes)
            : getFileSizeInMBytes(bytes);
    }

    public static String getFileSizeInBytes(long bytes) {
        return String.format("%dB", bytes);
    }

    public static String getFileSizeInKBytes(long bytes) {
        long kbs = (bytes >> 10);
        return String.format("%dK", kbs);
    }

    public static String getFileSizeInMBytes(long bytes) {
        float kbs = bytes / 1024.0f;
        float mbs = kbs / 1024;
        DecimalFormat df = new DecimalFormat("0.00M");
        String ret = df.format(mbs);
        return ret;
    }

    private static final String BARCODE_FILE_EXT = ".png";
    private static final String YY_BARCODE_DIR = "YYBarcode";
    private static final String TAG = "YYFileUtils";
    private static final int MAX_FILENAME_LENGTH = 24;
    private static final Pattern NOT_ALPHANUMERIC = Pattern
        .compile("[^A-Za-z0-9]");

    /**
     * Save bitmap to external storage public directory for pictures.
     *
     * @param bitmap
     * @param fileName
     */
    public static void saveBitmapToPublicDir(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return;
        }

        File barcodesRoot = new File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            YY_BARCODE_DIR);

        if (!barcodesRoot.exists() && !barcodesRoot.mkdirs()) {
            //YLog.warn(TAG, "Couldn't make dir %s", barcodesRoot);
            // showErrorMessage(R.string.msg_unmount_usb);
            return;
        }

        File barcodeFile = new File(barcodesRoot, makeFileName(fileName)
            + BARCODE_FILE_EXT);
        barcodeFile.delete();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(barcodeFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (FileNotFoundException fnfe) {
            //YLog.warn(TAG, "Couldn't access file %s due to %s", barcodeFile,fnfe);
            // showErrorMessage(R.string.msg_unmount_usb);
            return;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }

    }

    private static String makeFileName(CharSequence contents) {
        String fileName = NOT_ALPHANUMERIC.matcher(contents).replaceAll("_");
        if (fileName.length() > MAX_FILENAME_LENGTH) {
            fileName = fileName.substring(0, MAX_FILENAME_LENGTH);
        }
        return fileName;
    }

    /**
     * Read file bytes and return.
     *
     * @param file
     *            Must not be null.
     * @return null if input is not a valid file.
     */
    public static byte[] fileToByteArray(File file) {
        if (!file.exists() || !file.canRead()) {
            return null;
        }

        try {
            return streamToBytes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            //YLog.error(TAG, e);
            return null;
        }
    }

    /**
     * Convert input stream to byte array.
     *
     * @return null if failed.
     */
    public static byte[] streamToBytes(InputStream inputStream) {
        byte[] content = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);

        try {
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            content = baos.toByteArray();
            if (content.length == 0) {
                content = null;
            }

            baos.close();
            bis.close();
        } catch (IOException e) {
            //YLog.error(TAG, e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    //YLog.error(TAG, e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    //YLog.error(TAG, e);
                }
            }
        }

        return content;
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



    public static String getImagePathFromURL(String basePath, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int idx = url.lastIndexOf(File.separatorChar);
        String path = url.substring(idx + 1);
        return basePath + File.separator + path;
    }

    /** A shortcut alias class for code golf */
    public static class IO {
        public static void mkdir(String path) {
            BasicFileUtils.ensureDirExists(path);
        }

        public static String concat(String p1, String p2) {
            return concatPath(p1, p2);
        }

        public static String concats(String... ps) {
            return concatPaths(ps);
        }

        /**
         * Differ from isFileExisted, this func desn't test if the length is
         * zero.
         */
        public static boolean exist(String f) {
            if (!TextUtils.isEmpty(f))
                try {
                    return new File(f).exists();
                } catch (Exception e) {
                }
            return false;
        }

        public static boolean touch(String f) {
            if (exist(f))
                return false;
            if (ensureFileDirExists(f))
                try {
                    new File(f).createNewFile();
                } catch (Exception e) {
                }
            return true;
        }
    }


    public static void deleteOldFiles(String dirPath, final String postfix, long downloadTime) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return (filename.toLowerCase().indexOf(postfix) != -1);
                }
            };
            List<String> portraitList = new ArrayList<String>();
            //TODO xianbing
            //FIXME xianbing
//            if (StringUtils.equal(dirPath, YYFileUtils.getYYImageDir(), true)) {
//                List<UserInfo> friendList = Content.obj(YYMobile.gContext).getDisplayFriends(true, true);
//                for (UserInfo info : friendList) {
//                    if (!StringUtils.isNullOrEmpty(info.getPortraitUrl())) {
//                        portraitList.add(Utils.getPhotoFullPathFromUrl(info.getUid(), info.getPortraitUrl(),
//                            FriendPictureInfo.ImgType.SMALL));
//                    }
//                }
//            }
            File[] fileList = dir.listFiles(filter);
            if (fileList != null && fileList.length > 0) {
                long current = System.currentTimeMillis();
                for (File file : fileList) {
                    if (!portraitList.contains(file.getPath()) && current - file.lastModified() > downloadTime) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static boolean isSameFile(String path1, String path2) {
        if (path1 == null || path2 == null) {
            //YLog.error(YYFileUtils.class, "lcy input illegal for comparsion %s %s.", path1, path2);
            return false;
        }
        return new File(path1).equals(new File(path2));
    }

    public static String getRootDir() {
        return BasicConfig.getInstance().getRootDir().getAbsolutePath();
    }

    public static String getYYTempDir() {
        return getRootDir() + TEMP_DIR;
    }

    public static String getYYImReceivedImageDir() {
        return getRootDir() + IMAGE_DIR + File.separator;
    }

    public static String getYYImageFileLocalPath(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        String filename = name;
        int index = name.lastIndexOf(File.separatorChar);
        if (index != -1) {
            filename = name.substring(index + 1);
        }
        String dirPath = getYYImReceivedImageDir();
        BasicFileUtils.createDir(dirPath, true);
        return dirPath + filename;
    }

    public static boolean isTempFile(Context c, String path) {
        File temp = getTempFile(c, getFileName(path));
        return TextUtils.equals(path, temp.getPath());
    }

    public static String getLocalPathFromUrl(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        return getYYImReceivedImageDir() + fileName;
    }


    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            MLog.error("YYFileUtils", "getBytes error!" + e);
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void saveToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            MLog.error("YYFileUtils", "saveFile error!" + e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    MLog.error("YYFileUtils", "saveFile error!" + e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    MLog.error("YYFileUtils", "saveFile error!" + e);
                }
            }
        }
    }

    public static boolean saveToFile(String str, String path) {
//        MLog.debug(TAG, "saveToPath path:" + path + ", str:" + str);
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        try {
            OutputStream os = new FileOutputStream(path);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            stream.close();
        } catch (Exception e) {
            MLog.error(TAG, "saveToPath error!" + e);
            return false;
        }
        return true;
    }
}
