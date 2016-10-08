package com.drumge.template.view.component;

import android.support.v4.app.Fragment;

import com.yy.mobile.ui.basicchanneltemplate.Template;

/**
 * Created by xianjiachao on 2015/5/18.
 */
public interface IComponent {

    public Template getTemplate();

    public void setTemplate(Template template);

    public void setAttachment(Object obj);

    public Object getAttachment();

    public Fragment getContent();

    public boolean isInitHidden();

    public void setInitHidden(boolean hidden);

    public void onOrientationChanged(boolean isLandscape);
    public void showSelf();

    public void hideSelf();

    /**
     * 获取组件尺寸
     * @return
     */
    public ComponentDimension getDimension();

    /**
     * 當前組件是否創建并且初始化了
     * @return
     */
    public boolean isComponentCreated();

    /**
     * 設置當前組件是否創建了并且初始化了
     * @param componentCreated
     */
    public void setComponentCreated(boolean componentCreated);


//    /**
//     * 绑定视图跟组件完成
//     */
//    public void bindComponentFinsh();
}
