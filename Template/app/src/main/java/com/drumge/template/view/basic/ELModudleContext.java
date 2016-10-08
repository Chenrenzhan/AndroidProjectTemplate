package com.drumge.template.view.basic;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;


/**
 * Created by qiushunming on 16/1/11.
 */
public class ELModudleContext {
    private LiveComponent component;

    private Bundle saveInstance;

    private SparseArrayCompat<ViewGroup> viewGroups = new SparseArrayCompat<>();

    public LiveComponent getComponent() {
        return component;
    }

    public void setComponent(LiveComponent component) {
        this.component = component;
    }

    public Bundle getSaveInstance() {
        return saveInstance;
    }

    public void setSaveInstance(Bundle saveInstance) {
        this.saveInstance = saveInstance;
    }

    public ViewGroup getView(int key){
        return viewGroups.get(key);
    }


    public SparseArrayCompat<ViewGroup> getViewGroups() {
        return viewGroups;
    }

    public void setViewGroups(SparseArrayCompat<ViewGroup> viewGroups) {
        this.viewGroups = viewGroups;
    }
}
