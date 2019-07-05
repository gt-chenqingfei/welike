package com.redefine.welike.commonui.event.expose.model;

import android.view.View;

/**
 * Created by nianguowang on 2019/1/9
 */
public class ExposeModel {

    public int layoutPosition;
    public float showRatio;
    public View view;

    public ExposeModel(int layoutPosition, float showRatio, View view) {
        this.layoutPosition = layoutPosition;
        this.showRatio = showRatio;
        this.view = view;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExposeModel)) {
            return false;
        }
        ExposeModel model = (ExposeModel) obj;
        return model.view.equals(view);
    }
}
