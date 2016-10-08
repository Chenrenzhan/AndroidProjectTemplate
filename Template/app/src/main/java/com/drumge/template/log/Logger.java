/**
 * Log类。可以直接使用静态函数
 * 也可以用某个tag生成一个logger对象
 * 使用前需要先调用init初始化
 * 内部使用android的Log类实现，并支持写入文件
 */
package com.drumge.template.log;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.drumge.template.BasicConfig;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author daixiang
 *
 */
@SuppressLint("SimpleDateFormat")
public class Logger {

	public enum LogLevel {
		Verbose,
		Debug,
		Info,
		Warn,
		Error
	}

	// 写log文件策略
	public enum LogFilePolicy {
		NoLogFile,          // 不写文件
		PerDay,             // 一天只产生一个log文件
		PerLaunch           // 每次运行均产生一个log文件
	}

	public static class LogConfig {
		public String dir;                  // log文件目录，绝对路径
		public LogFilePolicy policy;
		public LogLevel outputLevel;        // 输出级别，大于等于此级别的log才会输出
		public LogLevel fileLevel;          // 输出到文件的级别，大于等于此级别的log才会写入文件
		public int fileFlushCount;          // 每次累计log超过此条数时，会检查是否需要flush log文件
		public int fileFlushInterval;       // 定时每隔一定秒数检查是否需要flush log文件
		public int fileFlushMinInterval;    // 距离上次flush最少需要多少秒

		public LogConfig() {
			policy = LogFilePolicy.PerLaunch;
			outputLevel = LogLevel.Verbose;
			fileLevel = LogLevel.Info;
			fileFlushCount = 10;
			fileFlushInterval = 60;
			fileFlushMinInterval = 10;
		}
		public LogConfig(LogConfig cfg) {
			this.dir = cfg.dir;
			this.policy = cfg.policy;
			this.outputLevel = cfg.outputLevel;
			this.fileLevel = cfg.fileLevel;
			this.fileFlushCount = cfg.fileFlushCount;
			this.fileFlushInterval = cfg.fileFlushInterval;
			this.fileFlushMinInterval = cfg.fileFlushMinInterval;
		}
	}


	private static ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<String, Logger>();
//	private static Context context;
	private static LoggerThread loggerThread;   // 用于在另一个线程写log文件

	private static LogConfig config = new LogConfig();
	// 写文件线程未准备好的时候，将可以写入文件的log先缓存起来
	private static List<String> logList = Collections.synchronizedList(new ArrayList<String>());

	private String myTag;

	private Logger(String tag) {
		myTag = tag;
	}

	public String getTag() {
		return myTag;
	}

//	public static void init(Context ctx) {
//
//		LogConfig cfg = new LogConfig();
//		if (ctx != null) {
//			File f = ctx.getExternalCacheDir();
//			if (f != null
//					&& Environment.getExternalStorageState().equals(
//							Environment.MEDIA_MOUNTED)) {
//				Log.i("Logger", "cache dir = " + f.getAbsolutePath());
//				cfg.dir = f.getAbsolutePath() + "/logs";
//			} else {
//				Log.i("Logger", "no extenal storage available");
//				f = ctx.getCacheDir();
//				if (f != null) {
//					cfg.dir = f.getAbsolutePath() + "/logs";
//				}
//			}
//		}
//		cfg.policy = LogFilePolicy.PerLaunch;
//		cfg.outputLevel = LogLevel.Verbose;
//		cfg.fileLevel = LogLevel.Info;
//
//		Logger.init(ctx, cfg);
//	}

	/**
	 * 使用Logger之前，必须先init
	 * @param cfg
	 */
	public static void init(LogConfig cfg) {

//		context = ctx;
		info("Logger", "init Logger");
		config = new LogConfig(cfg);

//		if (config.policy != LogFilePolicy.NoLogFile && loggerThread == null) {
//		    loggerThread = new LoggerThread("LoggerThread", config);
//		    loggerThread.start();
//		}

        initMLog(config);
	}

    public static void initMLog(LogConfig cfg) {
        if (cfg.policy != LogFilePolicy.NoLogFile) {
            String logDir = cfg.dir;
            MLog.LogOptions options = new MLog.LogOptions();
            if (BasicConfig.getInstance().isDebuggable()) {
                options.logLevel = MLog.LogOptions.LEVEL_VERBOSE;
            } else {
                options.logLevel = MLog.LogOptions.LEVEL_INFO;
            }
            options.honorVerbose = false;
            options.logFileName = "logs.txt";
            MLog.initialize(logDir, options);
            MLog.info("Logger", "init MLog, logFilePath = " + logDir + File.separator + options.logFileName);
        }
    }

	public static Logger getLogger(String tag) {
		if (TextUtils.isEmpty(tag)) {
			tag = "Default";
		}
        Logger logger;
        try {
            logger = loggers.get(tag);
            if (logger == null) {
                logger = new Logger(tag);
                loggers.put(tag, logger);
            }
        } catch (Exception e) {
            MLog.error("Logger", "getLogger error! " + e);
            logger = new Logger(tag);
        }

		return logger;
	}

	public static Logger getLogger(Class<?> cls) {
		if (cls == null) {
			return Logger.getLogger("");
		}

//		String className = cls.getName();
//		String tag = className.substring(className.lastIndexOf(".") + 1);
		return Logger.getLogger(cls.getSimpleName());
	}

	private static boolean isLoggable(LogLevel level) {
		return level.compareTo(config.outputLevel) >= 0;
	}

	private static String levelToString(LogLevel level) {
		String str = "";
		switch (level) {
		case Debug:
			str = "Debug";
			break;
		case Error:
			str = "Error";
			break;
		case Info:
			str = "Info";
			break;
		case Verbose:
			str = "Verbose";
			break;
		case Warn:
			str = "Warn";
			break;
		default:
			str = "Debug";
			break;
		}
		return str;
	}

	public static String getLogFilePath() {
		if (loggerThread != null) {
			return loggerThread.getFilePath();
		} else {
			return null;
		}
	}

	private static void logToFile(String tag, LogLevel level, String message, Throwable t) {

		if (config.policy != LogFilePolicy.NoLogFile) {
			if (loggerThread == null || !loggerThread.isReady()) {
				// 文件线程未准备好，先缓存
				logList.add(LoggerThread.getFormattedString(tag, level, message));
			} else {
				loggerThread.logToFile(tag, level, message, t);
			}
		}
	}

	public static void log(String tag, LogLevel level, String message) {
		if (Logger.isLoggable(level)) {
            message = msgForTextLog(tag, message);
			switch (level) {
			case Debug:
//				Log.d(tag, message);
                MLog.debugWithoutLineNumber(tag, message);
				break;
			case Error:
//				Log.e(tag, message);
				MLog.errorWithoutLineNumber(tag, message);
				break;
			case Info:
//				Log.i(tag, message);
				MLog.infoWithoutLineNumber(tag, message);
				break;
			case Verbose:
//				Log.v(tag, message);
                MLog.verboseWithoutLineNumber(tag, message);
				break;
			case Warn:
//				Log.w(tag, message);
                MLog.warnWithoutLineNumber(tag, message);
				break;
			default:
//				Log.d(tag, message);
                MLog.debugWithoutLineNumber(tag, message);
				break;
			}
//			logToFile(tag, level, message, null);
		}
	}

	private static void logError(String tag, String msg, Throwable tr) {
		if (Logger.isLoggable(LogLevel.Error)) {
//            msg = msgForTextLog(tag, msg);
			if (tr == null) {
//				Log.e(tag, msg);
                MLog.error(tag, msg);
			} else {
//				Log.e(tag, msg, tr);
                MLog.error(tag, msg, tr);
			}
//		    logToFile(tag, LogLevel.Error, msg, tr);
		}
	}

//	public static void log(String tag, LogLevel level, String message, Throwable throwable) {
//		switch (level) {
//		case Debug:
//			Log.d(tag, message, throwable);
//			break;
//		case Error:
//			Log.e(tag, message, throwable);
//			break;
//		case Info:
//			Log.i(tag, message, throwable);
//			break;
//		case Verbose:
//			Log.v(tag, message, throwable);
//			break;
//		case Warn:
//			Log.v(tag, message, throwable);
//			break;
//		default:
//			Log.d(tag, message, throwable);
//			break;
//		}
//	}

    private static String msgForTextLog(String tag, String message) {
        if (message == null) {
            message = "null";
        }
        int line = -1;
        String filename = null;
        if (Thread.currentThread().getStackTrace().length > 4) {
            line = Thread.currentThread().getStackTrace()[4].getLineNumber();
            filename = Thread.currentThread().getStackTrace()[4].getFileName();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(tag);
        sb.append("] ");
        sb.append(message);
        sb.append("(P:");
        sb.append(android.os.Process.myPid());
        sb.append(")");
        sb.append("(T:");
        if (Looper.getMainLooper() == Looper.myLooper())
            sb.append("Main&");
        else
            sb.append(Thread.currentThread().getId());
        sb.append(")");
//        sb.append("(C:");
//        sb.append(tag);
//        sb.append(")");
        if (filename != null) {
            sb.append(" at (");
            sb.append(filename);
        }
        if (line > 0) {
            sb.append(":");
            sb.append(line);
            sb.append(")");
        }
        return sb.toString();
    }

	public static void verbose(String tag, String message) {
//        message = msgForTextLog(tag, message);
		Logger.log(tag, LogLevel.Verbose, message);
	}

	public static void debug(String tag, String message) {
//        message = msgForTextLog(tag, message);
		Logger.log(tag, LogLevel.Debug, message);
	}

	public static void info(String tag, String message) {
//        message = msgForTextLog(tag, message);
		Logger.log(tag, LogLevel.Info, message);
	}

	public static void warn(String tag, String message) {
//        message = msgForTextLog(tag, message);
		Logger.log(tag, LogLevel.Warn, message);
	}

	public static void error(String tag, String message) {
//        message = msgForTextLog(tag, message);
		Logger.log(tag, LogLevel.Error, message);
	}

	public static void error(String tag, String message, Throwable throwable) {
//        message = msgForTextLog(tag, message);
		Logger.logError(tag, message, throwable);
	}

	public void verbose(String message) {
        Logger.verbose(myTag, message);
//        message = msgForTextLog(myTag, message);
//        MLog.verboseWithoutLineNumber(myTag, message);
	}

	public void debug(String message) {
        Logger.debug(myTag, message);
//        message = msgForTextLog(myTag, message);
//		MLog.debugWithoutLineNumber(myTag, message);
	}

	public void info(String message) {
        Logger.info(myTag, message);
//        message = msgForTextLog(myTag, message);
//		MLog.infoWithoutLineNumber(myTag, message);
	}

	public void warn(String message) {
        Logger.warn(myTag, message);
//        message = msgForTextLog(myTag, message);
//		MLog.warnWithoutLineNumber(myTag, message);
	}

	public void error(String message) {
        Logger.error(myTag, message);
//        message = msgForTextLog(myTag, message);
//		MLog.errorWithoutLineNumber(myTag, message);
	}

	public void error(String message, Throwable throwable) {
        Logger.logError(myTag, message, throwable);
//        message = msgForTextLog(myTag, message);
//        MLog.errorWithoutLineNumber(myTag, message, throwable);
	}

	public static void onTerminate() {
		if (loggerThread != null) {
			loggerThread.sendFlush();
		}
	}

//	private static class SdkLogger implements ILog {
//
//		@Override
//		public void verbose(String tag, String msg) {
//
//			Logger.verbose(tag, msg);
//		}
//
//		@Override
//		public void debug(String tag, String msg) {
//
//			Logger.debug(tag, msg);
//		}
//
//		@Override
//		public void info(String tag, String msg) {
//
//			Logger.info(tag, msg);
//		}
//
//		@Override
//		public void warn(String tag, String msg) {
//
//			Logger.warn(tag, msg);
//		}
//
//		@Override
//		public void error(String tag, String msg) {
//
//			Logger.error(tag, msg);
//		}
//
//		@Override
//		public void error(String tag, String msg, Throwable t) {
//			Logger.error(tag, msg, t);
//		}
//
//	}

	/**
	 * 用于写log文件的线程
	 * @author daixiang
	 *
	 */
	private static class LoggerThread extends Thread {

		private static final int LogMessageType = 0;
		private static final int TimerMessageType = 1;
		private static final int LogThrowableType = 2;
		private static final int FlushLog = 3;

		private LogThreadHandler handler;  // 使用此handler将log消息发到此线程处理
		private LogConfig config;
		private String filePath;
		private boolean isReady = false;

		public LoggerThread(String name, LogConfig cfg) {
			super(name);
			config = cfg;
		}

		public boolean isReady() {
			return isReady;
		}

		private static String getFormattedString(String tag, LogLevel level, String msg) {

			String thread = (Looper.getMainLooper() == Looper.myLooper()) ? "[Main]"
					: ("[" + Thread.currentThread().getId() + "]");
			String strLevel = "[" + Logger.levelToString(level) + "]";
			String logMsg = thread + "[" + tag + "]" + strLevel + " " + msg;
			return logMsg;
		}

		public void logToFile(String tag, LogLevel level, String msg, Throwable t) {
			if ((config.policy != LogFilePolicy.NoLogFile)
					&& (level.compareTo(config.fileLevel) >= 0)
					&& (handler != null)) {

				String logMsg = getFormattedString(tag, level, msg);

				Message threadMessage = null;
				if (t == null) {
				    threadMessage = handler.obtainMessage(LogMessageType);
				    threadMessage.obj = logMsg;
				} else {
					threadMessage = handler.obtainMessage(LogThrowableType);
					threadMessage.obj = logMsg;
					Bundle b = new Bundle();
				    b.putSerializable("throwable", t);
				    threadMessage.setData(b);
				}

				if (threadMessage != null) {
					handler.sendMessage(threadMessage);
				}
			}
		}

		public void sendFlush() {
			if (handler != null) {
				handler.sendEmptyMessage(FlushLog);
			}
		}

//		public void logToFile(String tag, LogLevel level, String msg) {
//			logToFile(tag, level, msg, null);
//		}

		public String getFilePath() {
			return filePath;
		}

		public void run() {

			Looper.prepare();

			File logDir = new File(config.dir);
			if (!logDir.exists()) {
				Logger.info("Logger", "create log dir: " + logDir.getAbsolutePath());
				logDir.mkdirs();
			}

			SimpleDateFormat f;
			if (config.policy == LogFilePolicy.PerLaunch) {
				f = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
			} else {
				f = new SimpleDateFormat("yyyy-MM-dd");
			}

			filePath = config.dir + "/" + f.format(new Date()) + ".log";
			Logger.info("Logger", "log file name: " + filePath);

			handler = new LogThreadHandler(this);
			isReady = true;

			// 将之前缓存的log先写入文件
			List<String> list = new ArrayList<String>(logList);
			try {
				if (list.size() > 0) {
					Logger.debug("Logger", "write logs before logger thread ready to file: " + list.size());
					for (String s : list) {
						handler.writeLine(s);
					}
					handler.flush(true);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logList.clear();
			list.clear();
			list = null;

			Looper.loop();
		}

		private static class LogThreadHandler extends Handler {

			private SimpleDateFormat dateFormat;
			private BufferedWriter writer;
			private LoggerThread loggerThread;
			private int logCounter;
			private long lastFlushTime;

			private void writeLine(String formattedStr) throws IOException {
				if (writer != null) {
					writer.write(dateFormat.format(new Date()) + " " + formattedStr);
					writer.newLine();
				}
			}

			public LogThreadHandler(LoggerThread thread) {
				loggerThread = thread;
				dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				boolean append;
				if (loggerThread.config.policy == LogFilePolicy.PerLaunch) {
					append = false;

				} else {
					append = true;
				}

				try {
					FileWriter fw = new FileWriter(loggerThread.filePath, append);
					writer = new BufferedWriter(fw);
					if (loggerThread.config.policy == LogFilePolicy.PerDay) {
						writer.newLine();
					}
//					writer.write(dateFormat.format(new Date()) + " " + loggerThread.getFormattedString("Logger", loggerThread.config.fileLevel, "---------------------Log Begin---------------------"));
//					writer.newLine();

					// 在文件开头加入一个易于识别的行
					writeLine(getFormattedString("Logger", loggerThread.config.fileLevel, "---------------------Log Begin---------------------"));
					flush(true);
				} catch (IOException e) {
					writer = null;
					e.printStackTrace();
				}

				if (writer != null && loggerThread.config.fileFlushInterval > 0) {
					long time = loggerThread.config.fileFlushInterval * 1000;
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							Message msg = obtainMessage(TimerMessageType);
							sendMessage(msg);
						}
					}, time, time);
				}
			}

			public void flush(boolean force) throws IOException {
				if (writer != null) {

					long now = System.currentTimeMillis();
					// 不要太频繁flush，最低间隔
					if ((now - lastFlushTime) > (loggerThread.config.fileFlushMinInterval * 1000)) {
						writer.flush();
						lastFlushTime = System.currentTimeMillis();
						logCounter = 0;
					} else {
						logCounter++;
					}
				}

			}

			public void flushIfNeeded() throws IOException {
				if (logCounter > loggerThread.config.fileFlushCount) {
					flush(false);
				} else {
					logCounter++;
				}
			}

			@Override
			public void handleMessage(Message msg) {

				if (writer == null) {
					return;
				}

				try {
					switch (msg.what) {

					case LogMessageType:
					{
//						String str = dateFormat.format(new Date()) + " "
//								+ msg.obj;
//						writer.write(str);
//						writer.newLine();
						writeLine((String)msg.obj);
						flushIfNeeded();
						break;
					}
					case LogThrowableType:
					{
//						String str = dateFormat.format(new Date()) + " "
//								+ msg.obj;
//						writer.write(str);
//						writer.newLine();
						writeLine((String)msg.obj);
						Bundle data = msg.getData();
						if (data != null) {
							Throwable t = (Throwable) data
									.getSerializable("throwable");
							if (t != null) {
								PrintWriter pw = new PrintWriter(writer);
								t.printStackTrace(pw);
								//pw.close();   // 不能close，否则内部的bufferedwriter也会被close！
								writer.newLine();
								flush(true);  // 异常，立刻flush
							} else {
								flushIfNeeded();
							}
						} else {
							flushIfNeeded();
						}
						break;
					}
					case TimerMessageType:
						flush(false);
						break;
					case FlushLog:
						flush(true);
						break;
					default:
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
