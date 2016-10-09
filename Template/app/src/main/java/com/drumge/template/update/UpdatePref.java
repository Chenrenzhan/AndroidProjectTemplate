package com.drumge.template.update;

import android.content.Context;
import android.content.SharedPreferences;

import com.drumge.template.BasicConfig;
import com.drumge.template.VersionUtil;
import com.drumge.template.YSharedPref;

/**
 *
 */
public class UpdatePref extends YSharedPref {

    private static final String SOURCE_VER = "SOURCE_VER";
    private static final String TARGET_VER = "TARGET_VER";
    private static final String UPDATE_TYPE = "UPDATE_TYPE";
    private static final String UPDATE_N = "UPDATE_N";
    private static final String LAST_CANCEL_VERSION = "LAST_CANCEL_VERSION";
    private static final String LAST_CANCEL_VERSION_TIME = "LAST_CANCEL_VERSION_TIME";
    private static final String VERSION_UPDATE_LASTTIME = "VERSION_UPDATE_LASTTIME";
    private static final String PREV_DOWNLOAD_VER = "prev_download_ver";
    private static final String APP_FIRST_START_TIME = "APP_FIRST_START_TIME";


    private static UpdatePref sInst;


    public synchronized static UpdatePref instance() {
        if(sInst == null){
            SharedPreferences pref = BasicConfig.getInstance()
                    .getAppContext().getSharedPreferences("UpdatePref", Context.MODE_PRIVATE);
            sInst = new UpdatePref(pref);
        }
        return sInst;
    }

    private UpdatePref(SharedPreferences pref) {
        super(pref);
    }

    public void setLastCancelVersion(String version){
        putString(LAST_CANCEL_VERSION, version);
    }

    public String getLastCancelVersion(){
        return getString(LAST_CANCEL_VERSION);
    }

    public void setLastCancelTime(){
        putLong(LAST_CANCEL_VERSION_TIME, System.currentTimeMillis());
    }

    public long getLastCancelTime(){
        return getLong(LAST_CANCEL_VERSION_TIME, 0l);
    }

    public void saveLastVersionUpdateTime(){
        putLong(VERSION_UPDATE_LASTTIME, System.currentTimeMillis());
    }

    public long getLastVersionUpdateTime(){
        return getLong(
                VERSION_UPDATE_LASTTIME, 0);
    }

    public void setAppFirstStartTime() {
        putLong(APP_FIRST_START_TIME, System.currentTimeMillis());
    }

    public long getAppFirstStartTime() {
        return getLong(APP_FIRST_START_TIME, 0L);
    }

    public void saveUpdateInfo(NewUpdateInfo newUpdateInfo, int updateType) {
        putInt(newUpdateInfo.getVer(), newUpdateInfo.getRuleId());
        putString(UpdatePref.TARGET_VER, newUpdateInfo.getVer());
        VersionUtil.Ver ver = VersionUtil.getLocalVer(BasicConfig.getInstance()
                .getAppContext());
        String sv = ver.getVersionName(BasicConfig.getInstance()
                .getAppContext());
        //String sv = "0.0.364";//TODO test
        putString(UpdatePref.SOURCE_VER, sv);
        putInt(UpdatePref.UPDATE_TYPE, updateType);
        //putString(UpdatePref.UPDATE_N, newUpdateInfo.getN());
    }

    public String getSourceVer(){
        return getString(UpdatePref.SOURCE_VER);
    }

    public String getTargetVer(){
        return getString(UpdatePref.TARGET_VER);
    }

    /*public String getUpdateN(){
        return getString(UpdatePref.UPDATE_N);
    }*/

    public int getUpdateType(){
        return getInt(UpdatePref.UPDATE_TYPE, 0);
    }

    public int getRuleIdByVer(String versionName) {
        return getInt(versionName, 0);
    }
}