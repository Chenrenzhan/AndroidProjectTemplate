package com.drumge.template.view.component;

import android.support.v4.app.FragmentManager;


import com.drumge.template.log.MLog;

import java.util.List;

/**
 * Created by xianjiachao on 2015/4/29.
 */
public class Template {
    private long mTemplateId;
    private Class<? extends IComponentRoot> mRootClass;
    private IComponentRoot mRoot;
    private FragmentManager mFragmentManager;
    private BackPressedDispatcher mDispatcher;
    private FinishHandler mFinishHandler;

    public Template(long templateId, Class<? extends IComponentRoot> rootClass) {
        mTemplateId = templateId;
        mRootClass = rootClass;
    }

    public long getTemplateId() {
        return mTemplateId;
    }


    public void applyTemplate(FragmentManager fm) {
        MLog.info(this, ChannelConst.Log.toModuleLog("applyTemplate : " + mRootClass));
        if (fm == null) {
            return;
        }
        mFragmentManager = fm;
        IComponentRoot root = null;
        try {
            root = mRootClass.newInstance();
        } catch (InstantiationException e) {
            MLog.info(this, ChannelConst.Log.toModuleLog("create root fail"));
        } catch (IllegalAccessException e) {
            MLog.info(this, ChannelConst.Log.toModuleLog("create root fail"));
        }

        if (root != null) {
            final IComponentRoot rootFinal = root;
            root.registerComponents(this);
            fm.beginTransaction().replace(R.id.channel_template, root.getContent()).commitAllowingStateLoss();
//            new Handler(Looper.myLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    rootFinal.buildContent();
//                }
//            });
            mRoot = root;
        }
    }

//    /**
//     * 扩展的申请模板方法，用于给活动切换频道功能使用
//     * @param fm fm
//     */
//    public void applyTemplate(FragmentManager fm,String businessId) {
//        MLog.info(this, ChannelConst.Log.toModuleLog("applyTemplate : " + mRootClass));
//        if (fm == null) {
//            return;
//        }
//        mFragmentManager = fm;
//        IComponentRoot root = null;
//        try {
//            root = mRootClass.newInstance();
//        } catch (InstantiationException e) {
//            MLog.info(this, ChannelConst.Log.toModuleLog("create root fail"));
//        } catch (IllegalAccessException e) {
//            MLog.info(this, ChannelConst.Log.toModuleLog("create root fail"));
//        }
//        if (root != null) {
//            final IComponentRoot rootFinal = root;
//            root.registerComponents(this);
////          通过tag存储fragment
//            Fragment rootFragment = getFragmentManager().findFragmentByTag(businessId);
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            if (rootFragment == null) {
//                MLog.info(this,"zy applyTemplate rootFragment == null businessId =="+businessId);
//                rootFragment = root.getContent();
//            }
//            if (rootFragment.isAdded()) {
//                MLog.info(this,"zy applyTemplate rootFragment.isAdded businessId =="+businessId);
//                ft.show(rootFragment).commitAllowingStateLoss();
//            } else {
//                MLog.info(this,"zy applyTemplate rootFragment.isNotAdded businessId =="+businessId);
//                ft.detach(rootFragment).add(R.id.channel_template,rootFragment,businessId).commitAllowingStateLoss();
//            }
//            mRoot = root;
//        }
//    }

    public <T extends IComponentBehavior> T getComponentBehavior(Class<T> clazz) {
        if (mRoot == null) {
            return null;
        }
        List<IComponent> components = mRoot.getComponents();
        for (IComponent component : components) {
            Class<?>[] interfaces = component.getClass().getInterfaces();
            for (Class<?> interfaceClazz : interfaces) {
                if (interfaceClazz == clazz) {
                    return (T) component;
                }
            }
        }
        return null;
    }

    /**
     * 带有时序的行为接口，调用此接口时，被调用的组件必须是组件已经创建完了，才能返回，否则不返回
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends IComponentBehavior> T getComponentBehaviorAfterCreate(Class<T> clazz) {
        if (mRoot == null) {
            return null;
        }
        List<IComponent> components = mRoot.getComponents();
        if (components == null){
            return null;
        }
        for (IComponent component : components) {
            Class<?>[] interfaces = component.getClass().getInterfaces();
            for (Class<?> interfaceClazz : interfaces) {
                if (interfaceClazz == clazz && component.isComponentCreated()) {
                    return (T) component;
                }else if(interfaceClazz == clazz && !component.isComponentCreated()) {
                    MLog.error(this,"zy getComponentBehaviorAfterCreate is null but component.isComponentCreated() == false");
                    return null;
                }
            }
        }
        return null;
    }

    public IPopupComponent findPopupComponent(Class<? extends IPopupComponent> clazz) {
        if (clazz != null && mRoot != null) {
            List<IComponent> components = mRoot.getComponents();
            for (IComponent component : components) {
                if (component.getClass() == clazz) {
                    IPopupComponent p = (IPopupComponent) component;
                    p.setParentFragmentManager(mFragmentManager);
                    return (IPopupComponent) p;
                }
            }
        }
        return null;
    }

    public void loadComponent(Class<? extends IComponent> clazz) {
        List<IComponent> components = mRoot.getComponents();
        for (IComponent component : components) {
            if (component.getClass() == clazz) {
                if (component.getContent().isDetached()) {
                    mRoot.getContent().getChildFragmentManager().beginTransaction().attach(component.getContent()).commitAllowingStateLoss();
//                    mRoot.getContent().getChildFragmentManager().executePendingTransactions();
                }
            }
        }
    }

    public void clearComponent(){
        List<IComponent> components = mRoot.getComponents();
        for (IComponent component : components) {
            if (component.getContent().isDetached()) {
                mRoot.getContent().getChildFragmentManager().beginTransaction().detach(component.getContent()).commitAllowingStateLoss();
            }
            component = null;
        }
    }
    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    public void setBackPressedDispatcher(BackPressedDispatcher dispatcher) {
        mDispatcher = dispatcher;
    }

    public BackPressedDispatcher getBackPressedDispatcher() {
        return mDispatcher;
    }

    public void setFinishInterrupter(FinishHandler finishHandler) {
        mFinishHandler = finishHandler;
    }

    public FinishHandler getFinishHandler() {
        return mFinishHandler;
    }

    public void clear() {
        mRoot = null;
        mFragmentManager = null;
        mDispatcher = null;
        mFinishHandler = null;
    }

    public IComponentRoot getRoot(){
        return mRoot;
    }

}
