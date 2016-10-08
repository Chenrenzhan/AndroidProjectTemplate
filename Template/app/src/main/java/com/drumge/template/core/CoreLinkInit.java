package com.drumge.template.core;


/**
 * Created by yuanxiaoming on 16/1/7.
 */
public class CoreLinkInit {

    public static void registerCoreClass(){
        CoreLinkUiCoreInit.registerCoreClass();
    }

    public static void getCore(){
        CoreLinkUiCoreInit.getCore();
    }


    /**
     * 业务动态注册core
     * @param coreInterface
     * @param coreClass
     * @param isGetCore
     */
	public static void registerCoreClass(Class<? extends IBaseCore> coreInterface, Class<? extends AbstractBaseCore> coreClass, boolean isGetCore) {
        if (!CoreFactory.hasRegisteredCoreClass(coreInterface)){
            CoreFactory.registerCoreClass(coreInterface,coreClass);
        }
        if (isGetCore){
            ICoreManager.getCore(coreInterface);
        }

    }


}
