package com.drumge.template.view.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.drumge.template.log.MLog;


/**
 * Created by xujiexing on 2014/8/29.
 */
public class ScrollEnabledViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public ScrollEnabledViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isPagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (this.isPagingEnabled) {
                return super.onTouchEvent(event);
            }
        } catch (Throwable ex) {
            MLog.error(this, "xuwakao, onTouchEvent fix touch viewpager error happens, ev = " + event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            try{
                return super.onInterceptTouchEvent(event);
            }catch (Exception ex){
                MLog.error(this, "onInterceptTouchEvent error", ex);
            }
            return false;
        }
        if(isPagingEnabled == false && event.getAction() == MotionEvent.ACTION_UP){
            MLog.verbose(this, "isPagingEnabled to true");
            isPagingEnabled = true;
        }
        return false;
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}
