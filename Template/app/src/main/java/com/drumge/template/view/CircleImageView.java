package com.drumge.template.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/6/17.
 */

public class CircleImageView extends ImageView {

    public CircleImageView(Context context){
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int state;
    private Drawable tail;
    public void setTail(int id){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),id);
        tail=new BitmapDrawable( getResources(),bitmap);
        tail.setBounds(0,0,tail.getIntrinsicWidth(),tail.getIntrinsicHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        Drawable drawable=getDrawable();
        for(int i=0;i<state;i++){
            if(drawable==null)
                return;
            super.onDraw(canvas);
            canvas.rotate(-30,(getRight()-getLeft())/2,(getBottom()-getTop())/2);
        }
        if(tail!=null)
            tail.draw(canvas);
        canvas.restore();
    }

}
