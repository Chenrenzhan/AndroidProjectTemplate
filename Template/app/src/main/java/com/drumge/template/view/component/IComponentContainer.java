package com.drumge.template.view.component;

import android.support.v4.app.FragmentManager;
import android.util.SparseArray;


import java.util.List;

/**
 * Created by xianjiachao on 2015/5/18.
 */
public interface IComponentContainer extends IComponent {

    public void setComponents(SparseArray<IComponent> components);

    public boolean needCustomArrangeComponent();

    public void customArrangeComponent(FragmentManager fm, SparseArray<IComponent> includes);

    public List<IComponent> getIncludeComponents();
}
