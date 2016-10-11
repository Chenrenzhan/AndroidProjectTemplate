package com.drumge.template;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.drumge.template.imageloader.ImageLoaderConfigs;
import com.drumge.template.status.RefreshLayout;
import com.drumge.template.status.StateLayout;

public class MainActivity extends Activity {
    String TAG = "chenrenzhan";

    RefreshLayout mStatusView;
    RelativeLayout mLayout;
    ViewGroup viewGroup;

    StateLayout stateLayout;
    
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ViewGroup viewGroup = createViewGroup();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mStatusView.addView(viewGroup, 1, params);
            Log.e("eeeeeeee", "handle msg : " + msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatusView = (RefreshLayout)findViewById(R.id.status);
        mLayout = (RelativeLayout)findViewById(R.id.layout);

        stateLayout = (StateLayout)findViewById(R.id.state);
        stateLayout.setStateLayoutListener(new StateLayout.StateLayoutListener() {
            @Override
            public void onClickLayout(StateLayout.STATE state) {
                Log.i(TAG, state.toString());
            }
        });
        stateLayout.setVisibility(View.GONE);
        stateLayout.setNoDataView(getStateView("没有数据"));
        stateLayout.setNoNetView(getStateView("没有网络"));
        stateLayout.setRequestFailedView(getStateView("请求失败"));
        stateLayout.setLoadingView(getStateView("正在加载...."));




        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout.showNoDataView();
                Log.i(TAG, "数据");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout.showNoNetView();
                Log.i(TAG, "网络");
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout.showRequestFailedView();
                Log.i(TAG, "请求");
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout.showLoadingView();
                Log.i(TAG, "加载");
            }
        });
        
        FrameLayout frame = (FrameLayout)findViewById(R.id.frame_layout);
//        TextView t1 = new TextView(this);
//        t1.setText("frame        1");
//        TextView t2 = new TextView(this);
//        t2.setText("frame             2");
//        TextView t3 = new TextView(this);
//        t3.setText("frame                 3");
//        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER;
//        param.addRule(RelativeLayout.CENTER_IN_PARENT);
//        t1.setLayoutParams(p);
//        t2.setLayoutParams(p);
//        t3.setLayoutParams(p);
//        frame.addView(nodata, p);
//        frame.addView(t2);
//        frame.addView(t3);
//        t3.setVisibility(View.GONE);
        
        TextView tv = new TextView(this);
        tv.setText("test");

        viewGroup = createViewGroup();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mStatusView.addView(viewGroup, 1, params);
        
        if (mStatusView != null) {
            // 刷新状态的回调
            mStatusView.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 延迟3秒后刷新成功
                    mStatusView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
                            mStatusView.refreshComplete();
                            mHandler.sendEmptyMessage(0);
//                            viewGroup.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            });
        }
    }
    
    private View getStateView(String state){
        TextView nodata = new TextView(this);
        nodata.setText(state);
        nodata.setCompoundDrawables(null, ContextCompat.getDrawable(this, R.mipmap.ic_launcher), null, null);
        nodata.setTextColor(Color.BLUE);
        return nodata;
    }
    
    private ViewGroup createViewGroup(){
        ViewGroup viewGroup = new FrameLayout(this);
        viewGroup.setBackgroundColor(Color.BLACK);
        TextView textView = new TextView(this);
        textView.setText("no data!");
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewGroup.addView(textView, params);
        return viewGroup;
    }
}
