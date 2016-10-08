package com.drumge.template.view.pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.drumge.template.log.MLog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xujiexing on 14-7-15.
 */
public class SelectedViewPager extends ViewPager {
    private PageChangeListenerWrapper mWrapper;

    public SelectedViewPager(Context context) {
        super(context);
    }

    public SelectedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(null);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mWrapper = new PageChangeListenerWrapper(this, listener);
        super.setOnPageChangeListener(mWrapper.getWrapperPageListener());
        if(getAdapter() != null && getAdapter() instanceof PagerSelectedAdapter){
            ((PagerSelectedAdapter) getAdapter()).setSelectedInitialize(true);
        }else{
            mWrapper.getWrapperPageListener().onPageSelected(getCurrentItem());
            mWrapper.getWrapperPageListener().onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Throwable ex) {
            MLog.error(this, "xuwakao, onTouchEvent SelectedViewPager viewpager error happens, ev = " + ev);
        }
        return false;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (mWrapper != null) {
            mWrapper.getWrapperPageListener().onPageSelected(0);
            mWrapper.getWrapperPageListener().onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        }else if(getAdapter() != null && getAdapter() instanceof PagerSelectedAdapter){
            ((PagerSelectedAdapter) getAdapter()).setSelectedInitialize(true);
        }
    }

    public void setFirstOnPageSelected() {
        if (mWrapper != null) {
            mWrapper.getWrapperPageListener().onPageSelected(0);
        }
    }

    private static class PageChangeListenerWrapper {
        private final OnPageChangeListener mPageListener;
        private final WeakReference<SelectedViewPager> mPager;

        public PageChangeListenerWrapper(SelectedViewPager viewPager, OnPageChangeListener listener) {
            this.mPager = new WeakReference<SelectedViewPager>(viewPager);
            this.mPageListener = listener;
        }

        public OnPageChangeListener getWrapperPageListener() {
            return this.mWrapperPageListener;
        }

        private OnPageChangeListener mWrapperPageListener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPageListener != null) {
                    mPageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mPageListener != null) {
                    mPageListener.onPageSelected(position);
                }

                final SelectedViewPager pager = mPager.get();
                if (pager != null && pager.getAdapter() != null && pager.getAdapter() instanceof PagerSelectedAdapter) {
                    final PagerSelectedAdapter adapter = (PagerSelectedAdapter) pager.getAdapter();
                    IPagerFragment fragment = adapter.getPosFragment(position);
                    if (fragment != null) {
                        fragment.onSelected(position);
                    }
                    List<IPagerFragment> fragmentList = adapter.excludePosFragment(position);
                    if (fragmentList != null) {
                        for (IPagerFragment item : fragmentList) {
                            if (item != null)
                                item.onUnSelected(adapter.indexOfFragment(item));
                        }
                    }
                } else {
                    MLog.warn(this, "xuwakao, pager = " + pager);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    final SelectedViewPager pager = mPager.get();
                    if (pager != null && pager.getAdapter() != null && pager.getAdapter() instanceof PagerSelectedAdapter) {
                        final PagerSelectedAdapter adapter = (PagerSelectedAdapter) pager.getAdapter();
                        final int position = pager.getCurrentItem();
                        IPagerFragment fragment = adapter.getPosFragment(position);
                        if (fragment != null)
                            fragment.onPageScrollComplete(position);
                    } else {
                        MLog.warn(this, "xuwakao, pager = " + pager);
                    }
                }
                if (mPageListener != null) {
                    mPageListener.onPageScrollStateChanged(state);
                }
            }
        };
    }
}
