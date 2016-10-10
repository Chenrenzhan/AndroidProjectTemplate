package com.drumge.template;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.drumge.template.status.RefreshLayout;

public class MainActivity extends Activity {

    RefreshLayout mStatusView;
    RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatusView = (RefreshLayout)findViewById(R.id.status);
        mLayout = (RelativeLayout)findViewById(R.id.layout); 
        
        TextView tv = new TextView(this);
        tv.setText("test");

        ViewGroup viewGroup = createViewGroup();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mStatusView.addView(tv, -1);
        
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
                            ViewGroup viewGroup = createViewGroup();
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            mStatusView.addView(viewGroup, -1, params);
                            
                        }
                    }, 3000);
                }
            });
        }
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
