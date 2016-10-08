package com.drumge.template.view.component;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;


import com.drumge.template.log.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name:BasicChannelComponentContainer
 * Description:直播间模板化容器
 * Author:zengyan
 * Date:2016/03/10
 * Modified History:
 */
public abstract class ChannelComponentContainer extends Component implements IComponentContainer {
    private boolean mOnViewCreatedCalled;
    private SparseArray<IComponent> includes;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mOnViewCreatedCalled = true;
        addLevelComponents(firstLevelComponents);
        MLog.warn(this, "onViewCreated addFirstLevelComponents");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mOnViewCreatedCalled) {
            throw new RuntimeException("Component " + this.getClass().getSimpleName() +
                    " did not call through to super.onViewCreated()");
        }
    }

//    protected void registerIncludesComponent(SparseArray<IComponent> components) {
//    }

//    public void registerComponents(Template template) {
//        if (includes == null) {
//            includes = new SparseArray<IComponent>();
//            registerIncludesComponent(includes);
//        }
//
//        for (int i = 0; i < includes.size(); i++) {
//            IComponent component = includes.valueAt(i);
//            component.setTemplate(template);
//            if (component instanceof IComponentContainer) {
//                IComponentContainer componentContainer = (IComponentContainer) component;
//                componentContainer.registerComponents(template);
//            }
//        }
//
//        setTemplate(template);
//    }

    public void setComponents(SparseArray<IComponent> components) {
        if (components == null) {
            return;
        }
//        includes = components;
        divideComponents(components);
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
//            for (int i = 0; i < includes.size(); i++) {
//                int componentId = includes.keyAt(i);
//                    if (componentId==R.id.basic_live_video_component){
//                        IComponent component = includes.valueAt(i);
//                        MLog.info(this,"zy ComponentContainer ftmedia component == " + component.getClass().toString());
//                        Fragment fragment = component.getContent();
//                        FragmentTransaction ftmedia = getChildFragmentManager().beginTransaction();
//                        ftmedia.replace(componentId, fragment);
//                        ftmedia.commitAllowingStateLoss();
//                        getChildFragmentManager().executePendingTransactions();
//                        //includes.delete(componentId);
//                        break;
//                    }
//            }
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for (int i = 0; i < includes.size(); i++) {
                int componentId = includes.keyAt(i);
//                if (componentId == 0||componentId ==R.id.basic_live_video_component) {
//                    continue;
//                }
                IComponent component = includes.valueAt(i);
                Fragment fragment = component.getContent();
                MLog.info(this, "zy ComponentContainer component == " + component + " class = " + component.getClass().toString());
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

//    public List<IComponent> getIncludeComponents() {
//        ArrayList<IComponent> components = new ArrayList<IComponent>(includes.size());
//        components.add(this);
//        for (int i = 0; i < includes.size(); i++) {
//            IComponent component = includes.valueAt(i);
//            if (component instanceof IComponentContainer) {
//                IComponentContainer componentContainer = (IComponentContainer) component;
//                components.addAll(componentContainer.getIncludeComponents());
//            } else {
//                components.add(component);
//            }
//        }
//        return components;
//    }


    /**
     * -------分级加载-------------------------------------------------------------------------------------------------------------------------
     **/
    private SparseArray<IComponent> firstLevelComponents = new SparseArray<>(); //一级组件
    private SparseArray<IComponent> secondLevelComponents = new SparseArray<>(); //二级组件
    private SparseArray<IComponent> thirdLevelComponents = new SparseArray<>(); //三级组件

    private void divideComponents(SparseArray<IComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            int compId = components.keyAt(i);
            IComponent comp = components.valueAt(i);
            int level = parseComponentLevel(comp);
            if (level > 0) {
                switch (level) {
                    case ComponentConst.COMPONENT_LEVEL_ONE:
                        firstLevelComponents.put(compId, comp);
                        break;
                    case ComponentConst.COMPONENT_LEVEL_TWO:
                        secondLevelComponents.put(compId, comp);
                        break;
                    default:
                        thirdLevelComponents.put(compId, comp);
                        break;
                }
            }
        }
        MLog.debug(this, "firstLevelComponents=" + firstLevelComponents);
        MLog.debug(this, "secondLevelComponents=" + secondLevelComponents);
        MLog.debug(this, "thirdLevelComponents=" + thirdLevelComponents);
    }

//    public int parseComponentLevel(IComponent component){
//        int compLevel = -1;
//        switch(component.getClass().getSimpleName()){
//            /**一级组件**/
//            case ComponentConst.PROGRAM_INFO_FRAGMENT:  compLevel=ComponentConst.COMPONENT_LEVEL_ONE;  break;
//            case ComponentConst.ONLINE_AUDIENCE_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_ONE; break;
//            //case ComponentConst.BASIC_DANMU_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_ONE;  break;
//            case ComponentConst.INTERACTIVE_EXPAND_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_ONE;  break;
//            case ComponentConst.CHAT_EMOTION_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_TWO;  break;
//            case ComponentConst.LIVE_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_TWO;  break; //业务区--秀场
//            case ComponentConst.MOBILE_LIVE_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_TWO;  break; //业务区--现场
//            /**二级组件**/
//            case ComponentConst.LIKE_LAMP_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_TWO;  break; //氛围灯
//            case ComponentConst.SLIDE_FUNCTION_COMPONENT:  compLevel=ComponentConst.COMPONENT_LEVEL_TWO;  break;
//            /**默认三级组件**/
//            default : compLevel=ComponentConst.COMPONENT_LEVEL_THREE;  break;
//        }
//        return compLevel;
//    }


    public int parseComponentLevel(IComponent component) {
        int compLevel = -1;
        if (component == null) {
            return compLevel;
        }
        if (ComponentConst.OFFICIAL_PROGRAM_INFO_FRAGMENT.equals(component.getClass().getSimpleName())) { //一级组件
            compLevel = ComponentConst.COMPONENT_LEVEL_ONE;
        } else if (ComponentConst.PROGRAM_INFO_FRAGMENT.equals(component.getClass().getSimpleName())) {
            compLevel = ComponentConst.COMPONENT_LEVEL_ONE;
        } else if ((ComponentConst.ONLINE_AUDIENCE_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_ONE;
        } else if ((ComponentConst.INTERACTIVE_EXPAND_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_ONE;
        } else if ((ComponentConst.CHAT_EMOTION_COMPONENT.equals(component.getClass().getSimpleName()))) {//二级组件
            compLevel = ComponentConst.COMPONENT_LEVEL_TWO;
        } else if ((ComponentConst.LIVE_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_TWO;
        } else if ((ComponentConst.MOBILE_LIVE_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_TWO;
        } else if ((ComponentConst.LIKE_LAMP_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_TWO;
        } else if ((ComponentConst.SLIDE_FUNCTION_COMPONENT.equals(component.getClass().getSimpleName()))) {
            compLevel = ComponentConst.COMPONENT_LEVEL_TWO;
        } else {
            compLevel = ComponentConst.COMPONENT_LEVEL_THREE; //三级组件
        }
        return compLevel;
    }

    public void registerComponents(Template template) {
        if (firstLevelComponents == null) {
            firstLevelComponents = new SparseArray<IComponent>();
        }
        for (int i = 0; i < firstLevelComponents.size(); i++) {
            IComponent component = firstLevelComponents.valueAt(i);
            component.setTemplate(template);
//            if (component instanceof IComponentContainer) {
//                IComponentContainer componentContainer = (IComponentContainer) component;
//                componentContainer.registerComponents(template);
//            }
        }

        if (secondLevelComponents == null) {
            secondLevelComponents = new SparseArray<IComponent>();
        }
        for (int i = 0; i < secondLevelComponents.size(); i++) {
            IComponent component = secondLevelComponents.valueAt(i);
            component.setTemplate(template);
        }

        if (thirdLevelComponents == null) {
            thirdLevelComponents = new SparseArray<IComponent>();
        }
        for (int i = 0; i < thirdLevelComponents.size(); i++) {
            IComponent component = thirdLevelComponents.valueAt(i);
            component.setTemplate(template);
        }
        setTemplate(template);
    }

    public void addLevelComponents(SparseArray<IComponent> levelComponents) {
        if (levelComponents != null && levelComponents.size() > 0) {
            for (int i = 0; i < levelComponents.size(); i++) {
                int componentId = levelComponents.keyAt(i);
                IComponent component = levelComponents.valueAt(i);
                commitComponents(componentId, component);
            }
        }
    }

    public void commitComponents(int componentId, IComponent component) {
        String tag = component.getClass().getSimpleName() + "_" + componentId;
        MLog.info(this, "[oyyj] componentsCommits tag=" + tag +
                " component.isInitHidden():" + component.isInitHidden());
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = component.getContent();
        if (component.isInitHidden()) {
            if (componentId >>> 24 != 0 /*check legal resId*/) {
//                    fragmentTransaction.hide(fragment);
                ft.detach(fragment).add(componentId, fragment);
            }
        } else {
            ft.replace(componentId, fragment);
        }
        ft.commitAllowingStateLoss();

    }

    public List<IComponent> getIncludeComponents() {
        if (firstLevelComponents == null || secondLevelComponents == null) {
            return null;
        }
        ArrayList<IComponent> components = new ArrayList<IComponent>(firstLevelComponents.size() + secondLevelComponents.size());
        components.add(this);
        for (int i = 0; i < firstLevelComponents.size(); i++) {
            IComponent component = firstLevelComponents.valueAt(i);
            components.add(component);
//            if (component instanceof IComponentContainer) {
//                IComponentContainer componentContainer = (IComponentContainer) component;
//                components.addAll(componentContainer.getIncludeComponents());
//            } else {
//                components.add(component);
//            }
        }
        for (int i = 0; i < secondLevelComponents.size(); i++) {
            IComponent component = secondLevelComponents.valueAt(i);
            components.add(component);
        }
        for (int i = 0; i < thirdLevelComponents.size(); i++) {
            IComponent component = thirdLevelComponents.valueAt(i);
            components.add(component);
        }
        MLog.debug(this, "getIncludeComponents components：" + components);
        return components;
    }

    private int loadLevelOneCompNum = 0, loadLevelTwoCompNum = 0;

    protected void onBaseComponenLoad(IComponent iComponent) {
        if (parseComponentLevel(iComponent) == ComponentConst.COMPONENT_LEVEL_ONE) {
            loadLevelOneCompNum++;
            if (loadLevelOneCompNum >= firstLevelComponents.size()) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addLevelComponents(secondLevelComponents);
                    }
                }, 500);
                MLog.warn(this, "onViewCreated addSecondLevelComponents");
            }
        } else if (parseComponentLevel(iComponent) == ComponentConst.COMPONENT_LEVEL_TWO) {
            loadLevelTwoCompNum++;
            if (loadLevelTwoCompNum >= secondLevelComponents.size()) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addLevelComponents(thirdLevelComponents);
                    }
                }, 500);
                MLog.warn(this, "onViewCreated addThirdLevelComponents");
            }
        }

        MLog.debug(this, "[onBaseComponenLoad] [" + loadLevelOneCompNum + " - " + loadLevelTwoCompNum + "] " +
                " className=" + iComponent.getClass().getSimpleName() +
                " componentLevel=" + parseComponentLevel(iComponent));
    }
}
