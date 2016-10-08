package com.drumge.template.crash;

import com.drumge.template.BasicConfig;
import com.drumge.template.log.LogToES;
import com.drumge.template.log.MLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 崩溃捕获处理，
 * 
 * @author chengaochang
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	private UncaughtExceptionHandler sDefaultHandler;

	public CrashHandler(UncaughtExceptionHandler sDefaultHandler) {
		this.sDefaultHandler = sDefaultHandler;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		try {
			//写到UNCAUGHT_EXCEPTIONS_LOGNAME
			String crashData = collectStackTrace(ex);
            writeTraceToLog(crashData,ex);
            MLog.flush();
            Thread.sleep(1000);//等待1秒，等崩溃日志线程跑一会，写入日志到文件
		} catch (Exception e) {
			MLog.error(this, ex);
		}
		if(sDefaultHandler!=null){
			sDefaultHandler.uncaughtException(thread, ex);
		}
	}

	public  String collectStackTrace(Throwable th) {

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		Throwable cause = th;
		while (cause != null) {
			cause.printStackTrace(printWriter);
			break;// 只取第一层
		}
		String stackTrace = result.toString();
		printWriter.close();
		return stackTrace.trim();
	}

    private  void writeTraceToLog(String traces,Throwable ex) {
    	try {
            MLog.error(this, traces);
			if(BasicConfig.getInstance().isExternalStorageWriteable()) {
				LogToES.writeLogToFile(LogToES.getLogPath(), CrashConfig.UNCAUGHT_EXCEPTIONS_LOGNAME, traces,
						true, System.currentTimeMillis());
			}
        } catch (Exception e) {
            MLog.error(this, e);
        }
    }
}
