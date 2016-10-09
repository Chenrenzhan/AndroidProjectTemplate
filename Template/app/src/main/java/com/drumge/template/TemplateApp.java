package com.drumge.template;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.drumge.template.crash.CrashFrequencyChecker;
import com.drumge.template.log.Logger;
import com.drumge.template.log.MLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.OkHttpRequest;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhongyongsheng on 14-6-12.
 */
public class TemplateApp extends Application {

    public static HashMap isFirstMsg;
    public static Context gContext;
    public static boolean gPushRuning;

    @Override
    public void onCreate() {
        super.onCreate();
        RapidBoot.sStopWatch.split("TemplateApp oncreate");
        gContext = this;

        if (isMainProcess()) {
            RapidBoot.sStopWatch.split("After isMainProcess");

            BasicConfig.getInstance().setAppContext(TemplateApp.gContext);
            BasicConfig.getInstance().startExternalState();
            BasicConfig.getInstance().setLogDir(BasicConfig.DIR_NAME + File.separator + "logs");
            BasicConfig.getInstance().setConfigDir(BasicConfig.DIR_NAME + File.separator + "config");
            BasicConfig.getInstance().setRootDir(BasicConfig.DIR_NAME);
            Logger.LogConfig logCfg = new Logger.LogConfig();
            if (BasicConfig.getInstance().getLogDir() != null) {
                logCfg.dir = BasicConfig.getInstance().getLogDir().getAbsolutePath();
            }
            Logger.init(logCfg);
            RapidBoot.sStopWatch.setPrinter(RapidBoot.sMLogPrinter);
            RapidBoot.sStopWatch.split("init logger");


            ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                ActivityManager.RunningTaskInfo info = tasksInfo.get(0);
                StringBuilder stringBuilder = new StringBuilder(128);
                stringBuilder.append("task top:");
                stringBuilder.append(info.topActivity.getClassName());
                stringBuilder.append(", task base:");
                stringBuilder.append(info.baseActivity.getClassName());
                MLog.info(this, stringBuilder.toString());
                if (!info.topActivity.getClassName().equals(SplashActivity.class.getName())
                        || !info.baseActivity.getClassName().equals(SplashActivity.class.getName())) {
                    MLog.info(this, "init in TemplateApp");
                    //崩溃频率检查
                    if(CrashFrequencyChecker.instance().crashFrequencyCheck()) {
                        MLog.info(this,"crashFrequencyCheck() : true");
                        MLog.flush();
                        return;
                    } else {
                        MLog.info(this,"crashFrequencyCheck() : false");
                        UrgentRun.ensureUrgentRun();
                    }
                } else {
                    MLog.info(this, "Normal launch, reset local CrashRecord");
                    CrashFrequencyChecker.instance().resetLocalCrashRecord();//正常进入，重置崩溃次数记录
                }
            }
        } else {
            MLog.info("TemplateApp", "This is remote process just return");
            
        }
    }


    protected boolean isMainProcess() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        int myPid = android.os.Process.myPid();
        String mainProcessName = this.getPackageName();
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getContext() {
        return gContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        RapidBoot.sStopWatch.setPrinter(RapidBoot.sLogPrinter);
        RapidBoot.sStopWatch.start();
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}