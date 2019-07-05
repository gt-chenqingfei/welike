package com.redefine.welike.business.feeds.ui.contract;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.view.CommonFeedBottomView;

/**
 * Created by liwb on 2018/1/11.
 */

public interface ICommonFeedBottomContract {

    interface ICommonFeedBottomView extends IBasePresenter {

        void bindViews(PostBase postBase, RecyclerView.Adapter adapter);

        void setVisibility(int i);

        void setDismissDivider(boolean b);

        void setDismissBottomContent(boolean b);
    }

    class CommonFeedBottomFactory {
        public static CommonFeedBottomView createView( View view) {
            return new CommonFeedBottomView( view);
        }
    }
}
