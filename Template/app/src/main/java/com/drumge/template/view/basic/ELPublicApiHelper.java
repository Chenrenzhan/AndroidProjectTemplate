package com.drumge.template.view.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by qiushunming on 16/1/11.
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
