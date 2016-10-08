package com.drumge.template;

import android.util.Log;
import android.util.LogPrinter;
import android.util.Printer;

/**
 * Created by Zhongyongsheng on 16/7/8.
 */
public class RapidBoot {

    public static StopWatch sStopWatch = new StopWatch();
    public static Printer sLogPrinter = new LogPrinter(Log.INFO, "RapidBoot");
    public static Printer sMLogPrinter = new MLogPrinter("RapidBoot");

}
