package com.drumge.template;

import android.util.Printer;

import com.drumge.template.log.MLog;

/**
 * MLog实现的printer
 * Created by Zhongyongsheng on 16/7/8.
 */
public class MLogPrinter implements Printer {

    private Object mTag = "";

    public MLogPrinter(Object tag){
        this.mTag = tag;
    }

    @Override
    public void println(String x) {
        MLog.info(mTag, x);
    }
}
