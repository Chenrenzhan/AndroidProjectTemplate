package com.drumge.template.view.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.drumge.template.NetworkUtils;
import com.drumge.template.SafeDispatchHandler;
import com.drumge.template.log.MLog;
import com.drumge.template.view.IDataStatus;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xujiexing on 14-6-5.
 */
public class BaseActivity extends FragmentActivity implements IDataStatus {
    private Context mContext;
    private SafeDispatchHandler mHandler = new SafeDispatchHandler(Looper.getMainLooper());
    private static boolean isForeground;
    private boolean isResume;
    private boolean isForceUpdate = false;
    protected boolean mKickoffBack2Main = true;
    private Toast mToast;
    protected String autoFinish = "";
    private Intent mSplashIntent = null;//启动时自动登录密码错误，或者需要需要验证码时候，跳到登录页面

    //    public static final String KEY_PIC_NULL_COUNT = "key_pic_null_count";
//    public static final String KEY_PIC_NULL_SEND = "key_pic_null_send";
    public static final String TAG_LOG = "BaseActivity";
    public static final String TO_LOGIN_AGAIN = "to_login_again";
    public static final int UREASON_COM_KICKOFF = 10;
    public static final int UREASON_CPW_KICKOFF = 12;
    public static final int UREASON_MOBILE_KICKOFF = 11;
    public static final int UREASON_FJ_KICKOFF = 13;//用户被封禁，405等错误
    public final static LinkedList<WeakReference<Activity>> stack = new LinkedList<WeakReference<Activity>>();

    //接收到用户正在绑定YY帐号和手机密保的通知事件时，此时即使被KickOff也不用弹出登录提示，因为绑定页面自己处理登录引导
    protected boolean bindingYYAccount;
    private long uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        UrgentRun.ensureUrgentRun();
        mContext = this;
        super.onCreate(savedInstanceState);
        MLog.verbose(this, "activity oncreate");
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return super.getSupportFragmentManager();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Throwable th) {
            MLog.error(this, "super.onResume()", th);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public static boolean isForeground() {
        return isForeground;
    }

    protected boolean isResume() {
        return isResume;
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            //注册 HOME键直播间后台广播
            if (!mHomeKeyEventRegisterReceiver) {
                mHomeKeyEventRegisterReceiver = true;
                if (mHomeKeyEventReceiver != null) {
                    registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                }
            }
        } catch (Throwable e) {
            MLog.error(this, "mHomeKeyEventReceiver is registerReceiver e = " + e);
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            //移除 HOME键直播间后台广播
            if (mHomeKeyEventRegisterReceiver) {
                if (mHomeKeyEventReceiver != null) {
                    unregisterReceiver(mHomeKeyEventReceiver);
                }
                mHomeKeyEventRegisterReceiver = false;
            }
        } catch (Throwable e) {
            MLog.error(this, "mHomeKeyEventReceiver is unregisterReceiver e = " + e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.verbose(this, "xuwakao, activity onDestroy");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * add by cheng @2015-08-05 for fix crash:
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * at android.support.v4.app.FragmentActivity.onBackPressed(FragmentActivity.java:179)
     */
    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception ex) {
            MLog.error(this, ex);
        }
    }

    //判断应用是否后台运行
    private boolean isBackgroundRunning() {
        String processName = this.getPackageName();

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (activityManager == null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.equalsIgnoreCase(processName)) {
                boolean isBackground = process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    //判断当前activity是否为栈顶的活动activity
    protected boolean isActivityOnTop(Activity activity) {
        if (activity == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            if (tasksInfo.get(0).topActivity.getClassName().equals(activity.getClass().getName())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isTargetActivity(String classname) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            if (tasksInfo.get(0).topActivity.getClassName().equals(classname)) {
                return true;
            }
        }
        return false;
    }


    /**
     * --------------------------------------------------
     * -------------------------数据状态状态相关-------------
     * --------------------------------------------------
     */

    private static final String STATUS_TAG = "STATUS_TAG";

    @Override
    public View.OnClickListener getLoadListener() {
        return null;
    }

    @Override
    public View.OnClickListener getNoMobileLiveDataListener() {
        return null;
    }

    @Override
    public View.OnClickListener getLoadMoreListener() {
        return null;
    }

    @Override
    public void showLoading() {
        showLoading(0, 0);
    }

    @Override
    public void showLoading(View view) {
        showLoading(view, 0, 0);
    }

    @Override
    public void showReload() {
        showReload(0, 0);
    }

    @Override
    public void showNoData() {
        showNoData(0, 0);
    }

    @Override
    public void showNoData(int drawable, CharSequence charSequence) {

    }

    @Override
    public void showNoData(View view, int drawable, CharSequence charSequence) {

    }

    @Override
    public void showNoDataWithBtn(int drawable, String tips, String btnText, View.OnClickListener btnClickListener) {

    }

    @Override
    public void showLoading(int drawable, int tips) {
        if (!checkActivityValid()) {
            return;
        }

//        View status = findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        Fragment fragment = LoadingFragment.newInstance(drawable, tips);
//        getSupportFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showLoading(View view, int drawable, int tips) {

    }

    @Override
    public void showReload(int drawable, int tips) {
        if (!checkActivityValid()) {
            return;
        }

//        View status = findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        ReloadFragment fragment = ReloadFragment.newInstance(drawable, tips);
//        fragment.setListener(getLoadListener());
//        getSupportFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showReload(View view, int drawable, int tips) {

    }

    @Override
    public void showNoData(int drawable, int tips) {
        if (!checkActivityValid()) {
            return;
        }

//        View status = findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NoDataFragment fragment = null;
//        if (tips <= 0) {
//            fragment = NoDataFragment.newInstance();
//        } else {
//            fragment = NoDataFragment.newInstance(drawable, getString(tips));
//        }
//        fragment.setListener(getLoadListener());
//        getSupportFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showNoData(View view, int drawable, int tips) {

    }

    @Override
    public void showNetworkErr() {
        if (!checkActivityValid()) {
            return;
        }

//        View status = findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NetworkErrorFragment fragment = NetworkErrorFragment.newInstance();
//        fragment.setListener(getLoadListener());
//        getSupportFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showNoMobileLiveData() {
        if (!checkActivityValid())
            return;

//        View status = findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NoMobileLiveFragment fragment = NoMobileLiveFragment.newInstance();
//        fragment.setListener(getNoMobileLiveDataListener());
//        getSupportFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void hideStatus() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STATUS_TAG);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    @Override
    public void showPageError(int tips) {
        if (!checkActivityValid()) {
            return;
        }

//        View more = findViewById(R.id.loading_more);
//        if (more == null) {
//            MLog.error(this, "xuwakao, showReload more is NULL");
//            return;
//        }
//        StatusLayout statusLayout = (StatusLayout) more.getParent();
//        statusLayout.showErrorPage(tips, getLoadMoreListener());
    }

    @Override
    public void showPageError(View view, int tips) {

    }

    @Override
    public void showPageLoading() {
        if (!checkActivityValid()) {
            return;
        }

//        View more = findViewById(R.id.loading_more);
//        if (more == null) {
//            MLog.error(this, "xuwakao, showReload more is NULL");
//            return;
//        }
//        StatusLayout statusLayout = (StatusLayout) more.getParent();
//        statusLayout.showLoadMore();
    }

    @TargetApi(17)
    public boolean checkActivityValid() {
        if (isFinishing()) {
            //MLog.warn(this, "activity is finishing");
            return false;
        }

        if (Build.VERSION.SDK_INT >= 17 && isDestroyed()) {
            //MLog.warn(this, "activity is isDestroyed");
            return false;
        }
        return true;
    }

    /**--------------------------------------------------
     -------------------------网络状态状态相关-------------
     --------------------------------------------------*/

    /**
     * 当前网络是否可用
     *
     * @return
     */
    public boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkStrictlyAvailable(this);
    }


    public boolean checkNetToast() {
        boolean falg = isNetworkAvailable();
//        if (!falg)
//            SingleToastUtil.showToast(getContext(), R.string.str_network_not_capable);
            //Toast.makeText(getContext(), R.string.str_network_not_capable, Toast.LENGTH_SHORT).show();
            return falg;

    }



    /**
     * --------------------------------------------------
     * -------------------------UI基本功能------------------
     * --------------------------------------------------
     */
    public void hideIME() {
        View v = getCurrentFocus();
        if (null != v)
            hideIME(v);
    }

    public void hideIME(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showIME(final View vv) {
        View v = vv;
        if (null == v) {
            v = getCurrentFocus();
            if (null == v)
                return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 通用消息提示
     *
     * @param resId
     */
    public void toast(int resId) {
        toast(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 通用消息提示
     *
     * @param resId
     * @param length
     */
    public void toast(int resId, int length) {
        if (mToast == null) {
            if (getContext() != null) {
                mToast = Toast.makeText(getContext(), resId, length);
            } else {
                return;
            }
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    /**
     * 通用消息提示
     *
     * @param message
     */
    public void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 通用消息提示
     *
     * @param message
     * @param length
     */
    public void toast(String message, int length) {
        if (mToast == null) {
            if (getContext() != null) {
                mToast = Toast.makeText(getContext(), message, length);
            } else {
                return;
            }
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    @SuppressWarnings("unchecked")
    public <T> T findFragmentById(int id, Class<T> clazz) {
        T f = (T) this.getSupportFragmentManager().findFragmentById(id);
        return f;
    }

    @SuppressWarnings("unchecked")
    public <T> T findFragmentByTag(String tag, Class<T> clazz) {
        T f = (T) this.getSupportFragmentManager().findFragmentByTag(tag);
        return f;
    }

    public Handler getHandler() {
        return mHandler;
    }


    //是否已经注册后台home键广播
    private boolean mHomeKeyEventRegisterReceiver = false;

    public BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    /*//表示按了home键,程序到了后台
                    if (CoreManager.getChannelCore().getChannelState() != ChannelState.No_Channel) {
                        channelForegroundNotify();
                    }
                    */
//                    CoreManager.notifyClients(IHomeKeyEventReceiverClient.class, "onHomePressed");
                    //CoreManager.getCore(ISplashCore.class).querySplashAd(); //更新闪屏机制
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
//                    CoreManager.notifyClients(IHomeKeyEventReceiverClient.class, "onHomeLongPressed");
                }
            }
        }
    };

}
