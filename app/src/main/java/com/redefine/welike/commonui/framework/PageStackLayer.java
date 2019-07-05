package com.redefine.welike.commonui.framework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.pekingese.pagestack.framework.layer.BasePageStackLayer;
import com.redefine.welike.business.feeds.ui.contract.ISuperLikeContract;

/**
 * Created by liwenbo on 2018/3/23.
 */

public class PageStackLayer extends BasePageStackLayer {

    public PageStackLayer(@NonNull Context context) {
        super(context);
    }


    public PageStackLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageStackLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void registerPresenters() {
        super.registerPresenters();
        mPresenters.put(ISuperLikeContract.ISuperLikePresenter.class, ISuperLikeContract.SuperLikeFactory.createPresenter(this));
//        mPresenters.put(ILikeAnimationContract.ILikeAnimationPresenter.class, ILikeAnimationContract.LikeAnimationFactory.createPresenter(this));
    }
}
