package com.drumge.template.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by xujiexing on 14-6-5.
 */
public class BaseFragment extends BaseLinkFragment {


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            Fragment fragment = getChildFragmentManager().findFragmentByTag(STATUS_TAG);
        }
    }

    public void showNoMobileLivePersonalReplayData(long userId,boolean showNoRecordTips) {
        if (!checkActivityValid()) {
            return;
        }

    }
}
