package com.drumge.template.view.component;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;

import com.drumge.template.SafeDispatchHandler;
import com.drumge.template.log.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianjiachao on 2015/4/29.
 */
public abstract class ComponentContainer extends Component implements IComponentContainer {
    private boolean mOnViewCreatedCalled;

    private SparseArray<IComponent> includes;
    private Handler mHandler = new SafeDispatchHandler();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mOnViewCreatedCalled = true;
        addToFragment();
//        getHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                addToFragment();
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mOnViewCreatedCalled) {
            throw new RuntimeException("Component " + this.getClass().getSimpleName() +
                            " did not call through to super.onViewCreated()");
        }
    }

    protected void registerIncludesComponent(SparseArray<IComponent> components) {
    }

    public void setComponents(SparseArray<IComponent> components) {
        if (components == null) {
            return;
        }
        includes = components;
    }

    /**
     * 添加组件
     * @param components
     */
    public void addComponents(SparseArray<IComponent> components) {

        if (components == null) {
            return;
        }

        if (includes == null) {
            MLog.warn(this, "ComponentContainer includes = " + includes);
            return;
        }
        //加入Components
        if(mOnViewCreatedCalled) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for (int i = 0; i < components.size(); i++) {
                int componentId = components.keyAt(i);
                if (componentId == 0) {
                    continue;
                }
                includes.put(components.keyAt(i), components.valueAt(i));

                IComponent component = components.valueAt(i);

                Fragment fragment = component.getContent();
                if (component.isInitHidden()) {
                    if (componentId >>> 24 != 0 /*check legal resId*/) {
                        fragmentTransaction.detach(fragment).add(componentId, fragment);
                    }
                } else {
                    fragmentTransaction.replace(componentId, fragment);
                }
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    /**
     * 移除组件（）
     * @param componentId
     */
    public void removeComponents(int componentId ){

        if (includes == null) {
            MLog.warn(this, "ComponentContainer includes = " + includes);
            return;
        }
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        includes.remove(componentId);
        Fragment fragment =getChildFragmentManager().findFragmentById(componentId);// component.getContent();
        if(fragment != null){
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }

    }
    public boolean needCustomArrangeComponent() {
        return false;
    }

    public void customArrangeComponent(FragmentManager fm, SparseArray<IComponent> includes) {
    }

    private void addToFragment() {
        if (includes == null) {
            MLog.warn(this, "ComponentContainer includes = " + includes);
            return;
        }

        if (!needCustomArrangeComponent()) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for (int i = 0; i < includes.size(); i++) {
                int componentId = includes.keyAt(i);
                if (componentId == 0) {
                    continue;
                }
                IComponent component = includes.valueAt(i);
                Fragment fragment = component.getContent();
//                MLog.info(this,"zy ComponentContainer component == "+ component +" class = "+component.getClass().toString() );
                if (component.isInitHidden()) {
                    if (componentId >>> 24 != 0 /*check legal resId*/) {
//                    fragmentTransaction.hide(fragment);
                        fragmentTransaction.detach(fragment).add(componentId, fragment);
                    }
                } else {
                        fragmentTransaction.replace(componentId, fragment);
                }
            }
            fragmentTransaction.commitAllowingStateLoss();
//            getChildFragmentManager().executePendingTransactions();
//            getChildFragmentManager().beginTransaction();
        } else {
            customArrangeComponent(getChildFragmentManager(), includes);
        }
    }

    public List<IComponent> getIncludeComponents() {
        ArrayList<IComponent> components = new ArrayList<IComponent>(includes.size());
        components.add(this);
        for (int i = 0; i < includes.size(); i++) {
            IComponent component = includes.valueAt(i);
            if (component instanceof IComponentContainer) {
                IComponentContainer componentContainer = (IComponentContainer) component;
                components.addAll(componentContainer.getIncludeComponents());
            } else {
                components.add(component);
            }
        }
        return components;
    }

    protected Handler getHandler() {
        return mHandler;
    }
}
