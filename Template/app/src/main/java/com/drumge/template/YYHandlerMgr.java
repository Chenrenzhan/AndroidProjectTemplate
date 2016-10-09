package com.drumge.template;

import java.util.concurrent.CopyOnWriteArraySet;

import android.os.Message;

public class YYHandlerMgr {
    
    private CopyOnWriteArraySet<YYHandler> mHandlers = new CopyOnWriteArraySet<YYHandler>();

    private static YYHandlerMgr m_instance = null;
    public static YYHandlerMgr instance(){
		if(m_instance == null) {
			m_instance = new YYHandlerMgr();
		}
		return m_instance;
    }
    
    public void add(YYHandler handler) {
        mHandlers.add(handler);
    }

    public void remove(YYHandler handler) {
        mHandlers.remove(handler);
    }
    
    public boolean notify2UIThread(int message) {
        return notify2UIThread(message, (Object[]) null);
    }

    //Todo:sync
    public boolean notify2UIThread(int message, Object... params) {
    	for (YYHandler handler : mHandlers) {
            if (handler.canHandleMessage(message)) {
                Message msg = handler.obtainMessage();
                msg.what = message;
                msg.obj = params;
                handler.sendMessage(msg);
            }
        }
        return true;
    }
}
