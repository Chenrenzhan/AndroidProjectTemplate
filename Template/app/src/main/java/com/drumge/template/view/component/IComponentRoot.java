package com.drumge.template.view.component;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by xianjiachao on 2015/4/29.
 */
public interface IComponentRoot {

    public void registerComponents(Template template);

    public Fragment getContent();

    public List<IComponent> getComponents();

//    public void registerComponents(Template template,String businessId);
}
