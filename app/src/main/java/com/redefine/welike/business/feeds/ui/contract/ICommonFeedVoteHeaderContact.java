package com.redefine.welike.business.feeds.ui.contract;

import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by mengnan on 2018/6/20.
 **/
public interface ICommonFeedVoteHeaderContact {

    interface ICommonFeedVoteHeader  {

        void bindViews(PostBase postBase);

        void setVisibility(boolean i);

    }
    class CommonFeedBottomFactory {
        public static  void createView(IPageStackManager pageStackManager, View view) {
        }
    }
}
