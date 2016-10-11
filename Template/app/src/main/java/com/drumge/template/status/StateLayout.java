package com.drumge.template.status;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.drumge.template.R;

/**
 * 状态frame,定义了包括没网络，没数据，网络请求失败，以及正在加载中多种状态
 * 可使用setSateView来设置状态的View
 * 通过showSateView来显示对应的状态View
 */
public class StateLayout extends FrameLayout implements View.OnClickListener {
    
    private View mNoDataView;
    private View mNoNetView;
    private View mLoadingView;
    private View mRequestFailedView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private STATE mState = STATE.CLICK_NONE;

    private StateLayoutListener mStateLayoutListener;

    public enum STATE {
        CLICK_LOADING, CLICK_NONE, CLICK_NO_NET, CLICK_REQUEST_FAILED, CLICK_NO_DATA, STATE_CHANGE
    }

    public interface StateLayoutListener {
        void onClickLayout(STATE state);
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
//        test(context);
    }

    public StateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
//        test(context);
    }

    public StateLayout(Context context) {
        super(context);
        init();
//        test(context);
    }
    
    private void init(){
        this.setOnClickListener(this); // 吃掉frameLayout的TouchEvent
    }
    
    private void test(Context context){
        TextView nodata = new TextView(context);
        nodata.setText("没有数据");
        nodata.setCompoundDrawables(null, ContextCompat.getDrawable(context, R.mipmap.ic_launcher), null, null);
        nodata.setTextColor(Color.BLUE);
        this.setNoDataView(nodata);
        nodata.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
//        showNoDataView();
    }

    public void hideAllView() {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mState = STATE.CLICK_NONE;
                if (mStateLayoutListener != null) {
                    mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
                }
                if(mNoNetView != null){
                    mNoNetView.setVisibility(View.GONE);
                }
                if(mNoDataView != null ){
                    mNoDataView.setVisibility(View.GONE);
                }
                if(mRequestFailedView != null){
                    mRequestFailedView.setVisibility(View.GONE);
                }
                if(mLoadingView != null){
                    mLoadingView.setVisibility(View.GONE);
                }
                StateLayout.this.setVisibility(View.GONE);
            }
        });
    }

    public void showLoadingView() {
        if (mHandler == null || mLoadingView == null) {
            return;
        }
        hideAllView();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mState = STATE.CLICK_LOADING;
                if (mStateLayoutListener != null) {
                    mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
                }
                mLoadingView.setVisibility(View.VISIBLE);
                StateLayout.this.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showNoNetView() {
        if (mHandler == null || mNoNetView == null) {
            return;
        }
        hideAllView();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mState = STATE.CLICK_NO_NET;
                if (mStateLayoutListener != null) {
                    mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
                }
                mNoNetView.setVisibility(View.VISIBLE);
                StateLayout.this.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showNoDataView() {
        if (mHandler == null || mNoDataView == null) {
            return;
        }
        hideAllView();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mState = STATE.CLICK_NO_DATA;
                if (mStateLayoutListener != null) {
                    mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
                }
                mNoDataView.setVisibility(View.VISIBLE);
                StateLayout.this.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showRequestFailedView() {
        if (mHandler == null || mRequestFailedView == null) {
            return;
        }
        hideAllView();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mState = STATE.CLICK_REQUEST_FAILED;
                if (mStateLayoutListener != null) {
                    mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
                }
                mRequestFailedView.setVisibility(View.VISIBLE);
                StateLayout.this.setVisibility(View.VISIBLE);
            }
        });
    }

    public StateLayout setStateLayoutListener(StateLayoutListener stateLayoutListener) {
        this.mStateLayoutListener = stateLayoutListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        STATE state = STATE.CLICK_NONE;
        if (v == mNoDataView) {
            state = STATE.CLICK_NO_DATA;
        } else if (v == mNoNetView) {
            state = STATE.CLICK_NO_NET;
        } else if (v == mRequestFailedView) {
            state = STATE.CLICK_REQUEST_FAILED;
        }
        if (mStateLayoutListener != null) {
            mStateLayoutListener.onClickLayout(state);
        }
    }

    public void invokeStateListener() {
        if (mStateLayoutListener != null)
            mStateLayoutListener.onClickLayout(STATE.STATE_CHANGE);
    }

    public STATE getState() {
        return mState;
    }

    
    
    private void addStateView(View view){
        if(view == null){
            return;
        }
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.addView(view, params);
        view.setVisibility(View.GONE);
        view.setOnClickListener(this);
    }

    public void setNoDataView(View noDataView) {
        if(noDataView == null || !(noDataView instanceof View)){
            return;
        }
        this.mNoDataView = noDataView;
        addStateView(mNoDataView);
    }

    public void setNoNetView(View noNetView) {
        if(noNetView == null || !(noNetView instanceof View)){
            return;
        }
        this.mNoNetView = noNetView;
        addStateView(mNoNetView);
    }

    public void setLoadingView(View loadingView) {
        if(loadingView == null || !(loadingView instanceof View)){
            return;
        }
        this.mLoadingView = loadingView;
        addStateView(mLoadingView);
    }

    public void setRequestFailedView(View requestFailedView) {
        if(requestFailedView == null || !(requestFailedView instanceof View)){
            return;
        }
        this.mRequestFailedView = requestFailedView;
        addStateView(mRequestFailedView);
    }
}
