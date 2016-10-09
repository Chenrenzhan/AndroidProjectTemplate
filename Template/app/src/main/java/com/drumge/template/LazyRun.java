package com.drumge.template;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.drumge.template.log.MLog;

import java.io.File;
import java.util.HashMap;

/**
 * 延迟初始化，避免一开始在Application中初始化的配置过多，无需再app启动立即完成初始化的可以在此初始化
 */
public class LazyRun implements Runnable {
    private Context mContext;

    public LazyRun(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MLog.info(this, "initAPatchManager!");
            }
        });
        long startTime = System.currentTimeMillis();


        TemplateApp.isFirstMsg = new HashMap();

        coreManagerInit();




        long cost = System.currentTimeMillis() - startTime;
        MLog.info(this, "LazyRun cost : " + cost);
    }

    private void coreManagerInit() {
        String version = VersionUtil.getLocalVer(mContext).getVersionNameWithoutSnapshot();
        version = "yymand" + version;



    }

    private static byte[] getLogPath(){
        File dir = BasicConfig.getInstance().getLogDir();
        if (dir != null){
            File sdkLogDir = new File(dir, "sdklog");
            if (!sdkLogDir.exists()){
                sdkLogDir.mkdirs();
            }
            String path = sdkLogDir.getAbsolutePath();
            if (path != null){
                return path.getBytes();
            }
        }
        return null;
    }

}
