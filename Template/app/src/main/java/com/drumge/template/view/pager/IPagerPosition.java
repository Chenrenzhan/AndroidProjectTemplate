package com.drumge.template.view.pager;

/**
 * Created by xujiexing on 14-7-15.
 */
public interface IPagerPosition {
    public void setPosition(int position);

    public void onSelected(int position);

    public void onUnSelected(int position);

    public void onPageScrollComplete(int position);

    public void onRestore();
}
