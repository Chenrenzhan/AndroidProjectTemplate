package com.drumge.template.crash;

import android.os.Looper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 描述:
 *
 * @author: liyong on 2015/3/17
 */
public class ANRError extends Error {

    private static class InnerError {
        private final String mName;
        private final StackTraceElement[] mStackTrace;

        private class InnerThrowable extends Throwable {
            private InnerThrowable(InnerThrowable other) {
                super(mName, other);
            }

            @Override
            public Throwable fillInStackTrace() {
                setStackTrace(mStackTrace);
                return this;
            }
        }

        private InnerError(String name, StackTraceElement[] stackTrace) {
            mName = name;
            mStackTrace = stackTrace;
        }
    }

    private final Map<Thread, StackTraceElement[]> mStackTraces;

    private ANRError(InnerError.InnerThrowable st, Map<Thread, StackTraceElement[]> stackTraces) {
        super("Application Not Responding", st);
        mStackTraces = stackTraces;
    }

    /**
     * @return all the reported threads and stack traces.
     */
    public Map<Thread, StackTraceElement[]> getStackTraces() {
        return mStackTraces;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    static ANRError New(String prefix, boolean logThreadsWithoutStackTrace) {
        final Thread mainThread = Looper.getMainLooper().getThread();

        final Map<Thread, StackTraceElement[]> stackTraces = new TreeMap<Thread, StackTraceElement[]>(new Comparator<Thread>() {
            @Override
            public int compare(Thread lhs, Thread rhs) {
                if (lhs == rhs)
                    return 0;
                if (lhs == mainThread)
                    return 1;
                if (rhs == mainThread)
                    return -1;
                return rhs.getName().compareTo(lhs.getName());
            }
        });

        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet())
            if (
                    entry.getKey() == mainThread
                            ||  (
                            entry.getKey().getName().startsWith(prefix)
                                    &&  (
                                    logThreadsWithoutStackTrace
                                            ||
                                            entry.getValue().length > 0
                            )
                    )
                    )
                stackTraces.put(entry.getKey(), entry.getValue());

        InnerError.InnerThrowable tst = null;
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet())
            tst = new InnerError(entry.getKey().getName(), entry.getValue()).new InnerThrowable(tst);

        return new ANRError(tst, stackTraces);
    }

    static ANRError NewMainOnly() {
        final Thread mainThread = Looper.getMainLooper().getThread();
        final StackTraceElement[] mainStackTrace = mainThread.getStackTrace();

        final HashMap<Thread, StackTraceElement[]> stackTraces = new HashMap<Thread, StackTraceElement[]>(1);
        stackTraces.put(mainThread, mainStackTrace);

        return new ANRError(new InnerError(mainThread.getName(), mainStackTrace).new InnerThrowable(null), stackTraces);
    }
}
