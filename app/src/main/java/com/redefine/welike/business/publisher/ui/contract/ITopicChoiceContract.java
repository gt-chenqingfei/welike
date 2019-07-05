package com.redefine.welike.business.publisher.ui.contract;

import android.app.Activity;

import com.redefine.foundation.mvp.IBaseActivityPresenter;
import com.redefine.foundation.mvp.IBaseActivityView;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchHistoryAdapter;
import com.redefine.welike.business.publisher.ui.adapter.TopicSearchSugAdapter;
import com.redefine.welike.business.publisher.ui.component.SearchBar;
import com.redefine.welike.business.publisher.ui.dialog.OnTopicChoiceListener;
import com.redefine.welike.business.publisher.ui.presenter.TopicChoicePresenter;
import com.redefine.welike.business.publisher.ui.view.SuperTopicChoiceView;
import com.redefine.welike.business.publisher.ui.view.TopicChoiceView;

/**
 * Created by liwenbo on 2018/4/10.
 */

public interface ITopicChoiceContract {

    interface ITopicChoicePresenter extends IBaseActivityPresenter {

        void onBackPressed();

        void onSearch(String text);

        void onClickImeOption();

        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    interface ITopicChoiceView extends IBaseActivityView {

        void setPresenter(ITopicChoicePresenter presenter);

        void showContentView();

        void showEmptyView();

        void setHistoryAdapter(TopicSearchHistoryAdapter mTopicHistory);

        void setSugAdapter(TopicSearchSugAdapter mTopicSugAdapter);

        void showErrorView();

        void onTextChanged(CharSequence s, int start, int before, int count);
    }

    class TopicChoiceFactory {
        public static ITopicChoicePresenter createPresenter(Activity activity, OnTopicChoiceListener listener) {
            return new TopicChoicePresenter(activity, listener);
        }

        public static ITopicChoicePresenter createPresenter(Activity activity, OnTopicChoiceListener listener, SearchBar searchBar) {
            return new TopicChoicePresenter(activity, listener, searchBar);
        }

        public static ITopicChoiceView createView(OnTopicChoiceListener listener) {
            return new TopicChoiceView(listener);
        }

        public static ITopicChoiceView createView(SearchBar searchBar,OnTopicChoiceListener listener) {
            return new SuperTopicChoiceView(searchBar, listener);
        }
    }
}
