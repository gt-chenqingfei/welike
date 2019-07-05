package com.redefine.welike.business.publisher.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.feeds.management.SearchManager;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.ui.contract.IContactListContract;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/17.
 */

public class ContactListPresenter implements IContactListContract.IContactListPresenter
        , SearchManager.SearchManagerListener {
    private static final boolean NEED_AUTH = true;
    private IContactListContract.IContactListView mView;
    private boolean isFirstCreate = true;

    private final SearchManager mModel;


    public ContactListPresenter(Activity activity) {
        mView = IContactListContract.IContactListFactory.creatView();
        mView.setPresenter(this);
        mModel = new SearchManager();
        mModel.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_USERS);
    }

    public ContactListPresenter(Activity activity, IContactListContract.OnContactChoiceListener listener) {
        mView = IContactListContract.IContactListFactory.creatView();
        mView.setOnContactChoiceListener(listener);
        mView.setPresenter(this);
        mModel = new SearchManager();
        mModel.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_USERS);
        mModel.register(this);
    }

    @Override
    public void onCreate(View rootView, Bundle savedInstanceState) {
        mView.onCreate(rootView, savedInstanceState);
        onRefresh();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void loadMore() {
        mModel.loadMore(NEED_AUTH);
    }

    @Override
    public void onRefresh() {
        List<User> userAll = ContactsManager.getInstance().listAllContacts();
        List<User> userRecent = ContactsManager.getInstance().listRecentContacts();
        mView.setContactData(userAll, userRecent);
    }

    @Override
    public void searchOnLine(String searchKey) {
        if (TextUtils.isEmpty(searchKey)) {
            return;
        }

        if (isFirstCreate) {
            isFirstCreate = false;
            mView.showLoading();
        }
        mModel.search(searchKey, NEED_AUTH);
        PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report8();
    }

    @Override
    public void onNewSearchResult(List<Object> contents, int searchType, int errCode) {

        mView.onSearchOnLineResult(fileType(contents), searchType, errCode);

    }

    @Override
    public void onMoreSearchResult(List<Object> contents, int searchType, boolean last, int errCode) {
        mView.onLoadMoreResult(fileType(contents), searchType, last, errCode);
    }

    private List<User> fileType(List<Object> contents) {
        List<User> users = new ArrayList<>();
        if (CollectionUtil.isEmpty(contents)) {
            return users;
        }
        for (Object o : contents) {
            if (o instanceof User) {
                users.add((User) o);
            }
        }
        return users;
    }
}
