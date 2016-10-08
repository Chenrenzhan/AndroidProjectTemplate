package com.drumge.template.view;

import android.view.View;

/**
 * Created by xujiexing on 14-7-15.
 */
public interface IDataStatus {

    public View.OnClickListener getLoadListener();

    public View.OnClickListener getLoadMoreListener();

    public View.OnClickListener getNoMobileLiveDataListener();

    public void showLoading();

    public void showLoading(View view);

    public void showReload();

    public void showNoData();


    public void showLoading(int drawable, int tips);

    public void showLoading(View view, int drawable, int tips);

    public void showReload(int drawable, int tips);

    public void showReload(View view, int drawable, int tips);

    public void showNoData(int drawable, CharSequence charSequence);

    public void showNoData(int drawable, int tips);

    public void showNoData(View view, int drawable, CharSequence charSequence);

    public void showNoData(View view, int drawable, int tips);

    public void showNoDataWithBtn(int drawable, String tips, String btnText, View.OnClickListener btnClickListener);

    public void showNetworkErr();

    public void hideStatus();

    public void showPageError(int tips);

    public void showPageError(View view, int tips);

    public void showPageLoading();

    public void showNoMobileLiveData();

}
