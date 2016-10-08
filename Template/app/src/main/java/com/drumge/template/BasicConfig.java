package com.drumge.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.drumge.template.log.MLog;

import java.io.File;

/**
 * Created by xujiexing on 14-6-12.
 */
public class BasicConfig {
    public final static String DIR_NAME = "template";
    
    private Context mContext;
    private boolean isDebuggable;
    private File mLogDir;
    private File mRoot;
    private File mConfigDir;
    private static BasicConfig mInstance;


    public synchronized static BasicConfig getInstance(){
        if(mInstance == null)
            mInstance = new BasicConfig();
        return mInstance;
    }

    private BasicConfig(){

    }

    public void setAppContext(Context context){
        mContext = context;
        setDebuggable(isDebugMode(context));
    }

    private boolean isDebugMode(Context context) {
        boolean debuggable = false;
        ApplicationInfo appInfo = null;
        PackageManager packMgmr = context.getPackageManager();
        try {
            appInfo = packMgmr.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            MLog.error(this, e);
        }
        if (appInfo != null) {
            debuggable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0;
        }
        MLog.verbose(this, "isDebugMode debuggable = %b", debuggable);
        return debuggable;
    }

    public Context getAppContext(){
        return mContext;
    }

    public boolean isDebuggable(){
        return isDebuggable;
    }

    public void setDebuggable(boolean debuggable){
        isDebuggable = debuggable;
    }
    
    

    public void setRootDir(String rootDir){
    	File f = new File(rootDir);
    	if (f != null || !f.exists()){
    		f.mkdirs();
    	}
        this.mRoot = f;
    }

    public File getRootDir(){
        return this.mRoot;
    }

    /**
     * 设置config的目录
     * @param dir
     */
    public void setConfigDir(String dir){
        try {
            mConfigDir = new File(dir);
            if (!mConfigDir.exists()){
                if(!mConfigDir.mkdirs()){
                    MLog.error(this, "Can't create config dir " + mConfigDir);
                    return;
                }
            }
        } catch (Exception e) {
            MLog.error(this, "Set config dir error", e);
        }
    }

    public File getConfigDir(){
        return mConfigDir;
    }

    /**
     * 设置log的目录
     * @param dir
     */
    public void setLogDir(String dir){
        try {
            mLogDir = new File(dir);
            if (!mLogDir.exists()){
                if(!mLogDir.mkdirs()){
                    MLog.error(this, "Can't create log dir " + mLogDir);
                    return;
                }
            }

        } catch (Exception e) {
            MLog.error(this, "Set log dir error", e);
        }
    }

    public File getLogDir(){
        return mLogDir;
    }




    public boolean isExternalStorageAvailable() {
        return mExternalStorageAvailable;
    }

    public boolean isExternalStorageWriteable() {
        return mExternalStorageWriteable;
    }

    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    public void startExternalState() {
        updateExternalStorageState();
        startWatchingExternalStorage();
    }

    public void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public void startWatchingExternalStorage() {
        if (mContext == null) {
            MLog.error(this, "mContext null when startWatchingExternalStorage");
            return;
        }
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MLog.info("ExternalStorageReceiver", "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        mContext.registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    public void stopWatchingExternalStorage() {
        if (mContext == null) {
            MLog.error(this, "mContext null when stopWatchingExternalStorage");
            return;
        }
        mContext.unregisterReceiver(mExternalStorageReceiver);
    }
}
