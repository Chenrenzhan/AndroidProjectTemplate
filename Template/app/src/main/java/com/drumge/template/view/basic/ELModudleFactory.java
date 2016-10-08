package com.drumge.template.view.basic;

import android.text.TextUtils;

/**
 * Created by qiushunming on 16/1/11.
 */
public class ELModudleFactory {

    /**
     * 通过反射new 对象
     * @param name
     * @return
     */
    public static ELAbsModule newModudleInstance(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        try {
            Class<? extends ELAbsModule> moduleClzz = (Class<? extends ELAbsModule>) Class.forName(name);
            if (moduleClzz != null) {
                ELAbsModule instance =  (ELAbsModule)moduleClzz.newInstance();
                return instance;
            }
            return null;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
//
//        if (ELModudleConfig.MODULE_NAME_VOTE.equals(name)) {
//            return new VoteModule();
//        }

    }



}
