package com.redefine.welike.business.publisher.ui.contract;

import android.app.Activity;

import com.redefine.foundation.mvp.IBaseActivityPresenter;
import com.redefine.foundation.mvp.IBaseActivityView;
import com.redefine.welike.business.publisher.ui.presenter.ContactListPresenter;
import com.redefine.welike.business.publisher.ui.view.ContactListView;
import com.redefine.welike.business.user.management.bean.User;

import java.util.List;

/**
 * Created by gongguan on 2018/1/17.
 */

public interface IContactListContract {
    interface IContactListPresenter extends IBaseActivityPresenter {

        void loadMore();

        void onRefresh();

        void searchOnLine(String searchKey);
    }

    interface IContactListView extends IBaseActivityView {
        void setContactData(List<User> userAll, List<User> userRecent);

        void setOnContactChoiceListener(OnContactChoiceListener listener);

        void setPresenter(IContactListPresenter presenter);

        void showLoading();

        void onSearchOnLineResult(List<User> contents, int searchType, int errCode);

        void onLoadMoreResult(List<User> contents, int searchType, boolean last, int errCode);
    }

    class IContactListFactory {
        public static IContactListPresenter createPresenter(Activity activity) {
            return new ContactListPresenter(activity);
        }

        public static IContactListPresenter createPresenter(Activity activity, OnContactChoiceListener listener) {
            return new ContactListPresenter(activity, listener);
        }

        public static IContactListView creatView() {
            return new ContactListView();
        }
    }

    interface OnContactChoiceListener {
         void onUserChoice(User user);
    }

}
