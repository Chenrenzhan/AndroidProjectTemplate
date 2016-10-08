package com.drumge.template;


import android.content.Context;

public class AppInfo {
	
	private static final String TAG = "AppInfo";

	private static AppInfo mInstance = null;
	private static int mVersion = 0; // TODO 日志目录
	
	private Context mContext = null;
	private String mLogPath = ""; // TODO 日志目录
	private String mLogName = "pushsvc_log.txt";
	private String mLogNameBak = "pushsvc_logbak.txt";
	
	public static AppInfo instance() {
		if (mInstance == null) {
			mInstance = new AppInfo();
		}
		return mInstance;
	}
	
	private AppInfo() {
		
	}
	
	public String getAppPacketName() {
		return mContext.getPackageName();
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public int getVersion() {
		return mVersion;
	}
	
	public void setLogPath(String logPath) {
		mLogPath = logPath;
	}
	
	public String getLogPath() {
		return mLogPath;
	}
	
	public String getLogName() {
		return mLogName;
	}
	
	public String getLogNameBak() {
		return mLogNameBak;
	}
}
