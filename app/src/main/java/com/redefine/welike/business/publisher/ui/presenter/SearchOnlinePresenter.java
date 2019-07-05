package com.redefine.welike.business.publisher.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.management.SearchManager;
import com.redefine.welike.business.publisher.ui.contract.IContactListContract;
import com.redefine.welike.business.publisher.ui.contract.ISearchOnlineContract;
import com.redefine.welike.business.search.ui.adapter.SearchUserAdapter;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/2/27.
 */

public class SearchOnlinePresenter implements ISearchOnlineContract.ISearchOnlinePresenter
        , SearchManager.SearchManagerListener, OnClickRetryListener {
    private ISearchOnlineContract.ISearchOnlineView onlineView;

    private static final boolean NEED_AUTH = true;
    private final SearchManager mModel;
    private final SearchUserAdapter mAdapter;
    private boolean isFirstCreate = true;
    private String searchStr;
    private IContactListContract.OnContactChoiceListener mListener;


    public SearchOnlinePresenter(IContactListContract.OnContactChoiceListener listener) {
        mListener = listener;
        onlineView = ISearchOnlineContract.ISearchOnlineFactory.createView();
        onlineView.setPresenter(this);
        mAdapter = new SearchUserAdapter();
        mAdapter.setRetryLoadMoreListener(this);

        mModel = new SearchManager();
        mModel.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_USERS);
    }

    @Override
    public void onCreate(final View rootView, Bundle savedInstanceState) {
        onlineView.onCreate(rootView, savedInstanceState);
        onlineView.setAdapter(mAdapter);
        mModel.register(this);

        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t instanceof User) {
                    mListener.onUserChoice((User) t);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        mModel.unregister(this);
        onlineView.onDestroy();
    }

    @Override
    public void onRefresh() {
        mModel.search(searchStr, NEED_AUTH);
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void loadMore() {
        mAdapter.onLoadMore();
        mModel.loadMore(NEED_AUTH);
    }

    @Override
    public void doSearch(String key) {
        searchStr = key;
        if (isFirstCreate) {
            isFirstCreate = false;
            onlineView.showLoading();
        }
        onRefresh();
    }

    @Override
    public void onNewSearchResult(List<Object> contents, int searchType, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addNewData(fileType(contents));
            mAdapter.clearFinishFlag();
            int size = CollectionUtil.getCount(contents);
            if (size == 0 && mAdapter.getRealItemCount() == 0) {
                onlineView.showEmptyView();
            } else {
                onlineView.showContent();
            }
        } else {
            if (mAdapter.getRealItemCount() == 0) {
                onlineView.showErrorView();
            } else {
                onlineView.showContent();
            }
        }
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

    @Override
    public void onMoreSearchResult(List<Object> contents, int searchType, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addHisData(fileType(contents));
            if (last) {
                mAdapter.noMore();
            } else {
                mAdapter.finishLoadMore();
            }
        } else {
            mAdapter.loadError();
        }
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            loadMore();
        }
    }
}
