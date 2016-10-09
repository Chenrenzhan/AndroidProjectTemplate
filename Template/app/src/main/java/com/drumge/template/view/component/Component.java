package com.drumge.template.view.component;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.drumge.template.log.MLog;
import com.drumge.template.view.fragment.BaseLinkFragment;

/**
 * Created by xianjiachao on 2015/4/29.
 */
public abstract class Component extends BaseLinkFragment implements IComponent {
    private Object mAttachment;
    boolean mInitHidden;
    private ComponentDimension dimension;
    private int mOrientation;
    private boolean isComponentCreated = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOrientation = getActivity().getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) { // init orientation state
            onOrientationChanged(true);
        }
        setComponentCreated(true);//通過參數設置
        MLog.debug(this, "Component onActivityCreated className=" + getClass().getSimpleName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    /**
     * 标识改组件是否是以懒加载的形式进行
     * @return
     */
    @Override
    public boolean isInitHidden() {
        return mInitHidden;
    }

    @Override
    public void setInitHidden(boolean hidden) {
        mInitHidden = hidden;
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

    public void showSelf() {
        getFragmentManager().beginTransaction().show(this).commitAllowingStateLoss();
//        ICoreManager.getCore(IComponentCore.class).componentShowSelfNotify(this);


    }

    public void hideSelf() {
        getFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
    }

    /**
     * 获取当前组件fragment的尺寸
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
//    @Override
//    public void bindComponentFinsh(){
//        MLog.info(this,"zy bindComponentFinsh " + " Component");
//    }

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

    @Override
    public void onDestroy() {
        setComponentCreated(false);
        super.onDestroy();
    }
}
