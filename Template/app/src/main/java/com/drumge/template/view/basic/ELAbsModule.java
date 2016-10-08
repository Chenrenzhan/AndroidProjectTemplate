package com.drumge.template.view.basic;

import android.os.Bundle;

import com.yymobile.core.utils.IConnectivityCore;

/**
 * Created by qiushunming on 16/1/11.
 */
public abstract class ELAbsModule {


    public abstract void init(ELModudleContext modudleContext, String extend);

    public abstract void onSaveInstanceState(Bundle outState);

    public abstract void onResume();
    public abstract void onPause();

    public abstract void onOrientationChanges(boolean isLandscape);
    //网络改变回调
    public abstract void onConnectivityChange(IConnectivityCore.ConnectivityState previousState, IConnectivityCore.ConnectivityState currentState);

    public abstract void onDispose();

    public abstract void subscribeBackPressListener();
    public abstract void unSubscribeBackPressListener();
    //监听返回键
    public abstract boolean onBackPress();
    public abstract boolean persist();

}
