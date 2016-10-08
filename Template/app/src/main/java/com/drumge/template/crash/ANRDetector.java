package com.drumge.template.crash;

import android.os.Handler;
import android.os.Looper;

import com.drumge.template.SafeDispatchHandler;
import com.drumge.template.ScheduledTask;
import com.drumge.template.log.MLog;


/**
 * 描述:检测ANR,并且打印日志
 *
 * @author: liyong on 2015/3/17
 */
public class ANRDetector {
    public interface ANRListener {
        public void onAppNotResponding(ANRError error);
    }

    public interface InterruptionListener {
        public void onInterrupted(InterruptedException exception);
    }

    private static final int DEFAULT_ANR_TIMEOUT = 5000;

    private static final ANRListener sDefaultANRListener = new ANRListener() {
        @Override
        public void onAppNotResponding(ANRError error) {
            MLog.error("ANRDetector",error);
        }
    };

    private static final InterruptionListener sDefaultInterruptionListener = new InterruptionListener() {
        @Override
        public void onInterrupted(InterruptedException exception) {
            MLog.info("ANRDetector", "Interrupted: " + exception.getMessage());
        }
    };

    private ANRListener mAnrListener = sDefaultANRListener;
    private InterruptionListener mInterruptionListener = sDefaultInterruptionListener;

    private final Handler mUiHandler = new SafeDispatchHandler(Looper.getMainLooper());
    private final int mTimeoutInterval;

    private String mNamePrefix = "";
    private boolean mLogThreadsWithoutStackTrace = false;

    private volatile int mTick = 0;

    private  final Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            mTick = (mTick + 1) % 10;
        }
    };

    /**
     * Constructs a watchdog that checks the ui thread every {@value #DEFAULT_ANR_TIMEOUT} milliseconds
     */
    public ANRDetector() {
        this(DEFAULT_ANR_TIMEOUT);
    }

    /**
     * Constructs a watchdog that checks the ui thread every given interval
     *
     * @param timeoutInterval The interval, in milliseconds, between to checks of the UI thread.
     *                        It is therefore the maximum time the UI may freeze before being reported as ANR.
     */
    public ANRDetector(int timeoutInterval) {
        super();
        mTimeoutInterval = timeoutInterval;
    }

    /**
     * Sets an interface for when an ANR is detected.
     * If not set, the default behavior is to throw an error and crash the application.
     *
     * @param listener The new listener or null
     * @return itself for chaining.
     */
    public ANRDetector setANRListener(ANRListener listener) {
        if (listener == null) {
            mAnrListener = sDefaultANRListener;
        }
        else {
            mAnrListener = listener;
        }
        return this;
    }

    /**
     * Sets an interface for when the watchdog thread is interrupted.
     * If not set, the default behavior is to just log the interruption message.
     *
     * @param listener The new listener or null.
     * @return itself for chaining.
     */
    public ANRDetector setInterruptionListener(InterruptionListener listener) {
        if (listener == null) {
            mInterruptionListener = sDefaultInterruptionListener;
        }
        else {
            mInterruptionListener = listener;
        }
        return this;
    }

    /**
     * Set the prefix that a thread's name must have for the thread to be reported.
     * Note that the main thread is always reported.
     *
     * @param prefix The thread name's prefix for a thread to be reported.
     * @return itself for chaining.
     */
    public ANRDetector setReportThreadNamePrefix(String prefix) {
        if (prefix == null)
            prefix = "";
        mNamePrefix = prefix;
        return this;
    }

    /**
     * Set that only the main thread will be reported.
     *
     * @return itself for chaining.
     */
    public ANRDetector setReportMainThreadOnly() {
        mNamePrefix = null;
        return this;
    }

    public void setLogThreadsWithoutStackTrace(boolean logThreadsWithoutStackTrace) {
        mLogThreadsWithoutStackTrace = logThreadsWithoutStackTrace;
    }


    public void start() {
        ScheduledTask.getInstance().scheduledDelayed(command, mTimeoutInterval);
    }
    int lastTick=-1;
    private final Runnable command=new Runnable() {
        @Override
        public void run() {
            if (!ScheduledTask.getInstance().isInterrupted()) {

                if (mTick == lastTick) {  // If the main thread has not handled mTicker, it is blocked. ANR.
                    ANRError error;
                    if (mNamePrefix != null)
                        error = ANRError.New(mNamePrefix, mLogThreadsWithoutStackTrace);
                    else
                        error = ANRError.NewMainOnly();
                    mAnrListener.onAppNotResponding(error);
                    return ;
                }
                lastTick = mTick;
                mUiHandler.post(mTicker);
                start();
            }
        }
    } ;
}
