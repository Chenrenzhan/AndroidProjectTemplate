package com.drumge.template.view.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.drumge.template.NetworkUtils;
import com.drumge.template.R;
import com.drumge.template.SafeDispatchHandler;
import com.drumge.template.log.MLog;
import com.drumge.template.view.IDataStatus;

import java.util.List;

/**
 * Class Name:PopupComponent
 * Description:公共的Popup fragment
 * Author:zengyan
 * Date:2015/11/30
 * Modified History:
 */
public abstract class BasePopupComponent extends PopupComponent implements IPopupComponent, IDataStatus {

    private Context mContext;
    private Handler mHandler = new SafeDispatchHandler();
    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MLog.verbose(this, "xuwakao, fragment onCreate");
        mContext = getActivity();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onResume() {
        super.onResume();
        //MLog.verbose(this, "xuwakao, fragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //MLog.verbose(this, "xuwakao, fragment onPause");
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mContext = null;
        //MLog.verbose(this, "xuwakao, fragment onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MLog.verbose(this, "xuwakao, fragment onDestroyView");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(STATUS_TAG);
//            if (fragment != null && fragment instanceof NoMobileLiveFragment) {
//                ((NoMobileLiveFragment) fragment).setListener(getNoMobileLiveDataListener());
//            } else if (fragment != null && fragment instanceof IStatusFragment) {
//                MLog.verbose(this, "xuwakao, onViewStateRestored re-set listener");
//                ((IStatusFragment) fragment).setListener(getLoadListener());
//            }

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.verbose(this, "xuwakao, fragment onHiddenChanged hidden = " + hidden);
    }

    /**
     * --------------------------------------------------
     * -------------------------数据状态相关-----------------
     * --------------------------------------------------
     */

    public static final String STATUS_TAG = "STATUS_TAG";

    @Override
    public View.OnClickListener getLoadListener() {
        return null;
    }

    @Override
    public View.OnClickListener getLoadMoreListener() {
        return null;
    }

    @Override
    public View.OnClickListener getNoMobileLiveDataListener() {
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
    public void showLoading(int drawable, int tips) {
        showLoading(getView(), drawable, tips);
    }

    @Override
    public void showLoading(View view, int drawable, int tips) {
        if (!checkActivityValid())
            return;

        if (view == null) {
            //MLog.error(this, "xuwakao, showLoading view is NULL");
            return;
        }
    }

    @Override
    public void showReload(int drawable, int tips) {
        showReload(getView(), drawable, tips);
    }

    @Override
    public void showReload(View view, int drawable, int tips) {
        if (!checkActivityValid())
            return;

        if (view == null) {
            MLog.error(this, "xuwakao, showReload view is NULL");
            return;
        }
    }

    @Override
    public void showNoData() {
        showNoData(0, 0);
    }

    @Override
    public void showNoData(int drawable, int tips) {
        showNoData(getView(), drawable, tips);
    }


    @Override
    public void showNoData(int drawable, CharSequence charSequence) {
        showNoData(getView(), drawable, charSequence);
    }

    @Override
    public void showNoData(View view, int drawable, int tips) {
        if (tips <= 0) {
            showNoData(view, drawable, "");
        } else {
            showNoData(view, drawable, getString(tips));
        }
    }

    @Override
    public void showNoData(View view, int drawable, CharSequence charSequence) {
        if (!checkActivityValid())
            return;

        if (view == null) {
            MLog.error(this, "xuwakao, showNoData view is NULL");
            return;
        }
    }

    @Override
    public void showNoDataWithBtn(int drawable, String tips, String btnText, View.OnClickListener btnClickListener) {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "showNoDataWithBtn view is NULL");
            return;
        }
    }

    @Override
    public void showNetworkErr() {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "xuwakao, showNetworkErr view is NULL");
            return;
        }
    }

    @Override
    public void hideStatus() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(STATUS_TAG);
        if (fragment != null)
            getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        else
            MLog.verbose(this, "xuwakao, status fragment is NULL");
    }

    @Override
    public void showNoMobileLiveData() {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "xuwakao, showNoMobileLiveData view is NULL");
            return;
        }
    }

    @Override
    public void showPageError(int tips) {
        showPageError(getView(), tips);
    }

    @Override
    public void showPageError(View view, int tips) {
        if (!checkActivityValid())
            return;
    }

    @Override
    public void showPageLoading() {
        if (!checkActivityValid())
            return;
    }

    @TargetApi(17)
    protected boolean checkActivityValid() {
        if (getActivity() == null) {
            //MLog.warn(this, "Fragment " + this + " not attached to Activity");
            return false;
        }

        if (getActivity().isFinishing()) {
            //MLog.warn(this, "activity is finishing");
            return false;
        }

        if (Build.VERSION.SDK_INT >= 17 && getActivity().isDestroyed()) {
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
        return NetworkUtils.isNetworkStrictlyAvailable(getContext());
    }


    public boolean checkNetToast() {
        boolean falg = isNetworkAvailable();
        if (!falg &&getContext()!=null){
//            toast(R.string.str_network_not_capable, Toast.LENGTH_SHORT);
            
        }

        return falg;

    }

    /**--------------------------------------------------
     -------------------------UI基本功能------------------
     --------------------------------------------------*/
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
            if (getContext()!=null){
                mToast = Toast.makeText(getContext(), resId, length);
            }
        } else {
            mToast.setText(resId);
        }
        if (mToast!=null){
            mToast.show();
        }
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
            if (getContext()!=null){
                mToast = Toast.makeText(getContext(), message, length);
            }
        } else {
            mToast.setText(message);
        }
        if (mToast!=null){
            mToast.show();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T findFragmentById(int id, Class<T> clazz) {
        T f = (T) getActivity().getSupportFragmentManager().findFragmentById(id);
        return f;
    }

    @SuppressWarnings("unchecked")
    public <T> T findChildFragmentById(int id, Class<T> clazz) {
        T f = (T) this.getChildFragmentManager().findFragmentById(id);
        return f;
    }

    @SuppressWarnings("unchecked")
    public <T> T findFragmentByTag(String tag, Class<T> clazz) {
        T f = (T) getActivity().getSupportFragmentManager().findFragmentByTag(tag);
        return f;
    }

    @SuppressWarnings("unchecked")
    public <T> T getActivity(Class<T> clazz) {
        T f = (T) getActivity();
        return f;
    }

    protected Handler getHandler() {
        return mHandler;
    }


}
