package com.redefine.welike.commonui.view;

import android.content.Context;

/**
 * Created by mengnan on 2018/5/12.
 **/
public class MasterTextVoteItemView extends TextVoteItemView {
    public MasterTextVoteItemView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setMode(TextVoteItemView.MASTER_MODE);

    }
}
