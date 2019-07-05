package com.redefine.welike.business.feeds.ui.contract;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.layer.IStackLayerPresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.feeds.ui.presenter.SuperLike1Presenter;
import com.redefine.welike.business.feeds.ui.util.OnSuperLikeExpCallback;
import com.redefine.welike.business.feeds.ui.view.SuperLikeView;
import com.redefine.welike.commonui.framework.PageStackLayer;

/**
 * Created by liwenbo on 2018/4/2.
 */

public interface ISuperLikeContract {

    interface ISuperLikePresenter extends IStackLayerPresenter {

        boolean onTouch(View v, int[] targetPositions,MotionEvent event, OnSuperLikeExpCallback mCallback, long mExp);
    }

    interface ISuperLikeView extends IBaseView {

    }

    class SuperLikeFactory {
        public static ISuperLikePresenter createPresenter(PageStackLayer viewGroup) {
            return new SuperLike1Presenter(viewGroup);
        }

        public static ISuperLikeView createView(ViewGroup viewGroup) {
            return new SuperLikeView(viewGroup);
        }
    }
}
