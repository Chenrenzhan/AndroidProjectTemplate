package com.drumge.template.crash;

import android.content.Context;
import android.content.SharedPreferences;

import com.drumge.template.BasicConfig;
import com.drumge.template.YSharedPref;
import com.drumge.template.log.MLog;


/**
 * Created by wang duo on 2016/8/3.
 */
public class CrashPref extends YSharedPref {
    private static final String CRASH_FREQUENCY_CHECK = "crash_frequence_check_pref";//崩溃频率记录
    private static final String CRASH_COUNT = "crash_count";//崩溃次数记录
    private static final String CRASH_TIMEPOINT = "crash_time_point";//崩溃时间点记录

    private static CrashPref sInst;

    private CrashPref(SharedPreferences preferences){
        super(preferences);
    }

    public synchronized static CrashPref instance() {
        if(sInst == null){
            SharedPreferences pref = BasicConfig.getInstance().getAppContext().getSharedPreferences(CRASH_FREQUENCY_CHECK, Context.MODE_PRIVATE);
            sInst = new CrashPref(pref);
        }
        return sInst;
    }

    public void putCrashTimePoint(long value){
        MLog.info(this,"putCrashTimePoint() : " + value);
        putLong(CRASH_TIMEPOINT, value);
    }
    public void putCrashTimes(int value){
        MLog.info(this,"putCrashTimes() : " + value);
        putInt(CRASH_COUNT, value);
    }
    public long getCrashTimePoint(){
        MLog.info(this,"getCrashTimePoint() called");
        return getLong(CRASH_TIMEPOINT, System.currentTimeMillis());
    }
    public int getCrashTimes(){
        MLog.info(this,"getCrashTimes() called");
        return getInt(CRASH_COUNT,0);
    }
}
