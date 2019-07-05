package com.redefine.welike.business.search.ui.presenter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpFragmentPagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.management.SearchManager;
import com.redefine.welike.business.search.ui.adapter.SearchUserAdapter;
import com.redefine.welike.business.search.ui.contract.ISearchUserContract;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchUserPresenter extends MvpFragmentPagePresenter<ISearchUserContract.ISearchUserView> implements
        ISearchUserContract.ISearchUserPresenter, SearchManager.SearchManagerListener,
        OnClickRetryListener {

    private final SearchManager mModel;
    private final SearchUserAdapter mAdapter;
    private String mSearchQuery;
    private boolean mAuth;

    public SearchUserPresenter(IPageStackManager pageStackManager, Bundle pageConfig) {
        super(pageStackManager, pageConfig);
        mView.setPresenter(this);
        mAdapter = new SearchUserAdapter();
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t instanceof User) {
                    User user = (User) t;
                    UserHostPage.launch(true, user.getUid());

                    int i = mAdapter.indexOfUser(user);
                    if (i >= 0) {
                        EventLog1.Search.report3(mSearchQuery, user.getUid(), null, null, null, user.getSequenceId(), i);
                    }
                }
            }
        });
        mModel = new SearchManager();
        mModel.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_USERS);
    }

    @Override
    protected ISearchUserContract.ISearchUserView createFragmentPageView() {
        return ISearchUserContract.SearchUserFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mView.setAdapter(mAdapter);
        mModel.register(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mModel.unregister(this);
    }

    @Override
    public void detach() {
        super.detach();
        mAdapter.addNewData(null);
    }

    @Override
    public void onRefresh(String searchText) {
        mSearchQuery = searchText;
        mModel.search(searchText, mAuth);
        mView.showLoading();
    }

    @Override
    public void retryRefresh() {
        onRefresh(mSearchQuery);
    }


    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mAdapter.onLoadMore();
        mModel.loadMore(mAuth);
    }

    @Override
    public void onNewSearchResult(List<Object> contents, int searchType, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addNewData(fileType(contents));
            mAdapter.clearFinishFlag();
            if (CollectionUtil.isEmpty(contents)) {
                mView.showEmptyView();
            } else {
                mView.showContentView();
            }
        } else {
            mView.showErrorView();
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
            onLoadMore();
        }
    }
}
