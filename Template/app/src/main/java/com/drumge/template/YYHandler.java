package com.drumge.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.drumge.template.log.MLog;

public abstract class YYHandler extends Handler {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)

    public @interface MessageHandler {
        int message();
    }

    private SparseArray<Method> mHandlerMap = new SparseArray<Method>();
    
    public YYHandler(Looper looper) {
        super(looper);
        init();
    }

    private void init() {
        for (Method method : getClass().getDeclaredMethods()) {
            MessageHandler an = method.getAnnotation(MessageHandler.class);
            if (an != null) {
                mHandlerMap.put(an.message(), method);
            }
        }
    }

    private synchronized Method getMessageHandler(int message) {
        return mHandlerMap.get(message);
    }

    public boolean canHandleMessage(int message) {
        return getMessageHandler(message) != null;
    }

    @Override
    public void handleMessage(Message msg) {
        Object[] params = null;
        Method handler = null;
        try {
            handler = getMessageHandler(msg.what);
            params = (Object[]) msg.obj;
            if (params != null) {
                handler.invoke(this, params);
            } else {
                handler.invoke(this, (Object[]) null);
            }
        } catch (Exception e) {
            MLog.error(this, getLog(msg, params, handler));
            MLog.error(this, e);
        }
    }

    private String getLog(Message msg, Object[] params, Method handler) {
        try {
            StringBuilder log = new StringBuilder("handle msg ");
            log.append(msg.what);
            log.append(" error, params = [");
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) {
                        log.append(", ");
                    }
                    log.append(params[i]);
                }
            }
            log.append("], ");
            log.append("handler = ");
            log.append(handler);
            return log.toString();
        } catch (Exception e) {
            MLog.error(this, "generate error log failed");
            return "generate error log failed";
        }
    }

}
