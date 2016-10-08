package com.drumge.template.log;

/**
 * Created by xujiexing on 14-3-19.
 */
public class LogCallerUtils {
    private static final String TAG = "LogCallerUtils";
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;


    public static void logStack(String msg) {
        logStack(msg, VERBOSE);
    }

    public static void logStack(String msg, int level) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append(msg + ", caller stack = [ ");
        for (StackTraceElement e : stackTraceElements) {
            sb.append(e.toString() + ", ");
        }
        String logs = sb.substring(0, sb.length() - 2) + " ]";
        switch (level) {
            case DEBUG:
                MLog.debug(TAG, logs);
                break;
            case INFO:
                MLog.info(TAG, logs);
                break;
            case WARN:
                MLog.warn(TAG, logs);
                break;
            case ERROR:
                MLog.error(TAG, logs);
                break;
            default:
                MLog.verbose(TAG, logs);
                break;
        }
    }
}
