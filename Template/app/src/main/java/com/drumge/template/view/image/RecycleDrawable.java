package com.drumge.template.view.image;

/**
 * 接口：可被自动回收的drawable
 * @author zhongyongsheng on 2015/5/6.
 */
public interface RecycleDrawable {

    public void setIsDisplayed(boolean isDisplayed);

    public void setIsCached(boolean isCached);
}
