package com.redefine.welike.business.feeds.ui.contract;

import android.view.View;

import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.view.CommonFeedTopicCardView;

/**
 * Created by mengnan on 2018/5/25.
 **/
public interface ICommonFeedTopicCardContract {

    interface ICommonFeedBottomTopicCardView {

        void bindViews(PostBase postBase);

        void setVisibility(boolean i);

    }

    class CommonFeedBottomFactory {
        public static CommonFeedTopicCardView createView(View view) {
            return new CommonFeedTopicCardView(view);
        }
    }

}
