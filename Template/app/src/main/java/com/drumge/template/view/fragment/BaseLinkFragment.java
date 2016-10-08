package com.drumge.template.view.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.drumge.template.NetworkUtils;
import com.drumge.template.SafeDispatchHandler;
import com.drumge.template.log.MLog;
import com.drumge.template.view.IDataStatus;

import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class BaseLinkFragment extends Fragment implements IDataStatus {

    private Context mContext;
    private SafeDispatchHandler mHandler = new SafeDispatchHandler(Looper.getMainLooper());
    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.info(this, "fragment(%s) onCreate", getClass().getSimpleName());
        mContext = getActivity();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onResume() {
        super.onResume();
        MLog.info(this, "fragment(%s) onResume", getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MLog.info(this, "fragment(%s) onPause", getClass().getSimpleName());
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MLog.info(this, "fragment(%s) onStop", getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mContext != null) {
            mContext = null;
        }
        MLog.info(this, "fragment(%s) onDestroy", getClass().getSimpleName());
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
//        View status = view.findViewById(R.id.status_layout);
//        if (status == null || status.getId() <= 0) {
//            //MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        Fragment fragment = LoadingFragment.newInstance(drawable, tips);
//        getChildFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
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
//        View status = view.findViewById(R.id.status_layout);
//        if (status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        ReloadFragment fragment = ReloadFragment.newInstance(drawable, tips);
//        fragment.setListener(getLoadListener());
//        getChildFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
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
//        View status = view.findViewById(R.id.status_layout);
//        if (status.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NoDataFragment fragment = NoDataFragment.newInstance(drawable, charSequence);
//        fragment.setListener(getLoadListener());
//        getChildFragmentManager().beginTransaction().replace(status.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showNoDataWithBtn(int drawable, String tips, String btnText, View.OnClickListener btnClickListener) {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "showNoDataWithBtn view is NULL");
            return;
        }
//        View view = getView().findViewById(R.id.status_layout);
//        if (view.getId() <= 0) {
//            MLog.error(this, "had not set layout id ");
//            return;
//        }
//        NoDataFragmentWithBtn fragment = NoDataFragmentWithBtn.newInstance(drawable, tips);
//        fragment.setBtnContent(btnText, btnClickListener);
//        getChildFragmentManager().beginTransaction().replace(view.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showNetworkErr() {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "xuwakao, showNetworkErr view is NULL");
            return;
        }
//        View view = getView().findViewById(R.id.status_layout);
//        if (view.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NetworkErrorFragment fragment = NetworkErrorFragment.newInstance();
//        fragment.setListener(getLoadListener());
//        getChildFragmentManager().beginTransaction().replace(view.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
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
//        View view = getView().findViewById(R.id.status_layout);
//        if (view.getId() <= 0) {
//            MLog.error(this, "xuwakao, had not set layout id ");
//            return;
//        }
//        NoMobileLiveFragment fragment = NoMobileLiveFragment.newInstance();
//        fragment.setListener(getNoMobileLiveDataListener());
//        getChildFragmentManager().beginTransaction().replace(view.getId(), fragment, STATUS_TAG).commitAllowingStateLoss();
    }

    @Override
    public void showPageError(int tips) {
        showPageError(getView(), tips);
    }

    @Override
    public void showPageError(View view, int tips) {
        if (!checkActivityValid())
            return;

        if (view == null) {
            MLog.error(this, "xuwakao, showReload view is NULL");
            return;
        }
//        View more = view.findViewById(R.id.loading_more);
//        if (more == null) {
//            MLog.error(this, "xuwakao, showReload more is NULL");
//            return;
//        }
//        StatusLayout statusLayout = (StatusLayout) more.getParent();
//        statusLayout.showErrorPage(tips, getLoadMoreListener());
    }

    @Override
    public void showPageLoading() {
        if (!checkActivityValid())
            return;

        if (getView() == null) {
            MLog.error(this, "xuwakao, showReload view is NULL");
            return;
        }
//        View more = getView().findViewById(R.id.loading_more);
//        if (more == null) {
//            MLog.error(this, "xuwakao, showReload more is NULL");
//            return;
//        }
//        StatusLayout statusLayout = (StatusLayout) more.getParent();
//        statusLayout.showLoadMore();
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
//        if (checkActivityValid() && !falg && getContext() != null)
//            Toast.makeText(getContext(), R.string.str_network_not_capable, Toast.LENGTH_SHORT).show();
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
