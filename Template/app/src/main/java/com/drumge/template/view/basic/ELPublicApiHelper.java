package com.drumge.template.view.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 根据module的类的名字，反射调用类的方法
 * 使用： 
 * 1、先调用{@init()}初始化，保存所有的module的Map<类名，Module>
 * 2、低调用{@invode()}通过反射调用module的某个方法
 */
public class ELPublicApiHelper {
    private Map<String , ELAbsModule> allModudles;


    private static ELPublicApiHelper ourInstance = new ELPublicApiHelper();

    public static ELPublicApiHelper getInstance() {
        return ourInstance;
    }

    private ELPublicApiHelper() {

    }

    public void init(Map<String , ELAbsModule> allModudles){
        this.allModudles = allModudles;
    }

    public void uninit(){
        if (allModudles != null) {
            allModudles = null;
        }
    }

    public Object invode(String clazz, String methodName, Object...params) {

        int paramsLen = params.length;
        Class<?>[] paramsTypes = new Class[paramsLen];

        for (int index = 0; index < paramsLen; index++) {
            paramsTypes[index] = params[index].getClass();
        }

        Class clz = null;
        try {
            clz = Class.forName(clazz);
            Method method = clz.getMethod(methodName, paramsTypes);
            return method.invoke(allModudles.get(clazz), params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new Object();
    }
}
