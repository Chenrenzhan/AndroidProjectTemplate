package com.drumge.template.view.component;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/**
 * Created by xianjiachao on 2015/10/28.
 */
public interface IPopupComponent extends IComponent {

    public void show(Bundle data);

    public void hide();

    public boolean isShowing();

    public void setParentFragmentManager(FragmentManager fm);

}
