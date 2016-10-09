package com.drumge.template.update;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.drumge.template.BasicConfig;
import com.drumge.template.common.MD5Utils;
import com.drumge.template.common.NetworkUtils;
import com.drumge.template.VersionUtil;
import com.drumge.template.common.YYFileUtils;
import com.drumge.template.core.AbstractBaseCore;
import com.drumge.template.core.ICoreManager;
import com.drumge.template.log.MLog;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 更新业务模块
 * @author zhongyongsheng on 14-7-17.
 */
public class UpdateCoreImpl extends AbstractBaseCore implements IUpdateCore {

    private static final int UPDATE_TYPE_NORMAL = 3;//限配额,无流量限制
    //public static final int UPDATE_TYPE_MANUAL = 2;//前台下载,无流量限制,无用
    private static final int UPDATE_TYPE_FORCE  = 1;//强制升级,无流量限制
    private static final int UPDATE_REPORT_S_SUCCESS = 1;//报告成功
    private static final int UPDATE_REPORT_S_ERROR = 0;//报告失败
    private static final int UPDATE_INSTALL_SUCCESS = 901;
    private static final int UPDATE_DOWNLOAD_SUCCESS = 900;//报告下载成功
    private static final int UPDATE_DOWNLOAD_ERROR = 203;//报告下载失败
    private static final int UPDATE_INSTALL_ERROR = 308;//报告安装失败
    private static final int UPDATE_CANCEL = 801;//报告以后再说
    private static final int UPDATE_START = 800;//报告开始下载
    public static final String PID = "entmobile-android";
    private static final String KEY_BABY = "pid=" + PID + "&sv=%s&t=%s&k=%s";
    private static final String DEFAULT_KEY = "sl3$@l43#yG34yY&4R0DF)d#DTe6f!t564%rdr54j6jswe4j";
    private static final long MS_OF_84H = 84 * 60 * 60 * 1000; //84hours
    private static final long UPDATE_QUERY_INTERVAL_72H = 72 * 60 * 60 * 1000;//72hours
    private static final String UPDATE_DIR = "update";

    private Context mContext;
    private AtomicReference<UpdateRequest> mRequest = new AtomicReference<UpdateRequest>();
    private NewUpdateInfo mNewUpdateInfo = new NewUpdateInfo();
    private long mUid = 0;
    private long mImid = 0;
    private boolean mIsForceUpdate;
    private int mUpdateType;
    private File mUpdateDir;

    public UpdateCoreImpl(){
        mContext = BasicConfig.getInstance().getAppContext();
        setUpdateDir(ICoreManager.YYMOBILE_DIR_NAME + File.separator + UPDATE_DIR);
    }

    @Override
    public void update(UpdateRequest updateRequest, boolean isForceUpdate) {
        try {
//            mUid = CoreManager.getAuthCore().getUserId();
//            UserInfo userInfo = CoreManager.getUserCore().getCacheLoginUserInfo();
//            if (userInfo != null){
//                mImid = userInfo.yyId;
//            }

            mIsForceUpdate = isForceUpdate;
            if (isForceUpdate) {
                mUpdateType = UPDATE_TYPE_FORCE;
            } else {
                mUpdateType = UPDATE_TYPE_NORMAL;
            }
            MLog.info(this, "Update req=" + updateRequest + ",uid=" + mUid + ",imid=" + mImid
                    + ",isForceUpdate=" + isForceUpdate);

            if (mRequest.get() != null){
                reportResult(UpdateResult.Updating);
                return;
            }

            mRequest.set(updateRequest);
            switch (updateRequest){
                case ManualCheck:{
                    checkAndGetUpdateInfo();
                    break;
                }
                case Check:{
                    if (needQueryVersionInfo()){
                        checkAndGetUpdateInfo();
                    }else{
                        mRequest.set(null);
                        MLog.debug(this, "Don't need query version info.");
                    }
                    break;
                }
                case Download:{
                    downloadApk();
                    break;
                }
                case Install:{
                    installApk();
                    break;
                }
                case RemindLater:{
                    remindLater();
                    break;
                }
            }
        } catch (Exception e) {
            MLog.error(this, "Update error", e);
            reportResult(UpdateResult.Error);
        }

    }

    @Override
    public NewUpdateInfo getUpdateInfo() {
        return mNewUpdateInfo;
    }

    private void remindLater(){
        reportCancelUpdate();
        mRequest.set(null);
    }

    private void installApk() {
        File file = getDownloadApk();
        if (!isValidUpdateFile(file)) {
            if(!file.delete()){
                MLog.error(this, "Invalid update file delete error.");
            }
            reportResult(UpdateResult.InstallError);
            return;
        }
        try {
            FileOutputStream fs = mContext.openFileOutput(
                    YYFileUtils.getFileName(file.getPath()),
                    Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            fs.close();
        } catch (Exception e) {
            MLog.error(this, "OpenFileOutput error.", e);
            reportResult(UpdateResult.Error);
            return;
        }
        MLog.info(this, "InstallApk, file = " + file + ", length = "
                + file.length());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive").addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent installIntent = PendingIntent.getActivity(mContext, 0, i, 0);
        try {
            installIntent.send();
            mRequest.set(null);
        } catch (PendingIntent.CanceledException e) {
            MLog.error(this, "InstallIntent error.", e);
            reportResult(UpdateResult.Error);
        }
    }

    public void setUpdateDir(String dir){
        try {
            // TODO 更新目录
//            mUpdateDir = DiskCache.getCacheDir(mContext, dir);
//            if (!mUpdateDir.exists()){
//                if(!mUpdateDir.mkdirs()){
//                    MLog.error(this, "Can't create update dir " + mUpdateDir);
//                    return;
//                }
//            }
        } catch (Exception e) {
            MLog.error(this, "Set update dir error", e);
            reportResult(UpdateResult.Error);
        }
    }

    private File getDownloadApk(){
        String url = mNewUpdateInfo.getCdnApkUrl();
        if (url == null)
            return null;
        int index = url.lastIndexOf(File.separatorChar);
        String fileName = "yy.apk";//default name
        if (index != -1) {
            fileName = url.substring(index + 1);
        }
        return new File(mUpdateDir, fileName);
    }

    private void downloadApk() {
        reportStartUpdate();
        if (mNewUpdateInfo == null) {
            reportResult(UpdateResult.DownloadError);
            return;
        }

        File apkFile = getDownloadApk();
        if (apkFile != null){
            if (apkFile.exists()){
                MLog.info(this,
                        "DownloadApk exists download");
                if (isValidUpdateFile(apkFile))
                {
                    MLog.debug(this, "Apk is already downloaded = " + apkFile);
                    reportResult(UpdateResult.Ready);
                    return;
                }else{
                    try {
                        apkFile.delete();
                    } catch (Exception e) {
                        MLog.error(this, "Delete apk error.", e);
                    }
                }
            }
            // TODO 下载文件
//            RequestManager.instance().submitDownloadRequest(mNewUpdateInfo.getCdnApkUrl(),
//                    apkFile.getAbsolutePath(),
//                    new ResponseListener<String>(){
//
//                        @Override
//                        public void onResponse(String response) {
//                            MLog.info(this, "Download response = " + response);
//                            reportResult(UpdateResult.DownloadSuccess);
//                            reportDownloadSuccess();
//                            installApk();
//                        }
//                    },
//                    new ResponseErrorListener(){
//
//                        @Override
//                        public void onErrorResponse(RequestError error) {
//                            reportResult(UpdateResult.DownloadError);
//
//                        }
//                    },
//                    new ProgressListener(){
//
//                        @Override
//                        public void onProgress(ProgressInfo info) {
//                            notifyClients(IUpdateClient.class, "onUpdateProgress",
//                                    info.getProgress(), info.getTotal());
//                        }
//                    },
//                    true
//            );
        }
    }

    private boolean needQueryVersionInfo() {
        long preTime = UpdatePref.instance().getAppFirstStartTime();
        long curTime = System.currentTimeMillis();

        Date predate = new Date(preTime);
        Date curdate = new Date(curTime);

        boolean isSameDay = isTheSameDay(predate, curdate);
        long diff = Math.abs(curTime - preTime);

        MLog.info(this, "IsSameDay=%b, diff=%d, wifi=%b", isSameDay, diff, isWifi());
        if (diff > UPDATE_QUERY_INTERVAL_72H && checkUpdateRequest() && isWifi()) {//增加只在wifi下检查
            return true;
        }
        return false;
    }

    //TODO 需要异步执行
    private boolean isValidUpdateFile(File apkFile) {
        if (mNewUpdateInfo == null || apkFile == null || !apkFile.exists()) {
            MLog.info(this, "UpdateService.isValidUpdateFile, mNewUpdateInfo = " + mNewUpdateInfo +
                    ", apkFile=" + apkFile + " not exists");
            return false;
        }
        String md5 = null;
        try {
            md5 = MD5Utils.getFileMD5String(apkFile);
        } catch (IOException e) {
            MLog.error(this, "GetFileMD5String error", e);
            return false;
        }
        boolean isSameMd5 = TextUtils.equals(mNewUpdateInfo.getMd5(), md5);
        MLog.info(this, "UpdateService.isValidUpdateFile, update = " + apkFile +
                ", md5 same = " + isSameMd5);
        if (!isSameMd5) {
            MLog.info(this,
                    "UpdateService.isValidUpdateFile, file = "
                            + apkFile.getPath() + ", length = "
                            + apkFile.length() + ", info.md5 = " + mNewUpdateInfo.getMd5()
                            + ", file md5 = " + md5);
        }
        return isSameMd5;
    }

    private boolean isWifi(){
        try {
            int networkType = NetworkUtils.getNetworkType(mContext);
            boolean isWifi = NetworkUtils.NET_WIFI == networkType;
            return isWifi;
        }catch (Exception e){
            return false;
        }
    }

    public boolean isTheSameDay(Date d1, Date d2){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断是否需要更新的接口*
     */
    public void checkAndGetUpdateInfo() {
        if (!NetworkUtils.isNetworkAvailable(mContext))
        {
            reportResult(UpdateResult.NetworkError);
            return;
        }
        VersionUtil.Ver ver = VersionUtil.getLocalVer(mContext);
        String sv = ver.getVersionName(mContext);

        Calendar now = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmss");
        String t = fmt.format(now.getTime());

        String baby = String.format(KEY_BABY, sv, t, DEFAULT_KEY);
        String nounce = MD5Utils.getMD5String(baby);

        // TODO 网络请求
//        RequestParam requestParam = new DefaultRequestParam();
//        requestParam.put("pid", PID);
//        requestParam.put("sv", sv);
//        requestParam.put("t", t);
//        requestParam.put("uinfo_mc", AppMetaDataUtil.getChannelID(mContext));
//        requestParam.put("uinfo_sp", getCarrierOperator()); //判断运营商
//        requestParam.put("uinfo_ns", getNetString());
//        requestParam.put("uinfo_ov", Build.VERSION.RELEASE);
//        requestParam.put("f", String.valueOf(mUpdateType));
//        requestParam.put("uid", String.valueOf(mUid));
//        requestParam.put("yid", String.valueOf(mImid));
//        requestParam.put("n", nounce);
//
//        RequestManager.instance().submitStringQueryRequest(UriProvider.UPDATE_HOST + "/check4update", requestParam,
//            new ResponseListener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    if (response != null && response.length() > 0) {//有更新信息
//                        parseIsNeedUpdateJson(response);
//                        getDetailUpdateInfo();
//
//                    }else{
//                        reportResult(UpdateResult.Recent);
//                    }
//                }
//            },
//            new ResponseErrorListener() {
//                @Override
//                public void onErrorResponse(RequestError error) {
//                    reportResult(UpdateResult.NetworkError);
//                }
//            }
//        );
    }

    private void parseIsNeedUpdateJson(String response) {
        MLog.info(this, "Parse update info = " + response);

        NewUpdateInfo newUpdateInfo = new NewUpdateInfo();
        JSONObject json = null;
        try {
            json = new JSONObject(response);
            JSONObject description = json.optJSONObject("description");
            if (null == description) {
                return;
            }
            String link = description.optString("link");
            String note = description.optString("note");

            if (null != link) {
                try {
                    link = URLDecoder.decode(link, "UTF-8");
                    newUpdateInfo.setLink(link);
                } catch (UnsupportedEncodingException e) {
                    MLog.error(this, "Decode link error", e);
                }
            }

            if (null != note) {
                try {
                    note = URLDecoder.decode(note, "UTF-8");
                    newUpdateInfo.setNote(note);
                } catch (UnsupportedEncodingException e) {
                    MLog.error(this, "Decode note error", e);
                }
            }

            int r = json.optInt("r");
            newUpdateInfo.setRuleId(r);

            String updateInfo = json.optString("updateInfo");
            newUpdateInfo.setUpdateInfo(updateInfo);

            MLog.debug(this, "Parse update link = " + link + " note = " + note);

            String cdnl = json.optString("cdnl");
            if (cdnl != null) {
                String[] cdnls = cdnl.split("\\|");
                List<String> cdnlList = new ArrayList<String>();
                if (cdnls != null && cdnls.length > 0) {
                    for (int i = 0; i < cdnls.length; i++) {
                        cdnlList.add(cdnls[i]);
                    }
                }
                newUpdateInfo.setCdnList(cdnlList);
            }

            //增加记录,report时使用这个n
            String n = json.getString("n");
            if (n != null)
            {
                newUpdateInfo.setN(n);
            }
        } catch (Exception e) {
            MLog.error(this, "", e);
        }
        mNewUpdateInfo = newUpdateInfo;
    }

    private void getDetailUpdateInfo(){
        if (mNewUpdateInfo.getCdnBuildConfigUrl() != null) {

            // TODO 
//            RequestManager.instance().submitStringQueryRequest(mNewUpdateInfo.getCdnBuildConfigUrl(),
//                    null,
//                    new ResponseListener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            if (response != null && response.length() > 0) {//有xml信息
//                                parseUpdateDetail(response);
//                                UpdatePref.instance().saveUpdateInfo(mNewUpdateInfo, mUpdateType);
//                                reportResult(UpdateResult.NeedDownload);
//                            } else {
//                                reportResult(UpdateResult.Recent);
//                            }
//                        }
//                    },
//                    new ResponseErrorListener() {
//                        @Override
//                        public void onErrorResponse(RequestError error) {
//                            reportResult(UpdateResult.NetworkError);
//                        }
//                    }
//            );
        }
    }

    public void parseUpdateDetail(String response) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            StringReader stringReader = new StringReader(response);
            InputSource inputSource = new InputSource(stringReader);
            document = builder.parse(inputSource);

            // 注意，此时xml文件已经都被装入内存中的document对象里了
            Element root = document.getDocumentElement();// 取得根节点（元素节点）
            String ver = root.getAttribute("version");
            if (null != ver) {
                MLog.debug(this, "Parse update detail ver = %s", ver);
            }
            mNewUpdateInfo.setVer(ver);

            NodeList nodeApk = root.getElementsByTagName("apk");
            if (nodeApk.getLength() > 0) {
                Element apkElement = (Element) nodeApk.item(0);
                String apk_url = String.valueOf(apkElement.getAttribute("url"));
                String apk_hash = String.valueOf(apkElement.getAttribute("hash"));
                mNewUpdateInfo.setMd5(getApkHash(apk_hash));
                mNewUpdateInfo.setApkUrl(apk_url);
                MLog.debug(this, "Parse update detail apk_url = %s,apk_hash =%s", apk_url, apk_hash);
            }

        } catch (Exception e) {
            MLog.error(this, "", e);
            reportResult(UpdateResult.Error);
        }
    }

    public String getApkHash(String apkHash) {
        String[] hashcode = apkHash.split("\\}");
        if (hashcode != null && hashcode.length > 1) {
            return hashcode[1];
        }
        return null;
    }

    private void reportResult(UpdateResult result) {
        UpdateRequest request = mRequest.get();
        if (request != null) {
            MLog.info(this, "ReportResult Request = " + request
                    + ", result = " + result);
            if (result != UpdateResult.Updating) {
                mRequest.set(null);
            }

            switch (result){
                case DownloadError:
                    reportDownloadError();
                    break;
                case InstallError:
                    reportInstallError();
                    break;
            }

            if (request == UpdateRequest.Check
                    && (result == UpdateResult.Downloading
                    || result == UpdateResult.DownloadError
                    || result == UpdateResult.Recent || result == UpdateResult.Error)) {
                return;
            }

            //当有新版本时,首次弹出提示框,用户若点击以后再说,则84小时后若发现新版本,才弹出提示
            if (UpdateResult.NeedDownload.equals(result) && request == UpdateRequest.Check){
                String lastCancelVersion = UpdatePref.instance().getLastCancelVersion();
                if (lastCancelVersion != null){
                    long lastCancelTime = UpdatePref.instance().getLastCancelTime();
                    MLog.info(this, "lastCancelVersion=" + lastCancelVersion + ", lastCancelTime=" + lastCancelTime);
                    if (lastCancelVersion.equals(mNewUpdateInfo.getVer())
                            && isIn84hour(lastCancelTime, System.currentTimeMillis())){
                        MLog.info(this, "This update is cancel in 84 hours.");
                        return;
                    }
                }
            }

            try
            {
                notifyClients(IUpdateClient.class, "onUpdateResult", result, mIsForceUpdate);
            }catch (Exception e)
            {
                MLog.error(this, "notifyEvent error result=" + result, e);
            }
        }
    }

    /**
     * 当有新版本时,首次弹出提示框,用户若点击以后再说,则84小时后若发现新版本,才弹出提示
     *
     * @return
     */
    private boolean checkUpdateRequest() {
        String lastCancelVersion = UpdatePref.instance().getLastCancelVersion();
        if (lastCancelVersion != null) {
            long lastCancelTime = UpdatePref.instance().getLastCancelTime();
            MLog.info(this, "checkUpdateRequest lastCancelVersion=" + lastCancelVersion + ", lastCancelTime=" + lastCancelTime);
            if (lastCancelVersion.equals(mNewUpdateInfo.getVer())
                    && isIn84hour(lastCancelTime, System.currentTimeMillis())) {
                MLog.info(this, "checkUpdateRequest This update is cancel in 84 hours.");
                return false;
            }
        }
        return true;
    }

    /**
     * 是否84小时内
     * @return
     */
    private boolean isIn84hour(long time, long time2) {
        long diff = Math.abs(time2 - time);
        boolean result = diff < MS_OF_84H;
        return result;
    }

    private String getCarrierOperator(){
        try {
            TelephonyManager tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String operator = tm.getSimOperator();
            if (operator != null){
                if (operator.equals("46000") || operator.equals("46002")){
                    return "chinamobile";
                } else if (operator.equals("46001")){
                    return "chinaunicom";
                } else if (operator.equals("46003")){
                    return "chinatelecom";
                }
            }
        } catch (Exception e) {
            MLog.error("UpdateCore", "GetCarrierOperator error", e);
        }
        return "nosp";
    }

    private String getNetString(){
        int netType = NetworkUtils.getNetworkType(mContext);
        if(netType == NetworkUtils.NET_2G){
            return "2g";
        }else if(netType == NetworkUtils.NET_3G){
            return "3g";
        }else if(netType == NetworkUtils.NET_WIFI){
            return "wifi";
        }
        return "";
    }

    /**
     * 升级成功报告
     */
    public void checkIfNeedReportUpdateSuccess() {
        MLog.debug(this, "Check need reportUpdateSuccess");
        VersionUtil.Ver ver = VersionUtil.getLocalVer(mContext);
        String versionName = ver.getVersionName(mContext);
        if (!TextUtils.isEmpty(versionName)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(versionName);
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_INSTALL_SUCCESS, UPDATE_REPORT_S_SUCCESS);
                UpdatePref.instance().clear();//清除数据
                MLog.info(this, "ReportUpdateSuccess ruleId %d", ruleId);
            }
        }
    }

    /**
     * 更新包下载成功报告
     */
    public void reportDownloadSuccess() {
        MLog.info(this, "ReportDownloadSuccess");
        String targetVer = UpdatePref.instance().getTargetVer();
        if (!TextUtils.isEmpty(targetVer)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(targetVer);
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_DOWNLOAD_SUCCESS, UPDATE_REPORT_S_SUCCESS);
                MLog.info(this, "ReportDownloadSuccess ruleId %d", ruleId);
            }
        }
    }

    /**
     * 下载中socket error
     */
    private void reportDownloadError() {
        MLog.info(this, "ReportDownloadError");
        String targetVer = UpdatePref.instance().getTargetVer();
        if (!TextUtils.isEmpty(targetVer)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(targetVer);
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_DOWNLOAD_ERROR, UPDATE_REPORT_S_ERROR);
                MLog.info(this, "ReportDownloadError ruleId %d", ruleId);
            }
        }
    }

    /**
     * 安装前验证md5失败
     */
    private void reportInstallError() {
        MLog.info(this, "ReportInstallError");
        String targetVer = UpdatePref.instance().getTargetVer();
        if (!TextUtils.isEmpty(targetVer)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(targetVer);
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_INSTALL_ERROR, UPDATE_REPORT_S_ERROR);
                MLog.info(this, "ReportInstallError ruleId %d", ruleId);
            }
        }
    }

    /**
     * 点击以后再说按钮
     */
    public void reportCancelUpdate() {
        MLog.info(this, "ReportCancelUpdate");
        String targetVer = UpdatePref.instance().getTargetVer();
        if (!TextUtils.isEmpty(targetVer)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(targetVer);
            UpdatePref.instance().setLastCancelVersion(targetVer);
            UpdatePref.instance().setLastCancelTime();
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_CANCEL, UPDATE_REPORT_S_SUCCESS);
                MLog.info(this, "ReportCancelUpdate ruleId %d", ruleId);
            }
        }

    }

    /**
     * 点击开始更新按钮
     */
    public void reportStartUpdate(){
        MLog.info(this, "ReportStartUpdate");
        String targetVer = UpdatePref.instance().getTargetVer();
        if (!TextUtils.isEmpty(targetVer)) {
            int ruleId = UpdatePref.instance().getRuleIdByVer(targetVer);
            if (ruleId != 0) {
                sendReportInfo(ruleId, UPDATE_START, UPDATE_REPORT_S_SUCCESS);
                MLog.info(this, "ReportStartUpdate ruleId %d", ruleId);
            }
        }
    }

    public void sendReportInfo(final int ruleId, final int scode, final int s) {
        try {
            String sv = UpdatePref.instance().getSourceVer();
            if (sv == null)
            {
                sv = "";
            }

            Calendar now = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmss");
            String t = fmt.format(now.getTime());

            String targetVer = UpdatePref.instance().getTargetVer();
            int updateType = UpdatePref.instance().getUpdateType();
            String updateN = null;
            if (TextUtils.isEmpty(updateN))
            {
                MLog.info(this, "UPDATE_N is null, create new n.");
                String baby = String.format(KEY_BABY, sv, t, DEFAULT_KEY);
                updateN = MD5Utils.getMD5String(baby);
            }

//            RequestParam requestParam = new DefaultRequestParam();
//            requestParam.put("tv", targetVer);
//            requestParam.put("s", String.valueOf(s));
//            requestParam.put("scode", String.valueOf(scode));
//            requestParam.put("r", String.valueOf(ruleId));
//            requestParam.put("pid", PID);
//            requestParam.put("sv", sv);
//            requestParam.put("t", t);
//            requestParam.put("uinfo_mc", AppMetaDataUtil.getChannelID(mContext));
//            requestParam.put("uinfo_sp", getCarrierOperator()); //判断运营商
//            requestParam.put("uinfo_ns", getNetString());
//            requestParam.put("f", String.valueOf(updateType));
//            requestParam.put("uid", String.valueOf(mUid));
//            requestParam.put("yid", String.valueOf(mImid));
//            requestParam.put("n", updateN);
//
//            //"%s/report?%s&tv=%s&s=%d&scode=%d&r=%d&uid=%d&yid=%d&n=%s&uinfo_mc=%s&uinfo_sp=%s&uinfo_ns=%s&f=%d"
//            RequestManager.instance().submitStringQueryRequest(UriProvider.UPDATE_HOST + "/report", requestParam,
//                    new ResponseListener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            MLog.debug(this, "Report success ruleId=%d scode=%d s=%d", ruleId, scode, s);
//                        }
//                    },
//                    new ResponseErrorListener() {
//                        @Override
//                        public void onErrorResponse(RequestError error) {
//                        }
//                    }
//            );
        } catch (Exception e) {
            MLog.error(this, "SendReport error.", e);
        }
    }
}
