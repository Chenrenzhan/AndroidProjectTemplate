package com.drumge.template.view.basic;

import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.yy.live.helper.DialogFactory;
import com.yy.live.livetemplate.LiveComponent;
import com.yy.mobile.util.ResolutionUtils;
import com.yy.mobile.util.SafeDispatchHandler;
import com.yy.mobile.util.log.MLog;
import com.yy.mobile.ylink.bridge.CoreApiManager;
import com.yy.mobile.ylink.bridge.coreapi.BaseFragmentApi;
import com.yy.mobile.ylink.bridge.coreapi.LoginApi;
import com.yymobile.core.ICoreClient;
import com.yymobile.core.ICoreManager;
import com.yymobile.core.basechannel.IChannelLinkCore;
import com.yymobile.core.utils.IConnectivityCore;

/**
 * Created by qiushunming on 16/1/11.
 */
public abstract class ELBasicModule extends ELAbsModule {

    protected FragmentActivity mContext;

    protected LiveComponent mComponent;

    protected DialogFactory mDialogFactory;

    protected ELModudleContext modudleContext;

    private Handler handler;

    //视频区域信息
    private RectF mVideoRect;

    @Override
    public void init(ELModudleContext modudleContext, String extend) {
        if(modudleContext!=null){
            ICoreManager.addClient(this);
            this.modudleContext = modudleContext;
            mComponent=modudleContext.getComponent();
            if(mComponent!=null){
                mContext=mComponent.getActivity();
                mDialogFactory=new DialogFactory(mContext);
            }
        }
    }

    public RectF getVideoRect() {
        if(mVideoRect==null){
            mVideoRect=new RectF();
            float w=ResolutionUtils.getScreenWidth(mContext);
            float h=w*3/4.0f;
            float m=ResolutionUtils.convertDpToPixel(120f,mContext);
            mVideoRect.top=m;
            mVideoRect.left=0;
            mVideoRect.right=w;
            mVideoRect.bottom=m+h;
        }
        MLog.debug(this,"video rect="+mVideoRect);
        return mVideoRect;
    }

    public DialogFactory getDialogFactory() {
        if(mDialogFactory==null){
            mDialogFactory=new DialogFactory(mContext);
        }
        return mDialogFactory;
    }

    public void notifyClients(Class<? extends ICoreClient> clientClass, String methodName, Object... args) {
        ICoreManager.notifyClients(clientClass,methodName,args);
    }

    protected IChannelLinkCore getChannelCore(){
        return ICoreManager.getChannelLinkCore();
    }
    @Override
    public void onConnectivityChange(IConnectivityCore.ConnectivityState previousState, IConnectivityCore.ConnectivityState currentState){

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDispose() {
        if(mDialogFactory!=null)
            mDialogFactory.dismissDialog();
        ICoreManager.removeClient(this);
    }

    public Handler getHandler(){
        if (modudleContext != null) {
            handler = modudleContext.getComponent().getHandler();
        }

        if (handler == null) {
            handler = new SafeDispatchHandler();
        }
        return handler;
    }

    protected ELAbsModule getModudleByName(String name) {
        if (mComponent == null){
            return null;
        }
        return mComponent.getModudleByName(name);
    }

    protected void login(){
        if(CoreApiManager.getInstance().getApi(LoginApi.class)!=null){
            CoreApiManager.getInstance().getApi(LoginApi.class).goToLogin(mContext);
        }
    }

    protected void showLoginDialog(String msg){
        if(CoreApiManager.getInstance().getApi(BaseFragmentApi.class)!=null){
            CoreApiManager.getInstance().getApi(BaseFragmentApi.class).showLoginDialogWithText(mContext,msg);
        }
    }

    protected boolean isLandScape(){
        if(mContext!=null) {
            return mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        }else
            return false;
    }

    protected void showLoginDialog(){
        showLoginDialog("登录更精彩，免费送鲜花");
    }

    public void subscribeBackPressListener(){
        if(mComponent!=null)
            mComponent.subscribeBackPressListener(this);
    }

    public void unSubscribeBackPressListener(){
        if(mComponent!=null)
            mComponent.unSubscribeBackPressListener();
    }

    @Override
    public boolean onBackPress(){
        return false;
    }

    @Override
    public boolean persist() {
        return false;
    }
}
