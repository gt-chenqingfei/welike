package com.redefine.welike.commonui.view;

import android.content.Context;

/**
 * Created by mengnan on 2018/5/12.
 **/
public class GuestTextVoteItemView extends TextVoteItemView{
    public GuestTextVoteItemView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setMode(TextVoteItemView.GUEST_MODE);
        setOnTouchListener(onTouchListener);
    }


}
