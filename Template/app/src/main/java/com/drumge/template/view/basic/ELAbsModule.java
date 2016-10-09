package com.drumge.template.view.basic;

import android.os.Bundle;

/**
 * 所有的模块module都需继承此抽象类
 */
public abstract class ELAbsModule {


    public abstract void init(ELModuleContext modudleContext, String extend);

    public abstract void onSaveInstanceState(Bundle outState);

    public abstract void onResume();
    public abstract void onPause();

    public abstract void onOrientationChanges(boolean isLandscape);
    //网络改变回调
//    public abstract void onConnectivityChange(IConnectivityCore.ConnectivityState previousState, IConnectivityCore.ConnectivityState currentState);

    public abstract void onDispose();

    public abstract void subscribeBackPressListener();
    public abstract void unSubscribeBackPressListener();
    //监听返回键
    public abstract boolean onBackPress();
    public abstract boolean persist();

}
