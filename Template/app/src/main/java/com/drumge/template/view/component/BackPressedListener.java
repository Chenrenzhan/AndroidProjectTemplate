package com.drumge.template.view.component;

/**
 * Created by xianjiachao on 2015/7/21.
 */
public interface BackPressedListener {
    public boolean onBackPressed();

    /**
     * 告诉下次是否还需要监听back事件
     * @return
     */
    public boolean persist();
}
