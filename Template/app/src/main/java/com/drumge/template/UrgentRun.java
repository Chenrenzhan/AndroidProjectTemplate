package com.drumge.template;

import android.app.Application;
import android.content.Context;

import com.drumge.template.crash.ANRDetector;
import com.drumge.template.log.MLog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by xianjiachao on 2016/7/6.
 */
public class UrgentRun implements Runnable {
    public final static int RUNNING_STATE_NOT_RUN = 0;
    public final static int RUNNING_STATE_RUNNING = 1;
    public final static int RUNNING_STATE_DONE = 2;

    public volatile static int sRunningState = RUNNING_STATE_NOT_RUN;
    public static SpinLock sLock = new SpinLock();

    private Context mContext;
    public Callback callback;
    private boolean mSyncRun;

    public UrgentRun(Context context) {
        mContext = context;
    }

    public static void ensureUrgentRun() {
        MLog.info("UrgentRun", "ensureUrgentRun. state : " + UrgentRun.sRunningState);
        if (UrgentRun.sRunningState == UrgentRun.RUNNING_STATE_NOT_RUN) {
            UrgentRun urgentRun = new UrgentRun(TemplateApp.gContext);
            urgentRun.mSyncRun = true;
            urgentRun.runSync();
            urgentRun.run();
        } else if (UrgentRun.sRunningState == UrgentRun.RUNNING_STATE_RUNNING) {
            UrgentRun.sLock.lock(); // 阻塞等待初始化完成
            UrgentRun.sLock.unlock();
        }

    }

    public static final void ensureUrgentRunAsync(Callback callback) {
        MLog.info("UrgentRun", "ensureUrgentRunAsync. state : " + UrgentRun.sRunningState);
        if (UrgentRun.sRunningState == UrgentRun.RUNNING_STATE_NOT_RUN) {
            UrgentRun urgentRun = new UrgentRun(TemplateApp.gContext);
            urgentRun.callback = callback;
            urgentRun.runSync();
            ScheduledTask.getInstance().scheduledDelayed(urgentRun, 0);
        } else if (UrgentRun.sRunningState == UrgentRun.RUNNING_STATE_RUNNING) {
            UrgentRun.sLock.lock(); // 阻塞等待初始化完成
            UrgentRun.sLock.unlock();
            callback.onRunFinished(RUNNING_STATE_RUNNING);
        } else {
            callback.onRunFinished(RUNNING_STATE_DONE);
        }
    }

    public void runSync() {
        // TODO 初始化okhttputils 
//        RequestManager.instance().init(mContext, ICoreManager.YYMOBILE_DIR_NAME + File.separator + "http");
//        ImageManager.instance().init(mContext, ICoreManager.YYMOBILE_DIR_NAME + File.separator + "image");
    }

    @Override
    public void run() {
        sLock.lock();
        try {
            if (sRunningState != RUNNING_STATE_NOT_RUN) {
                return;
            }

            sRunningState = RUNNING_STATE_RUNNING;

            new ANRDetector().start();//检测ANR
            RapidBoot.sStopWatch.split("ANRDetector");

            RapidBoot.sStopWatch.split("UrgentRun end");
            sRunningState = RUNNING_STATE_DONE;
            if (callback != null) {
                callback.onRunFinished(RUNNING_STATE_NOT_RUN);
            }

            mContext = null;
            callback = null;

        } finally {
            sLock.unlock();
        }

        onRunFinish();
    }

    private void onRunFinish() {
        if (mSyncRun) {
            new LazyRun(TemplateApp.gContext).run();
        } else {
            ScheduledTask.getInstance().scheduledDelayed(new LazyRun(TemplateApp.gContext), 0);
        }
    }

    //通过反射获取leakcanary实例 com.yy.mobile.leakcanary.YYLeakcanary
    public void initLeakCanary(){
        MLog.info(this,"zy initLeakCanary start");
        try {
            Class<?> leakcanaryClass = Class.forName("com.yy.mobile.leakcanary.YYLeakcanary");
            Object invokeLeakcanary = leakcanaryClass.getConstructor(new Class[]{}).newInstance(new Object[]{});//使用默认构造函数获取对象
            Method addMethod = leakcanaryClass.getMethod("init", new Class[]{Application.class});
            addMethod.invoke(invokeLeakcanary,mContext);
        } catch (ClassNotFoundException e) {
            MLog.error(this,"zy leakcanary init error =" +  e);
        } catch (NoSuchMethodException e) {
            MLog.error(this,"zy leakcanary init error =" +  e);
        } catch (InstantiationException e) {
            MLog.error(this,"zy leakcanary init error =" +  e);
        } catch (IllegalAccessException e) {
            MLog.error(this,"zy leakcanary init error =" +  e);
        } catch (InvocationTargetException e) {
            MLog.error(this,"zy leakcanary init error =" +  e);
        }
    }

    public interface Callback {
        /**
         * 返回的值是调用{@link UrgentRun#ensureUrgentRunAsync(Callback)}前的状态
         * @param state
         */
        void onRunFinished(int state);
    }
}
