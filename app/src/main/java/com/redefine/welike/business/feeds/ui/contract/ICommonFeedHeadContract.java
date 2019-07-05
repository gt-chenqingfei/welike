package com.redefine.welike.business.feeds.ui.contract;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.presenter.CommonFeedHeadPresenter;
import com.redefine.welike.business.feeds.ui.view.CommonFeedHeadView;

/**
 * Created by liwb on 2018/1/10.
 */

public interface ICommonFeedHeadContract {

    interface ICommonFeedHeadPresenter extends IBasePresenter {

        void bindViews(PostBase postBase, RecyclerView.Adapter adapter);

        void performClickDeleteBtn();

        void dismissFollowBtn(boolean b);

        void onMenuBtnClick(PostBase postBase);

        void dismissArrowBtn(boolean b);

        void showReadCount(boolean b);

        void setIBrowseListener(IBrowseClickListener iBrowseClickListener);

        CommonFeedHeadView getView();
    }

    interface ICommonFeedHeadView extends IBaseView {

        void bindViews(PostBase postBase, RecyclerView.Adapter adapter);

        void setPresenter(ICommonFeedHeadPresenter commonFeedHeadPresenter);

        void performClickDeleteBtn();

        void dismissFollowBtn(boolean b);

        void dismissArrowBtn(boolean b);

        void showTopFlag(boolean b);

        void showHotFlag(boolean b);

        void setReadCount(String readCount);

        void showReadCount(boolean b);

        void showTopPostFlag(boolean b);

    }

    class CommonFeedHeadFactory {
        public static ICommonFeedHeadPresenter createPresenter(View view, IFeedOperationListener listener) {
            return new CommonFeedHeadPresenter(view, listener);
        }

        public static CommonFeedHeadView createView(View view) {
            return new CommonFeedHeadView(view);
        }
    }
}
