package com.drumge.template.log;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.drumge.template.BasicConfig;
import com.drumge.template.ScheduledTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangduo on 2016/4/19.
 * 这个类提供对日志的一些管理，如本地日志描述文件管理、日志文件过期检查、压缩检查等等
 */
public class LogManager {
    /***************************/
    private static LogManager mInstance;
    private LogManager(){}
    public synchronized static LogManager getInstance(){
        if(mInstance == null)
            mInstance = new LogManager();
        return mInstance;
    }
    /***************************/

    public static final String LOG_EXT = ".txt";
    public static final String LOG_TAG = "yymobile_log_files";
    public static final String LOG_RECORDS = "yy_log_records";
    public static final String OLD_LOGS = "logs.txt";
    public static final String UNCAUGHT_EXCEPTIONS_LOGS = "uncaught_exception.txt";
    public static final String LOG_DESCRIPTION = "log_description.txt";
    private static final float AVERAGE_LOG_ZIP_COMPRESSION_RATIO = 0.15f;//ZIP方式在手Y的压log的平均压缩率，用于收集日志时，估算日志压缩后大小
    private static Context mContext;
    private static final String PATTERN_STR = "[0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}";
    private static final String LOG_DATE_FORMAT_STR = "yyyy_MM_dd_HH";
    private static Pattern PATTERN = Pattern.compile(PATTERN_STR);
    public static final int MAX_FILE_SIZE = 101;//日志目录下最大文件长度为101M（超过则删除该文件，比写入的100M大1M保证不会误删）
    public static final int SDK_LOG_FILE_LIMIT_ZIP_SIZE = 5;


    //压缩错误码
    public static final int LOG_DIR_NOT_EXIST = -8;
    public static final int NO_LOG_FILES_EXIST = -9;
    public static final int LOG_COMPRESS_FAILED = -10;
    public static final int SD_CARD_NOT_ENOUGH_FREE_SIZE = -11;


    public void setCompressListener(LogCompressListener listener) {
        this.listener = listener;
    }

    private LogCompressListener listener;

    private LogCurrentWritingPath pathListener;

    public LogCurrentWritingPath getPathListener() {
        return pathListener;
    }

    public void setPathListener(LogCurrentWritingPath pathListener) {
        this.pathListener = pathListener;
    }

    /** 7 days. */
    public static final long DAY_DELAY = 7L * 24 * 60 * 60 * 1000;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        LogManager.mContext = mContext;
    }

    /**
     * 将新的日志文件名加入到描述文件（SharedPreferences）里
     * @param logName
     */
    public void addSingleLogRecord(String logName) {
        String records = getLogRecord();
        if(TextUtils.isEmpty(records) ) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("|"+logName);
            setLogRecord(buffer.toString());
        } else if (!records.contains(logName)){
            StringBuffer buffer = new StringBuffer(records);
            buffer.append("|"+logName);
            setLogRecord(buffer.toString());
        } else {
            return;
        }
    }

    /**
     * 删除日志描述文件中的一条日志文件记录
     * @param logName
     */
    public void removeSingleLogRecord(String logName) {
        String records = getLogRecord();
        if(TextUtils.isEmpty(records)) {
            return;
        } else if(records.contains(logName)) {
            String target = records.replaceAll("\\|"+logName,"");
            setLogRecord(target);
        } else {
            return;
        }
    }
    /**
     * 获取所有在描述文件里的日志文件名集合（是一个String）
     * @return
     */
    public String getLogRecord() {
        if(BasicConfig.getInstance().getAppContext() != null)
            return BasicConfig.getInstance().getAppContext().getSharedPreferences(LOG_TAG,0).getString(LOG_RECORDS,null);
        else if(mContext != null)
            return mContext.getSharedPreferences(LOG_TAG,0).getString(LOG_RECORDS,null);
        else
            return null;
    }

    /**
     * 设置描述文件里的所有文件名
     * @param allLogNames
     */
    public void setLogRecord(String allLogNames) {
        if(BasicConfig.getInstance().getAppContext() != null)
            BasicConfig.getInstance().getAppContext().getSharedPreferences(LOG_TAG,0).edit().putString(LOG_RECORDS, allLogNames).commit();
        else if(mContext != null)
            mContext.getSharedPreferences(LOG_TAG,0).edit().putString(LOG_RECORDS, allLogNames).commit();
        else
            return;
    }

    /**
     * 将描述文件中的文件记录解析到内存,并输出为文件
     */
    public String createLogDescriptionFile() {
        MLog.info(this, "createLogDescriptionFile() called.");
        String records = getLogRecord();
        String descriptionDir = LogToES.getLogPath();
        String descriptionFilePath = LogToES.getLogPath() + File.separator + "log_description.txt";
        File dirFile = new File(descriptionDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File logFile = new File(descriptionFilePath);
        if(logFile.exists() && !logFile.isDirectory()) {
            logFile.delete();
        }
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer("");
        if(!TextUtils.isEmpty(records)) {
            String list[] = records.split("\\|");
            for (String s : list) {
                if (!TextUtils.isEmpty(s)) {
                    buffer.append(s);
                    buffer.append("\r\n");
                }
            }
        } else {
            buffer.append("There is no log record, log description is blank.");
        }
        String descriptionStr = buffer.toString();
        FileWriter fileWriter = null;
        BufferedWriter bufWriter = null;
        try {
            fileWriter = new FileWriter(logFile, true);
            bufWriter = new BufferedWriter(fileWriter, 32 * 1024);
            bufWriter.write(descriptionStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufWriter != null) {
                    bufWriter.flush();
                    bufWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return descriptionFilePath;
    }

    /**
     * 删除过期的log文件
     */
    public void deleteOldLogs() {
        MLog.info(this,"deleteOldLogs() called.");
        String dir = LogToES.getLogPath();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            return;
        }

        long now = System.currentTimeMillis();
        File files[] = dirFile.listFiles();
        if (files == null) {
            return;
        }
        //大小异常的文件和过期文件，删除
        for (File file : files) {
            if(checkCompressed(file.getName()) && file.length() < 200) {
                MLog.info(this,"deleteOldLogs() : " + file.getName() + " deleted , because of abnormal file size.");
                removeLogFile(file);
            }
            long backupTime = parseLogCreateTime(file);
            long fileSize = (file.length() >>> 20);// convert to M bytes 单位转换为兆
            if (fileSize >= MAX_FILE_SIZE) {
                MLog.info(this,"deleteOldLogs() : " + file.getName() + " deleted , because of abnormal file size.");
                removeLogFile(file);
            } else if(now - backupTime > DAY_DELAY) {
                MLog.info(this,"deleteOldLogs() : " + file.getName() + " deleted , because this file is overdue.");
                removeLogFile(file);
            } else {}
        }
    }

    /**
     * 删除日志文件，同时删去记录
     * @param file
     */
    private void removeLogFile(File file) {
        if(file.exists() && !file.isDirectory() && file.getName().contains(".")) {
            removeSingleLogRecord(file.getName().substring(0, file.getName().indexOf(".")));
        }
        file.delete();
    }

    /**
     * 检查并压缩尚未压缩的log文件（当前的log除外）
     */
    public void checkAndCompressLog() {
        MLog.info(this,"checkAndCompressLog() called");
        String dir = LogToES.getLogPath();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            return;
        }
        final File files[] = dirFile.listFiles();
        if (files == null) {
            return;
        }
        ScheduledTask.getInstance().scheduledDelayed(new Runnable() {
            @Override
            public void run() {
                for (File file : files) {
                    String currentTime = new SimpleDateFormat("yyyy_MM_dd_HH").format(new Date());
                    //旧系统输出的日志、异常日志、日志描述文件，不压
                    if (file.getName().equals(OLD_LOGS) || file.getName().equals(UNCAUGHT_EXCEPTIONS_LOGS) || file.getName().equals(LOG_DESCRIPTION))
                        continue;
                    //新系统生成的日志文件，如果没压过进行压缩
                    else if (file.getName().endsWith(LOG_EXT) && !file.getName().contains(currentTime) && containsPattern(file)) {
                        try {
                            MLog.info(this, "checkAndCompressLog() : " + file.getName() + " is compressed.");
                            LogZipCompress.getInstance().compress(file);
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        },0);
    }

    /**
     * SDK日志的目录
     * @return
     */
    public String sdkLogDir() {
        return LogToES.getLogPath() + File.separator + "sdklog" + File.separator;
    }

    /**
     * UNCAUGHT_EXCEPTIONS日志的路径
     * @return
     */
    public String uncaughtExceptionsLogsPath() {
        return LogToES.getLogPath() + File.separator + UNCAUGHT_EXCEPTIONS_LOGS;
    }

    /**
     * 用于压缩日志的缓存目录
     * @return
     */
    public String tempDecompressDir() {
        return LogToES.getLogPath() + File.separator + "tempDir" + File.separator;
    }

    /**
     * 收集时间段内的日志 并压缩
     * @param startTimeMils 起始时间
     * @param endTimeMils 终止时间
     * @param uid 用户ID
     * @return true：时间段内有日志  false：时间段内无日志
     */
    public boolean collectLogByTime(long startTimeMils, long endTimeMils, final long uid) {
        MLog.info(this, "collectLogByTime() called.");
        final ArrayList<File> srcLogFiles = new ArrayList<>();
        final ArrayList<File> destLogFiles = new ArrayList<>();
        final String tempDir = tempDecompressDir();
        String dir = LogToES.getLogPath();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            if(listener != null)
                listener.onCompressError(LOG_DIR_NOT_EXIST);
            return false;
        }
        final File files[] = dirFile.listFiles();
        if (files == null) {
            if(listener != null)
                listener.onCompressError(NO_LOG_FILES_EXIST);
            return false;
        }

        //生成描述文件并加入压缩列表
        MLog.info(this, "collectLogByTime() : generating log description");
        String logDescFilePath = createLogDescriptionFile();
        if(!TextUtils.isEmpty(logDescFilePath)) destLogFiles.add(new File(logDescFilePath));

        //将SDK日志加入的压缩列表
        MLog.info(this,"collectLogByTime() : collecting SDK logs");
        File sdkFileDir = new File(sdkLogDir());
        if(sdkFileDir.exists()) destLogFiles.add(sdkFileDir);

        //将UNCAUGHT_EXCEPTIONS日志文件加入的压缩列表
        MLog.info(this,"collectLogByTime() : collecting UNCAUGHT_EXCEPTIONS log");
        File uncaughtExceptionFile = new File(uncaughtExceptionsLogsPath());
        if(uncaughtExceptionFile.exists()) destLogFiles.add(new File(uncaughtExceptionsLogsPath()));

        //加入普通日志（文件名检查，判断是否含有正则表达式的子串，来筛选是否是日志文件）
        MLog.info(this,"collectLogByTime() : collecting normal logs between time point(" + startTimeMils + ") and (" + endTimeMils + ")");
        long createTime = 0L, zipSize = 0L;
        for (File file : files) {
            createTime = parseLogCreateTime(file);
            if(!file.isDirectory() && containsPattern(file) && createTime != 0L && createTime >= startTimeMils && createTime <= endTimeMils) {
                srcLogFiles.add(file);
                zipSize += file.length();
            }
        }

        //检查SD卡剩余空间是否足够
        if(getSDFreeSize() <= zipSize * 10) {
            if(listener != null)
                listener.onCompressError(SD_CARD_NOT_ENOUGH_FREE_SIZE);
            return false;
        }

        final File temp = new File(tempDir);
        if(temp.exists() && temp.isDirectory()) {
            deleteDir(temp);
        }

        ScheduledTask.getInstance().scheduledDelayed(new Runnable() {
            @Override
            public void run() {
                //将要上传的日志先进行解压
                if(srcLogFiles.size() > 0) {
                    MLog.info(this,"collectLogByTime() : Logs packing task started");
                    for (File file : srcLogFiles) {
                        if (checkCompressed(file.getName())) {
                            try {
                                //小于200字节的压缩文件可以判定为异常的文件，不进行解压（会出错）删除之。
                                if(file.length() < 200) {
                                    removeLogFile(file);
                                } else {
                                    LogZipCompress.getInstance().decompress(file, tempDir);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            destLogFiles.add(file);
                        }
                    }
                }
                //如果该时间段内有日志（会在临时文件夹中），那么把这些文件加入的压缩列表中
                File[] tempFiles = new File(tempDir).listFiles();
                if(tempFiles != null) {
                    for(File file : tempFiles) {
                        if(!destLogFiles.contains(file)) {
                            destLogFiles.add(file);
                        }
                    }
                }
                //开始压缩
                if(destLogFiles.size() > 0) {
                    Pair<Integer,String> pack = LogZipCompress.getInstance().compressFiles(destLogFiles, uid);
                    if(pack.first !=0 || TextUtils.isEmpty(pack.second)) {
                        if(listener != null)
                            listener.onCompressError(pack.first);
                    } else {
                        if(listener != null)
                            listener.onCompressFinished(pack.second);
                    }
                }
                //删除临时文件夹
                deleteDir(temp);
                MLog.info(this, "collectLogByTime() : Logs packing task finished");


            }
        },0);

        return true;
    }

    /**
     * 收集并打包该时间点附近的日志
     * @param collectTimePoint 时间点
     * @param sizeInMB 收集量
     * @param uid 用户ID
     * @return true：收集并打包成功 false：任务过程出现失败
     */
    public boolean collectLogBySize(long collectTimePoint, int sizeInMB, final long uid) {
        MLog.info(this,"collectLogBySize() called");
        final ArrayList<File> srcLogFiles = new ArrayList<>();
        final ArrayList<File> destLogFiles = new ArrayList<>();
        final ArrayList<File> sdkLogFiles = new ArrayList<>();
        final TreeMap<Long,String> fileList = new TreeMap<>(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                if(lhs instanceof Long && rhs instanceof Long) {
                    if((Long)lhs < (Long)rhs) {
                        return -1;
                    } else if((Long)lhs > (Long)rhs){
                        return 1;
                    } else {
                        return 0;
                    }
                }
                return 0;
            }
        });
        final String tempDir = tempDecompressDir();
        float residualSize = sizeInMB * 1024 * 1024;//计算大小
        String dir = LogToES.getLogPath();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            if(listener != null)
                listener.onCompressError(LOG_DIR_NOT_EXIST);
            return false;
        }
        final File files[] = dirFile.listFiles();
        if (files == null) {
            if(listener != null)
                listener.onCompressError(NO_LOG_FILES_EXIST);
            return false;
        }
        /****首先将描述文件、SDK日志、UNCAUGHT_EXCEPTIONS日志加入到压缩列表，并估算压缩后大小****/
        //生成描述文件并加入压缩列表
        MLog.info(this,"collectLogBySize() : generating log description");
        String logDescFilePath = createLogDescriptionFile();
        if(!TextUtils.isEmpty(logDescFilePath)) {
            File f = new File(logDescFilePath);
            destLogFiles.add(f);
        }
        //将SDK日志加入的压缩列表
        MLog.info(this,"collectLogBySize() : collecting SDK logs");
        File sdkDir = new File(sdkLogDir());
        File sdkFiles[] = sdkDir.listFiles();
        float sdkFileZipSize = SDK_LOG_FILE_LIMIT_ZIP_SIZE * 1024 *1024;
        if(sdkFiles != null) {
            for(File f : sdkFiles) {
                if(!f.isDirectory()) {
                    sdkLogFiles.add(f);
                    sdkFileZipSize -= f.length() * AVERAGE_LOG_ZIP_COMPRESSION_RATIO;
                }
            }
        }
        //SDK日志体积控制：太大超标的话，不压一些旧的SDK文件，去掉
        if(sdkFileZipSize < 0 && sdkLogFiles.size() > 0) {
            MLog.info(this,"collectLogBySize() : SDK Logs size exceeds the limit , starting to filter these SDK logs");
            File removeFile = null;
            long time = sdkLogFiles.get(0).lastModified();
            while(sdkFileZipSize < 0) {
                for(File f : sdkLogFiles) {
                    if(f.lastModified() < time) {
                        removeFile = f;
                        time = f.lastModified();
                    }
                }
                if(removeFile != null) {
                    sdkLogFiles.remove(removeFile);
                    sdkFileZipSize += removeFile.length() * AVERAGE_LOG_ZIP_COMPRESSION_RATIO;
                    removeFile = sdkLogFiles.get(0);
                    time = sdkLogFiles.get(0).lastModified();
                }
            }
        }
//        destLogFiles.add(sdkDir);
        //将UNCAUGHT_EXCEPTIONS日志加入的压缩列表
        MLog.info(this,"collectLogBySize() : collecting UNCAUGHT_EXCEPTIONS log");
        File uncaughtExceptionFile = new File(uncaughtExceptionsLogsPath());
        if(uncaughtExceptionFile.exists()) {
            destLogFiles.add(new File(uncaughtExceptionsLogsPath()));
        }
        /*****************end*********************/
        /****收集时间点附近日志填充size****/
        MLog.info(this,"collectLogBySize() : collecting normal logs around this time point(" + collectTimePoint + ")");
        long intervalTime = 0L, zipSize = 0L;
        for (File file : files) {
            /*距离传入时间点的相对时间*/
            if(containsPattern(file)) {
                intervalTime = Math.abs(parseLogCreateTime(file) - collectTimePoint);
                fileList.put(intervalTime, file.getAbsolutePath());
            }
        }
        Iterator iterator = fileList.entrySet().iterator();
        while(iterator.hasNext() && residualSize > 0) {
            Map.Entry<Long,String> entry = (Map.Entry<Long, String>) iterator.next();
            String value = entry.getValue();
            File f = new File(value);
            if(f.exists() && !f.isDirectory()) {
                if(checkCompressed(f.getName())) {
                    residualSize -= f.length();
                    srcLogFiles.add(f);
                } else {
                    residualSize -= f.length() * AVERAGE_LOG_ZIP_COMPRESSION_RATIO;
                    srcLogFiles.add(f);
                }
            }
        }
        //算SD卡容量
        if(getSDFreeSize() <= zipSize * 10) {
            if(listener != null)
                listener.onCompressError(SD_CARD_NOT_ENOUGH_FREE_SIZE);
            return false;
        }
        //临时文件夹
        final File temp = new File(tempDir);
        if(temp.exists() && temp.isDirectory()) {
            deleteDir(temp);
        }

        ScheduledTask.getInstance().scheduledDelayed(new Runnable() {
            @Override
            public void run() {
                //将要上传的日志先进行解压
                MLog.info(this,"collectLogBySize() : Logs packing task started");
                if(srcLogFiles.size() > 0) {
                    for (File file : srcLogFiles) {
                        if (checkCompressed(file.getName())) {
                            try {
                                //检查：压缩文件长度若小于200基本可判定为异常文件，删除，防止解压时出异常
                                if(file.length() < 200) {
                                    removeLogFile(file);
                                } else {
                                    LogZipCompress.getInstance().decompress(file, tempDir);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            destLogFiles.add(file);
                        }
                    }
                }
                File[] tempFiles = new File(tempDir).listFiles();
                if(tempFiles != null) {
                    for(File file : tempFiles) {
                        if(!destLogFiles.contains(file)) {
                            destLogFiles.add(file);
                        }
                    }
                }
                if(destLogFiles.size() > 0) {
                    Pair<Integer,String> pack = LogZipCompress.getInstance().compressFiles(destLogFiles, sdkLogFiles, uid);
                    if(pack.first !=0 || TextUtils.isEmpty(pack.second)) {
                        if(listener != null)
                            listener.onCompressError(pack.first);
                    } else {
                        if(listener != null)
                            listener.onCompressFinished(pack.second);
                    }
                }
                deleteDir(temp);
                MLog.info(this, "collectLogBySize() : Logs packing task finished");

            }
        },0);

        return true;
    }
    /**
     * 根据log文件名来解析该文件的创建时间，解析不到则返回文件最后修改时间
     * @param logFile
     * @return 创建时间
     */
    public long parseLogCreateTime(File logFile) {
        long time = logFile.lastModified();
        if(logFile.getName().contains(".")) {
            String logName = logFile.getName().substring(0, logFile.getName().indexOf("."));
            Matcher matcher = PATTERN.matcher(logName);
            if (matcher.find()) {
                String dateStr = logName.substring(matcher.start(), matcher.end());
                try {
                    time = new SimpleDateFormat(LOG_DATE_FORMAT_STR).parse(dateStr).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return time;
    }

    /**
     * 检查文件的文件名是否包含符合正则表达式的串
     * @param file
     * @return
     */
    public boolean containsPattern(File file) {
        String name = file.getName();
        Matcher matcher = PATTERN.matcher(name);
        return matcher.find();
    }

    /**
     * 检查扩展名来判断文件是否是压缩文件
     * @param fileName
     * @return
     */
    public boolean checkCompressed(String fileName) {
        if(fileName.endsWith(".zip") || fileName.endsWith(".7z")) {
            return true;
        }
        return false;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return true：删除完毕 false：递归的删除过程出现了失败
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 获取SD卡剩余空间
     * @return
     */
    public long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        return freeBlocks * blockSize;  //单位Byte
    }

}
