package com.drumge.template;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.drumge.template.common.StringUtil;
import com.drumge.template.log.MLog;

import java.util.ArrayList;
import java.util.List;

public class AppPackageUtil {

	private static final String TAG = "AppPackageUtil";
	private static int mAppKey = 0;
	
	public static int getMyAppKey(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			return appInfo.metaData.getInt("appKey");
			// return appKeyString.length()==0? null : appKeyString.getBytes();
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getMyAppKey error: " + StringUtil.exception2String(e));
			return 0;
		}
	}
	
	public static int getSDKVersion(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			return appInfo.metaData.getInt("sdkversion");
			// return appKeyString.length()==0? null : appKeyString.getBytes();
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getSDKVersion error: " + StringUtil.exception2String(e));
			return 0;
		}
	}
	
	public static String getCurrentProcessName(Context context) {
		try {
			int pid = android.os.Process.myPid();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					return appProcess.processName;
				}
			}
			return null;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getCurrentProcessName error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static boolean isPackageExist(Context context, String packageName) {
		try {
			if (packageName == null || "".equals(packageName)) {
				return false;
			}
			List<PackageInfo> pkgList = context.getPackageManager().getInstalledPackages(0);
			if (pkgList != null) {
				for (PackageInfo pkgInfo: pkgList) {
					if (pkgInfo.packageName.equals(packageName)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.isPackageExist error: " + StringUtil.exception2String(e));
			return false;
		}
	}
	
	public static boolean isActivityExist(Context context, String packageName, String activityName) {
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(packageName, activityName));
			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 
		            PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.isActivityExist error: " + StringUtil.exception2String(e));
			return false;
		}
	}
	
	public static boolean isPackageRunning(Context context, String packageName) {
		try {
			ActivityManager am = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE));
			List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();// 获取系统中运行的APP列表
			for (RunningAppProcessInfo info : processInfos) {
				if (packageName.equals(info.processName) && info.pid != 0) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.isPackageRunning error: " + StringUtil.exception2String(e));
			return false;
		}
	}
	
	public static int getPackagePID(Context context, String serviceName) {
		try {
			ActivityManager am = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE));
			List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();// 获取系统中运行的APP列表
			for (RunningAppProcessInfo info : processInfos) {
				if (serviceName.equals(info.processName) && info.pid != 0) {
					return info.pid;
				}
			}
			return -1;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getPackagePID error: " + StringUtil.exception2String(e));
			return -1;
		}
	}
	
	public static boolean startAppByPackageName(Context context, String packageName) {
		try {
			if (context == null || packageName == null) {
				return false;
			}
			if (isPackageRunning(context, packageName)) {
				return true;
			} else {
				Intent intent = null;
				intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
				if (intent != null) {
					context.startActivity(intent);
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.startAppByPackageName error: " + StringUtil.exception2String(e));
			return false;
		}
	}
	
	public static ArrayList<String> getServiceList(Context context) {
		try {
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				return null;
			}
			ArrayList<String> serviceList = new ArrayList<String>();
			for(int i = 0; i < myList.size(); i++) {
				String className = myList.get(i).service.getClassName().toString();
//				String packageName = myList.get(i).service.getPackageName().toString();
				serviceList.add(className);
			}
			return serviceList;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getServiceList error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static boolean isServiceRunning(Context context, String serviceName) {
		RunningServiceInfo serviceInfo = getServiceInfo(context, serviceName);
		if (serviceInfo != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getServicePackageName(Context context, String serviceName) {
		RunningServiceInfo serviceInfo = getServiceInfo(context, serviceName);
		if (serviceInfo != null) {
			return serviceInfo.service.getPackageName();
		} else {
			return null;
		}
	}
	
	public static int getServicePID(Context context, String serviceName) {
		RunningServiceInfo serviceInfo = getServiceInfo(context, serviceName);
		if (serviceInfo != null) {
			return serviceInfo.pid;
		} else {
			return -1;
		}
	}
	
	public static boolean isServiceRunning(Context context, String packageName, String className) {
		return getServiceInfo(context, packageName, className) != null;
	}
	
	public static int isSvcRunning(Context context, String packageName, String className) {
		try {
			if (context == null || packageName == null || className == null) {
				return -1;
			}
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				return -1;
			}
			for(int i = 0; i < myList.size(); i++) {
				String clsName = myList.get(i).service.getClassName().toString();
				String pkgName = myList.get(i).service.getPackageName().toString();
				int pid = myList.get(i).pid;
				if (clsName.compareTo(className) == 0 && packageName.compareTo(pkgName) == 0 && pid != 0) {
//					Log.e(TAG, TAG + ".getServiceInfo pid=" + myList.get(i).pid + ", clsName=" + clsName + ", pkgName=" + pkgName);
					if (myList.get(i) != null) {
						return 1;
					} else {
						return 0;
					}
				}
			}
			return 0;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.isSvcRunning error: " + StringUtil.exception2String(e));
			return -1;
		}
	}
	
	public static RunningServiceInfo getServiceInfo(Context context, String packageName, String className) {
		try {
			if (context == null || packageName == null || className == null) {
				return null;
			}
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				return null;
			}
			for(int i = 0; i < myList.size(); i++) {
				String clsName = myList.get(i).service.getClassName().toString();
				String pkgName = myList.get(i).service.getPackageName().toString();
				int pid = myList.get(i).pid;
				if (clsName.compareTo(className) == 0 && packageName.compareTo(pkgName) == 0 && pid != 0) {
//					Log.e(TAG, TAG + ".getServiceInfo pid=" + myList.get(i).pid + ", clsName=" + clsName + ", pkgName=" + pkgName);
					return myList.get(i);
				}
			}
			return null;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getServiceInfo error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static void showServiceInfo(Context context, String serviceName) {
		try {
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				return;
			}
			for(int i = 0; i < myList.size(); i++) {
				String clsName = myList.get(i).service.getClassName().toString();
				String pkgName = myList.get(i).service.getPackageName().toString();
				if (clsName.compareTo(serviceName) == 0) {
//					Log.e(TAG, TAG + ".showServiceInfo pid=" + myList.get(i).pid + ", clsName=" + clsName + ", pkgName=" + pkgName);
				}
			}
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.showServiceInfo error: " + StringUtil.exception2String(e));
		}
	}
	
	public static RunningServiceInfo getServiceInfo(Context context, String serviceName) {
		try {
			if (context == null || serviceName == null) {
				MLog.error(TAG, "AppPackageUtil.getServiceInfo svc context == null || serviceName == null");
				return null;
			}
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				MLog.error(TAG, "AppPackageUtil.getServiceInfo svc list == null");
				return null;
			}
			for(int i = 0; i < myList.size(); i++) {
				String className = myList.get(i).service.getClassName().toString();
				String pkgName = myList.get(i).service.getPackageName().toString();
				int pid = myList.get(i).pid;
				if (className != null && pkgName != null 
						&& pkgName.equals(context.getApplicationContext().getPackageName()) && className.compareTo(serviceName) == 0 && pid != 0) {
					return myList.get(i);
				}
			}
			return null;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getServiceInfo error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static String getMyServicePackageName(Context context) {
		try {
			RunningServiceInfo serviceInfo = getMyServiceInfo(context);
			if (serviceInfo != null && serviceInfo.service.getPackageName() != null) {
				return serviceInfo.service.getPackageName();
			} else {
				return null;
			}
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getMyServicePackageName error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static String getMyServiceClassName(Context context) {
		try {
			RunningServiceInfo serviceInfo = getMyServiceInfo(context);
			if (serviceInfo != null && serviceInfo.service.getClassName() != null) {
				return serviceInfo.service.getClassName();
			} else {
				return null;
			}
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getMyServiceClassName error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static RunningServiceInfo getMyServiceInfo(Context context) {
		try {
			if (context == null) {
				return null;
			}
			ActivityManager myAM = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> myList = (List<RunningServiceInfo>)myAM.getRunningServices(Integer.MAX_VALUE);
			if (myList.size() <= 0) {
				return null;
			}
			for(int i = 0; i < myList.size(); i++) {
				if (Process.myPid() == myList.get(i).pid) {
					return myList.get(i);
				}
			}
			return null;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getMyServiceInfo error: " + StringUtil.exception2String(e));
			return null;
		}
	}
	
	public static int getAppKey(Context context) {
		if (mAppKey > 0) {
			return mAppKey;
		}
		if (context == null) {
			return 0;
		}
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			mAppKey = appInfo.metaData.getInt("appKey");
			return mAppKey;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getAppKey error: " + StringUtil.exception2String(e));
			return 0;
		}
	}
	
	public static boolean inServiceProcess(Context context, String serviceName) {
		try {
			return Process.myPid() == AppPackageUtil.getServicePID(context, serviceName);
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.inServiceProcess error: " + StringUtil.exception2String(e));
			return false;
		}
	}
	
	public static void showAllService(Context context) {
		try {
			ActivityManager am = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE));
			List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();// 获取系统中运行的APP列表
//			String mainProcessName = context.getPackageName();// 获取应用的包名
//			int myPid = Process.myPid();// 获取应用的进程ID
			for (RunningAppProcessInfo info : processInfos) {
//				// 只有当service的PID和应用的PID相同，且service的名称与应用包名相同
//				if (info.pid == myPid && mainProcessName.equals(info.processName)) {
//					Log.d(TAG, getClass().getSimpleName() + ".shouldInit has same myPid=" + myPid + ", processName=" + mainProcessName);
//					return;
//				}
				MLog.error(TAG, "AppPackageUtil.inServiceProcess has Pid=" + info.pid + ", processName=" + info.processName);
			}
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.showAllService error: " + StringUtil.exception2String(e));
		}
	}
	
	public static boolean isAppInFrontground(Context context, String pkgName) {
		try {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
	        if (tasksInfo.size() > 0) {
	            //应用程序位于堆栈的顶层
	            if (pkgName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
	                return true;
	            }
	        }
	        return false;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.isAppInFrontground error: " + StringUtil.exception2String(e));
			return false;
		}
    }
	
	/**
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 * 			-2：	参数错误；
	 * 			-1:	错误；
	 * 			0:	不在运行；
	 * 			1：	在前台运行；
	 * 			2：	在后台运行；
	 */
	public static int getAppRunningStatus(Context context, String pkgName) {
		try {
			if (context == null || pkgName == null || pkgName.equals("")) {
				return -2;
			}
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
			if (appProcesses == null || appProcesses.size() == 0) {
				return -1;
			}
			for (RunningAppProcessInfo appInfo: appProcesses) {
				if (appInfo != null && appInfo.processName.equals(pkgName)) {
					if (appInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						return 1;
					} else if (appInfo.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
						return 2;
					} else {
						return 3;
					}
				}
			}
			return 0;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getAppRunningStatus error: " + StringUtil.exception2String(e));
			return -1;
		}
	}
	
	public static int getAppRunningStatus(Context context) {
		try {
			return getAppRunningStatus(context, context.getApplicationContext().getPackageName());
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getAppRunningStatus 2 error: " + StringUtil.exception2String(e));
			return -1;
		}
	}
	
	public static String getDeviceIDFromMobile(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei == null) {
				MLog.error(TAG, "PushService.getDeviceIDFromMobile dev id returns null.");
				imei = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
				if (imei == null) {
					MLog.error(TAG, "PushService.getDeviceIDFromMobile android id returns null.");
					imei = "YY_FAKE_DEV_ID";				
				}
			}
			return imei;
		} catch (Exception e) {
			MLog.error(TAG, "AppPackageUtil.getDeviceIDFromMobile error: " + StringUtil.exception2String(e));
			return "YY_FAKE_DEV_ID";
		}
	}
	
}
