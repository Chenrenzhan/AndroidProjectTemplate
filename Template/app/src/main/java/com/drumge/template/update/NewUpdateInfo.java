package com.drumge.template.update;

import android.os.SystemClock;

import java.util.List;
import java.util.Random;

/**
 *
 */
public class NewUpdateInfo {
    private String n;
    private List<String> cdnList;
    private int ruleId;
    private String md5;
    private String ver;
    private String updateInfo;
    private String link;
    private String note;
    private String apkUrl;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<String> getCdnList() {
        return cdnList;
    }

    public void setCdnList(List<String> cdnList) {
        this.cdnList = cdnList;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCdnAddr() {
        if (cdnList != null && cdnList.size() > 0) {
            //int rand = randInt();//在已经有cdn列表中，随机选一个cdn服务器
            //int pos = rand % cdnList.size();
            //return cdnList.get(pos);
            //cdn第一个才是不限速的连接
            //TODO 未做如果第一个cdn地址有问题,要尝试之后的地址
            return cdnList.get(0);
        }

        return null;
    }

    public static int randInt() {
        Random r = new Random(SystemClock.uptimeMillis());
        int rand = r.nextInt();

        return Math.abs(rand);
    }

    public String getCdnBuildConfigUrl() {
        String cdnServer = getCdnAddr();
        if (getUpdateInfo() != null && cdnServer != null) {
            return "http://" + cdnServer + getUpdateInfo();
        }

        return null;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getCdnApkUrl() {
        String cdnServer = getCdnAddr();
        if (getUpdateInfo() != null && apkUrl != null) {
            return "http://" + cdnServer + apkUrl;
        }

        return null;
    }

    @Override
    public String toString() {
        return "NewUpdateInfo{" +
                "n='" + n + '\'' +
                ", cdnList=" + cdnList +
                ", ruleId=" + ruleId +
                ", md5='" + md5 + '\'' +
                ", ver='" + ver + '\'' +
                ", updateInfo='" + updateInfo + '\'' +
                ", link='" + link + '\'' +
                ", note='" + note + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                '}';
    }
}
