package com.drumge.template.core;

import android.content.Context;


import com.drumge.template.log.MLog;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ICoreManager {
    public static final String YYMOBILE_DIR_NAME = "yymobile";
    public static final String TAG = "CoreManager";
    public static final String TAG_EVENT = "CoreManager_Event";
    public static final String IM_CACHE_DIR = YYMOBILE_DIR_NAME + File.separator + "im";
    public static final String IM_IMG_CACHE_DIR = IM_CACHE_DIR + File.separator + "image";
    public static final String IM_VOICE_CACHE_DIR = IM_CACHE_DIR + File.separator + "voice";


    private static Map<Class<? extends ICoreClient>, ArrayList<ICoreClient>> clients
            = new HashMap<Class<? extends ICoreClient>, ArrayList<ICoreClient>>();

    private static Map<Class<?>, Set<Object>> coreEvents = new HashMap<Class<?>, Set<Object>>();

    private static Map<Class<? extends ICoreClient>, HashMap<String, Method>> clientMethods
            = new HashMap<Class<? extends ICoreClient>, HashMap<String, Method>>();

    private static Map<Object, Map<String, Method>> coreEventMethods = new HashMap<Object, Map<String, Method>>();
    public static Context context;
    private static IEntCore entCore;
    private static IUserCore userCore;
    private static IAuthCore authCore;
    private static IMediaCore mediaCore;  // media core
    private static IChannelLinkCore channelLinkCore;


    public static Context getContext() {
        return context;
    }


    private static void addClientMethodsIfNeeded(Class<? extends ICoreClient> clientClass) {
        try {
            HashMap<String, Method> methods = clientMethods.get(clientClass);
            if (methods == null) {
                methods = new HashMap<String, Method>();
                Method[] allMethods = clientClass.getMethods();
                for (Method m : allMethods) {
                    methods.put(m.getName(), m);
                }
                clientMethods.put(clientClass, methods);
            }
        } catch (Throwable throwable) {
            MLog.error(TAG, throwable);
        }

    }

    /**
     * 监听某个接口的回调，监听者需要实现该接口
     * 注意在不需要回调时要用removeClient
     *
     * @param clientClass
     * @param client
     */
    public static void addClient(Class<? extends ICoreClient> clientClass, ICoreClient client) {

        if (clientClass == null || client == null) {
            return;
        }

        ArrayList<ICoreClient> clientList = clients.get(clientClass);
        if (clientList == null) {
            clientList = new ArrayList<ICoreClient>();
            clients.put(clientClass, clientList);
        }

        addClientMethodsIfNeeded(clientClass);

        if (clientList.contains(client)) {
            return;
        }

        clientList.add(client);
        //MLog.verbose(TAG, "client(" + client + ") added for " + clientClass.getName());
    }

    @SuppressWarnings("unchecked")
    private static void addClient(ICoreClient client, Class<?> clientClass) {
        if (clientClass == null)
            return;

        Class<?>[] interfaces = clientClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (ICoreClient.class.isAssignableFrom(interfaces[i])) {
                Class<? extends ICoreClient> intf = (Class<? extends ICoreClient>) interfaces[i];
                ICoreManager.addClient(intf, client);
                //logger.info("client(" + client + ") added for " + clientClass.getName());
            }
        }

        Class<?> superClass = clientClass.getSuperclass();
        addClient(client, superClass);
    }

    /**
     * 监听所有client声明实现的ICoreClient的接口
     *
     * @param client
     */

    public static void addClientICoreClient(ICoreClient client) {

        if (client == null) {
            return;
        }

        addClient(client, client.getClass());
    }

    /**
     * 移除对象对某个接口的监听
     *
     * @param clientClass
     * @param client
     */
    public static void removeClient(Class<? extends ICoreClient> clientClass, ICoreClient client) {

        if (clientClass == null || client == null) {
            return;
        }

        ArrayList<ICoreClient> clientList = clients.get(clientClass);
        if (clientList == null) {
            return;
        }

        clientList.remove(client);
        //MLog.verbose(TAG, "client(" + client + ") removed for " + clientClass.getName());
    }


    /**
     * 移除该对象所有监听接口
     *
     * @param client
     */
    public static void removeClientICoreClient(ICoreClient client) {

        if (client == null) {
            return;
        }

        Collection<ArrayList<ICoreClient>> c = clients.values();
        for (ArrayList<ICoreClient> list : c) {
            list.remove(client);
        }

        //MLog.verbose(TAG, "client(" + client + ") removed from all");
    }

    /**
     * TODO 增加Client，支持CoreEvent注解
     *
     * @param client
     */
    public static void addClient(Object client) {
        //MLog.verbose(TAG_EVENT, "AddClient support CoreEvent : " + client);
        if (client == null) {
            MLog.warn(TAG_EVENT, "Don't give me a null client");
            return;
        }

        if (client instanceof ICoreClient) {
            //MLog.verbose(TAG_EVENT, client + " instanceof ICoreClient, add to ICoreClient");
            addClientICoreClient((ICoreClient) client);
        }

        Class<?> originalClass = client.getClass();
        if (originalClass == null) {
            MLog.warn(TAG_EVENT, "Client.getClass() is null");
            return;
        }
        Method[] methods = originalClass.getMethods();

        for (Method method : methods) {
            //在Dalvik虚拟机中，对andfix修改过的方法取Annotation有机率引起ANR，andfix修改的方法不带CoreEvent注解，可以直接跳过。
            if(method.toString().contains("_CF.")){
                MLog.verbose(TAG_EVENT," AndFix fix the method="+method.toString());
                continue;
            }
            CoreEvent event = method.getAnnotation(CoreEvent.class);
            if (event != null) {
                Class<?> clientClass = event.coreClientClass();
                //MLog.verbose(TAG_EVENT, "Client =" + client + ", event=" + event + ",method=" + method.getName());
                if (clientClass != null) {
                    addCoreEvents(client, clientClass);
                    addCoreEventMethodsIfNeeded(client, clientClass, method);
                }
            }
        }
    }

    private static void addCoreEvents(Object client, Class<?> clientClass) {
        Set<Object> clients = coreEvents.get(clientClass);
        if (clients == null) {
            //MLog.verbose(TAG_EVENT, "Clients is null, create new set :" + clientClass);
            clients = new HashSet<Object>();
            coreEvents.put(clientClass, clients);
        }

        clients.add(client);
        //MLog.verbose(TAG_EVENT, "Clients add client " + client + ",size=" + clients.size());
    }

    private static void addCoreEventMethodsIfNeeded(Object client, Class<?> clientClass, /*Class<?> originalClass*/Method m) {
        Map<String, Method> methods = coreEventMethods.get(client);
        if (methods == null) {
            //MLog.verbose(TAG_EVENT, "Client " + client + ",Class " + clientClass + " methods null, create new one");
            methods = new HashMap<String, Method>();
            coreEventMethods.put(client, methods);
        }
        //MLog.verbose(TAG_EVENT, "Client=" + client + ",Class=" + clientClass + ",put method=" + m.getName());
        methods.put(m.getName(), m);
    }

    /**
     * TODO 移除该对象所有监听接口，支持CoreEvent
     *
     * @param client
     */
    public static void removeClient(Object client) {

        if (client == null) {
            return;
        }
        try {
            if (client instanceof ICoreClient) {
            /*if (isDebugSvc())
                MLog.verbose(TAG_EVENT, "Client is ICoreClient, remove core client method");*/
                removeClientICoreClient((ICoreClient) client);
            }

            Collection<Set<Object>> c = coreEvents.values();
            for (Set<Object> events : c) {
                events.remove(client);
            }

            coreEventMethods.remove(client);
        } catch (Throwable throwable) {
            MLog.error("CoreManager", "removeClient error! " + throwable);
        }

        //MLog.verbose(TAG_EVENT, "client(" + client + ") removed from all");
    }

    /**
     * 返回监听该接口的对象列表
     *
     * @param clientClass
     * @return
     */
    public static ArrayList<ICoreClient> getClients(Class<? extends ICoreClient> clientClass) {

        if (clientClass == null) {
            return null;
        }

        ArrayList<ICoreClient> clientList = clients.get(clientClass);
        if (clientList != null) {
            // 每次均构造一个新的对象返回，防止遍历中修改出问题


            clientList = new ArrayList<ICoreClient>(clientList);
        }

        return clientList;
    }

    /**
     * 返回监听该client类的具体执行上下文
     *
     * @param clientClass
     * @return
     */
    public static Set<Object> getCoreEventInvokeContext(Class<? extends ICoreClient> clientClass) {

        if (clientClass == null) {
            return null;
        }

        Set<Object> clientList = coreEvents.get(clientClass);
        if (clientList != null) {
            // 每次均构造一个新的对象返回，防止遍历中修改出问题
            clientList = new HashSet<>(clientList);
        }
        return clientList;
    }

    public static interface ICallBack {
        void onCall(ICoreClient client);
    }

    /**
     * 执行回调接口
     *
     * @param clientClass
     * @param callBack
     */
    public static void notifyClients(Class<? extends ICoreClient> clientClass, ICallBack callBack) {
        if (clientClass == null || callBack == null) {
            return;
        }

        ArrayList<ICoreClient> clientList = ICoreManager.getClients(clientClass);
        if (clientList == null) {
            return;
        }
        try {
            int length = clientList.size();
            for (int i = 0; i < length; i++) {
                ICoreClient ic = clientList.get(i);
                callBack.onCall(ic);
            }
        } catch (Exception e) {
            MLog.error(TAG, e.getMessage(), e);
        }
    }

    /**
     * 回调所有监听了该接口的对象。methodName为回调的方法名
     * 注意：所有用addClient和addEventListener注册了此接口的对象都会被回调
     * 注意：methodName所指定函数的参数列表个数必须匹配。目前没有对参数类型严格检查，使用时要注意
     *
     * @param clientClass
     * @param methodName
     * @param args
     */
    public static void notifyClients(Class<? extends ICoreClient> clientClass, String methodName, Object... args) {
        notifyClientsCoreEvents(clientClass, methodName, args);
        if (clientClass == null || methodName == null || methodName.length() == 0) {
            return;
        }

        ArrayList<ICoreClient> clientList = ICoreManager.getClients(clientClass);
        if (clientList == null) {
            return;
        }

        try {

            HashMap<String, Method> methods = clientMethods.get(clientClass);
            Method method = methods.get(methodName);

            if (method == null) {
                MLog.error(TAG, "cannot find client method " + methodName + " for args[" + args.length + "]: " + Arrays.toString(args));
                return;
            } else if (method.getParameterTypes() == null) {
                MLog.error(TAG, "cannot find client method  param:" + method.getParameterTypes() + " for args[" + args.length + "]: " + Arrays.toString(args));
                return;
            } else if (method.getParameterTypes().length != args.length) {
                MLog.error(TAG, "method " + methodName + " param number not matched: method(" + method.getParameterTypes().length + "), args(" + args.length + ")");
                return;
            }
            for (Object c : clientList) {
                try {
                    method.invoke(c, args);
                } catch (Throwable e) {
                    MLog.error(TAG, "Notify clients method invoke error.", e);
                }
            }
        } catch (Throwable e) {
            MLog.error(TAG, "Notify clients error.", e);
        }
    }

    /**
     * TODO 广播CoreEvent注解事件
     *
     * @param clientClass
     * @param methodName
     * @param args
     */
    public static void notifyClientsCoreEvents(Class<? extends ICoreClient> clientClass, String methodName, Object... args) {

        if (clientClass == null || methodName == null || methodName.length() == 0) {
            return;
        }

        Set<Object> clients = coreEvents.get(clientClass);

        if (clients != null) {
            //MLog.verbose(TAG_EVENT, "Notify core events client size=" + clients.size() + ",clientClass=" + clientClass);
            // 每次均构造一个新的对象返回，防止遍历中修改出问题
            clients = new HashSet<Object>(clients);
            //MLog.verbose(TAG_EVENT, "Notify core events AFTER size=" + clients.size() + ",clientClass=" + clientClass);
        } else {
            return;
        }
        try {
            for (Object c : clients) {
                Map<String, Method> methods = coreEventMethods.get(c);
                if (methods == null) {
                    /*if (isDebugSvc())
                        MLog.verbose(TAG_EVENT, "Notify core events methods is null client="
                            + c + ",method=" + methodName + ",args=" + args);*/
                    continue;
                }
                Method method = methods.get(methodName);
                Class<?>[] types = null;
                if (method != null) {
                    types = method.getParameterTypes();//减少创建小对象，减少timeout崩溃
                }

                if (method == null) {
                /*if (isDebugSvc())
                    MLog.verbose(TAG_EVENT, "Can't find " + c + " has method " + methodName +
                        " for args[" + args.length + "]: " + args.toString());*/
                    continue;
                }else if (types == null) {
                    MLog.error(TAG_EVENT, "Can't find " + c + " has method param null for args[" + args.length + "]: " + args);
                    continue;
                } else if (types.length != args.length) {
                    MLog.error(TAG_EVENT, "Can't find " + c + " has Method " + methodName +
                            " param number not matched: method(" + types.length +
                            "), args(" + args.length + ")");
                    continue;
                }

                /*if (isDebugSvc())
                    MLog.verbose(TAG_EVENT, "Notify core event target=" + c + ",method=" + methodName);*/
                try {
                    method.invoke(c, args);
                } catch (Throwable e) {
                    MLog.error(TAG_EVENT, "Notify core events method invoke error class=" + clientClass
                            + ",method=" + methodName
                            + ",args=" + args, e);
                }
            }

        } catch (Throwable e) {
            MLog.error(TAG_EVENT, "Notify core events error class=" + clientClass + ",method=" + methodName
                    + ",args=" + args, e);
        }
    }

    /*private static boolean isDebugSvc(){
        return (Env.instance().getSvcSetting() == Env.SvcSetting.Dev);
    }*/

    public static <T extends IBaseCore> T getCore(Class<T> cls) {
        return CoreFactory.getCore(cls);
    }


    /*
 * Ent消息透传服务
 * */
    public static IEntCore getEntCore() {
        if (entCore == null) {
            entCore = CoreFactory.getCore(IEntCore.class);
        }
        return entCore;
    }

    /**
     * 拥护信息服务
     *
     * @return 用户信息接口
     */
    public static IUserCore getUserCore() {
        if (userCore == null) {
            userCore = CoreFactory.getCore(IUserCore.class);
        }

        return userCore;
    }

    public static IAuthCore getAuthCore() {
        if (authCore == null) {
            authCore = CoreFactory.getCore(IAuthCore.class);
        }

        return authCore;
    }


    /**
     * 媒体服务
     * 提供媒体信息
     *
     * @return 媒体接口
     */
    public static IMediaCore getMediaCore() {
        if (mediaCore == null) {
            mediaCore = CoreFactory.getCore(IMediaCore.class);
        }

        return mediaCore;
    }


    /**
     * 频道服务
     * 提供频道信息
     *
     * @return 频道接口
     */
    public static IChannelLinkCore getChannelLinkCore(){
        if (channelLinkCore==null){
            channelLinkCore=CoreFactory.getCore(IChannelLinkCore.class);
        }
        return channelLinkCore;
    }

}
