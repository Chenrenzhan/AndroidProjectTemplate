package com.drumge.template.view.component;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.drumge.template.log.MLog;


/**
 * Created by xianjiachao on 2015/10/27.
 */
public abstract class PopupComponent extends DialogFragment implements IPopupComponent {
    private int mOrientation;

    private Object mAttachment;
    private ComponentDimension dimension;
    private FragmentManager mParentFragMgr;
    private boolean isComponentCreated = false;
    private boolean isPopupComponentDismissed = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOrientation = getActivity().getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) { // init orientation state
            onOrientationChanged(true);
        }
        setComponentCreated(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
//        setComponentCreated(false);
        super.onDestroy();
    }

    @Override
    public void setParentFragmentManager(FragmentManager fm) {
        mParentFragMgr = fm;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isPopupComponentDismissed = true;
    }

    @Override
    public void show(Bundle data) {
        try {
            setArguments(data);
            show(mParentFragMgr, "");
        }catch (IllegalStateException e){
            MLog.error(this,"catch到的error："+e.toString());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (!isPopupComponentDismissed) {
            return;
        }
        try {
            super.show(manager, tag);
            isPopupComponentDismissed = false;
        } catch (Throwable throwable) {
            MLog.error(this, "catch到的error: " + throwable);
        }
    }

    @Override
    public void hide() {
        dismissAllowingStateLoss();
    }

    @Override
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void setAttachment(Object obj) {
        mAttachment = obj;
    }

    @Override
    public Object getAttachment() {
        return mAttachment;
    }

    @Override
    public Fragment getContent() {
        return this;
    }

    @Override
    public boolean isInitHidden() {
        return true;
    }

    @Override
    public void setInitHidden(boolean hidden) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mOrientation != newConfig.orientation) {
            mOrientation = newConfig.orientation;
            onOrientationChanged(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onOrientationChanged(boolean isLandscape) {

    }
    @Override
    public void onDestroyView() {
        try {
            if (getView()!=null&&((ViewGroup) getView().getParent())!=null){
                ((ViewGroup) getView().getParent()).removeView(getView());
            }
            super.onDestroyView();
        }catch (Throwable throwable){
            MLog.error(this,throwable);
        }


    }

    public void showSelf() {
        getFragmentManager().beginTransaction().show(this).commitAllowingStateLoss();
    }

    public void hideSelf() {
        getFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
    }

    /**
     * 获取当前组件fragment的尺寸
     * @return
     */
    @SuppressLint("NewApi")
    public ComponentDimension getDimension(){
        if (dimension==null){
            dimension = new ComponentDimension();
        }
        if (getView()!=null){
            dimension.x = getView().getX();
            dimension.y = getView().getY();
            dimension.width = getView().getWidth();
            dimension.height = getView().getHeight();
        }
        return dimension;
    }

    /**
     * 當前組件是否創建了
     * @return
     */
    @Override
    public boolean isComponentCreated() {
        return isComponentCreated;
    }

    /**
     * 設置當前組件是否創建了
     * @param componentCreated
     */
    @Override
    public void setComponentCreated(boolean componentCreated) {
        isComponentCreated = componentCreated;
    }
}
