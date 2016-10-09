package com.drumge.template;

import android.content.Context;

/**
 * Created by Administrator on 2015/12/10.
 */
public class BasicYYHandlerMgr {

    private Context mContext;
    private static BasicYYHandlerMgr mInstance;


    private YYHandlerMgr sdkHandlerManager;

    public synchronized static BasicYYHandlerMgr getInstance(){
        if(mInstance == null)
            mInstance = new BasicYYHandlerMgr();
        return mInstance;
    }

    private BasicYYHandlerMgr(){

    }

    public void setAppContext(Context context){
        mContext = context;

    }

    public YYHandlerMgr getSdkHandlerManager() {
        return sdkHandlerManager;
    }

    public void setSdkHandlerManager(YYHandlerMgr sdkHandlerManager) {
        this.sdkHandlerManager = sdkHandlerManager;
    }


    public Context getAppContext(){
        return mContext;
    }
}
