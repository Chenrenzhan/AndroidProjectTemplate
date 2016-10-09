package com.drumge.template.view.component;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.drumge.template.BasicConfig;
import com.drumge.template.annotation.CoreEvent;
import com.drumge.template.core.CoreFactory;
import com.drumge.template.core.ICoreManager;
import com.drumge.template.log.MLog;
import com.drumge.template.view.basic.ELAbsModule;
import com.drumge.template.view.basic.ELModudleConfig;
import com.drumge.template.view.basic.ELModudleFactory;
import com.drumge.template.view.basic.ELModuleContext;
import com.drumge.template.view.basic.ELPublicApiHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LiveComponent extends Component implements BackPressedListener {

    private Map<String , ELAbsModule> allModudles = new HashMap<>();
    private int moduleIndex=0;
    private final int delayTime = 100;

    private Bundle mSavedInstanceState;

    private ViewGroup root;
    private ViewGroup mTopViewGroup;
    private ViewGroup mBottomViewGroup;

    private WindowManager wm;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//    private ViewGroup topWin;

    private boolean mIsLandscape = false;

    //管理所有的模块
    Stack<WeakReference<ELAbsModule>> moduleStack=new Stack<>();
    boolean isDebug=false;

    public static LiveComponent newInstance() {
        ELModudleConfig.elModules = new ELModudleConfig.Modudles();
        LiveComponent fragment = new LiveComponent();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    public static LiveComponent newInstance(ELModudleConfig.Modudles configModules) {
        if(configModules==null){
            return null;
        }
        ELModudleConfig.elModules = configModules;
        LiveComponent fragment = new LiveComponent();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    //这里就是插件方去进行开发
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDebug= BasicConfig.getInstance().isDebuggable();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO inflater view root
        
        //初始化公共api
        ELPublicApiHelper.getInstance().init(allModudles);

        if(getHandler()!=null) {
            getHandler().removeCallbacks(initModuleRunnable);
            getHandler().postDelayed(initModuleRunnable, 0);
        }
        return root;
    }

    private Runnable initModuleRunnable = new Runnable() {
        @Override
        public void run() {
            if(ELModudleConfig.elModules==null){
                ELModudleConfig.elModules = new ELModudleConfig.Modudles();
            }
            String[] names = ELModudleConfig.elModules.names;

            if(moduleIndex<names.length){
                String name=names[moduleIndex];
                ELAbsModule modudle = ELModudleFactory.newModudleInstance(ELModudleConfig.BASE_URI + name);
                if (modudle != null) {
                    ELModuleContext modudleContext = new ELModuleContext();
                    modudleContext.setComponent(LiveComponent.this);
                    modudleContext.setSaveInstance(mSavedInstanceState);

                    //关联视图
                    SparseArrayCompat<ViewGroup> sVerticalViews = new SparseArrayCompat<>();
                    sVerticalViews.put(ELModudleConfig.TOP_VIEW_GROUP, mTopViewGroup);
                    sVerticalViews.put(ELModudleConfig.BOTTOM_VIEW_GROUP, mBottomViewGroup);
//                sVerticalViews.put(ELModudleConfig.TOP_WINDOW_VIEW_GROUP, topWin);

                    modudleContext.setViewGroups(sVerticalViews);
                    long before = System.currentTimeMillis();
                    modudle.init(modudleContext, "");
                    long after = System.currentTimeMillis();

                    MLog.debug("hsj", "modulename: " + name + " init time = " + (after - before) + "ms");

                    allModudles.put(name, modudle);

                    /*if(isDebug&&moduleNames.size()!=names.length) {
                        EntModule module = new EntModule(name, ELModudleConfig.BASE_URI + name, true);
                        ICoreManager.getCore(IEntModuleConfigCore.class).saveModule(module);
                    }*/

                    moduleIndex++;
                    if(getHandler()!=null) {
                        getHandler().removeCallbacks(initModuleRunnable);
                        getHandler().postDelayed(initModuleRunnable, delayTime);
                    }
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (String key : allModudles.keySet()) {
            ELAbsModule modudle = allModudles.get(key);
            if (modudle != null) {
                modudle.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (String key : allModudles.keySet()) {
            ELAbsModule modudle = allModudles.get(key);
            if (modudle != null) {
                modudle.onPause();
            }
        }
        /*if(topWin!=null){
            topWin.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (String key : allModudles.keySet()) {
            ELAbsModule modudle = allModudles.get(key);
            if (modudle != null) {
                modudle.onResume();
            }
        }
        /*if(topWin!=null){
            topWin.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onOrientationChanged(boolean isLandscape) {
        super.onOrientationChanged(isLandscape);
        for (String key : allModudles.keySet()) {
            ELAbsModule modudle = allModudles.get(key);
            if (modudle != null) {
                modudle.onOrientationChanges(isLandscape);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (String key : allModudles.keySet()) {
            ELAbsModule modudle = allModudles.get(key);
            if (modudle != null) {
                modudle.onDispose();
            }
        }
        ELPublicApiHelper.getInstance().uninit();
        cancleCareBackPress();
        if(getHandler()!=null) {
            getHandler().removeCallbacks(initModuleRunnable);
        }
        moduleIndex = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allModudles.clear();
        moduleStack.clear();

    }

    public Handler getHandler(){
        return super.getHandler();
    }

    public ELAbsModule getModudleByName(String name) {
        return allModudles.get(name);
    }

    @Override
    public boolean checkActivityValid() {
        return super.checkActivityValid();
    }

    @Override
    public boolean checkNetToast() {
        return super.checkNetToast();
    }


    //监听返回键
    private void careBackPress() {
    }

    private void cancleCareBackPress() {
    }

    public void subscribeBackPressListener(ELAbsModule modudle){
        moduleStack.push(new WeakReference<ELAbsModule>(modudle));
        careBackPress();
    }

    public void unSubscribeBackPressListener(){
        if(!moduleStack.isEmpty())
            moduleStack.pop();
        if(moduleStack.isEmpty()){
            cancleCareBackPress();
        }
    }

    @Override
    public boolean onBackPressed() {
        MLog.info(this, "ly LiveComponet onBackPressed is click");
        if (moduleStack != null && !moduleStack.isEmpty()) {
            ELAbsModule module = moduleStack.peek().get();
            if (module == null) {
                moduleStack.pop();
                return onBackPressed();
            } else if (module.onBackPress()&&!module.persist()) {
                return true;
            }
        } else {
            cancleCareBackPress();
            return false;
        }
        return false;
    }

    @Override
    public boolean persist() {
        return true;
    }
}
