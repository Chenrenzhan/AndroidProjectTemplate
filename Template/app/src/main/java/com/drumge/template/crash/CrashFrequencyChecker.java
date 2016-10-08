package com.drumge.template.crash;


import com.drumge.template.log.MLog;

/**
 * Created by wang duo on 2016/8/1.
 * 崩溃频率检查（及相关操作）
 */
public class CrashFrequencyChecker {

    private static CrashFrequencyChecker sInst;
//    private static final long TIME_INTERVAL = 30 * 60 * 1000;//30 min
    private static final int CRASH_TIMES_LIMIT = 20;//20次

    public synchronized static CrashFrequencyChecker instance() {
        if(sInst == null){
            sInst = new CrashFrequencyChecker();
        }
        return sInst;
    }

    /**
     * 增加一次崩溃次数
     */
    public void addCrashCountRecord() {
        MLog.info(this,"addCrashCountRecord()");
        int crashTimes = getCrashTimesRecord();
        //如果是第一次崩，记录时间点
        if(crashTimes == 0) {
            MLog.info(this, "first crash happens.");
            CrashPref.instance().putCrashTimePoint(System.currentTimeMillis());
        }
        crashTimes++;
        CrashPref.instance().putCrashTimes(crashTimes);
        MLog.flush();
    }

    /**
     * 崩溃频率检查，崩溃过于频繁，返回true。
     */
    public boolean crashFrequencyCheck() {
        MLog.info(this,"crashFrequencyCheck() called.");
//        long now = System.currentTimeMillis();
//        long record = getCrashTimePointRecord();
        int times = getCrashTimesRecord();
        if(times >= CRASH_TIMES_LIMIT) {
            MLog.info(this,"Crash frequency reachs the limit !");
//            resetLocalCrashRecord();//暂不重置崩溃次数
            return true;
        }
        //暂不做时间检查
//        if(now - record > TIME_INTERVAL) {
//            MLog.info(this,"Crash timepoint is overdue.");
//            resetLocalCrashRecord();
//        }
        return false;
    }

    /**
     * 获取本地记录的崩溃次数
     * @return
     */
    private int getCrashTimesRecord() {
        int times = CrashPref.instance().getCrashTimes();
        MLog.info(this,"getCrashTimesRecord : " + times);
        return times;
    }

    /**
     * 获取第一次崩溃发生的时间点
     * @return
     */
    private long getCrashTimePointRecord() {
        long timepoint = CrashPref.instance().getCrashTimePoint();
        MLog.info(this,"getCrashTimePointRecord : " + timepoint);
        return timepoint;
    }

    /**
     * 重置崩溃次数的相关记录
     */
    public void resetLocalCrashRecord() {
        MLog.info(this, "resetLocalCrashRecord()");
        CrashPref.instance().putCrashTimes(0);
        CrashPref.instance().putCrashTimePoint(System.currentTimeMillis());
    }

}
