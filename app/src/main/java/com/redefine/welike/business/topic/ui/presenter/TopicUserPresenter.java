package com.redefine.welike.business.topic.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.management.manager.TopicUsersManager;
import com.redefine.welike.business.topic.ui.adapter.TopicUserAdapter;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.topic.ui.contract.ITopicUserContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class TopicUserPresenter extends MvpTitlePagePresenter1<ITopicUserContract.ITopicUserView> implements ITopicUserContract.ITopicUserPresenter
        , OnClickRetryListener, TopicUsersManager.TopicUsersCallback {
    private TopicUserAdapter mAdapter;
    private TopicUsersManager mModel;
    private TopicSearchSugBean.TopicBean mTopic;

    public TopicUserPresenter(Activity stackManager, Bundle pageConfig) {
        super(stackManager, pageConfig);
    }

    @Override
    protected ITopicUserContract.ITopicUserView createPageView() {
        return ITopicUserContract.TopicUserFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        parseBundle(mPageBundle, savedInstanceState);
        mAdapter = new TopicUserAdapter();
        mView.setPresenter(this);
        mView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t instanceof TopicUser) {
                    UserHostPage.launch(true, ((TopicUser) t).user.getUid());
                }
            }
        });
        mAdapter.setRetryLoadMoreListener(this);
        mModel = new TopicUsersManager(mTopic.id);
        mModel.setListener(this);
        onRefresh();
    }

    private void parseBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        mTopic = (TopicSearchSugBean.TopicBean) mPageBundle.getSerializable(TopicConstant.BUNDLE_KEY_TOPIC);
        if (mTopic == null && savedInstanceState != null) {
            mTopic = (TopicSearchSugBean.TopicBean) savedInstanceState.getSerializable(TopicConstant.BUNDLE_KEY_TOPIC);
        }
    }

    @Override
    public void onRefresh() {
        mView.showLoading();
        mModel.tryRefreshUsers();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mAdapter.onLoadMore();
        // do load more data
        mModel.tryHisUsers();
    }

    @Override
    public void destroy() {
        super.destroy();
        mAdapter.destroy();
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onRefreshTopicUsers(List<TopicUser> users, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            if (!CollectionUtil.isEmpty(users)) {
                TopicUser topicUser=users.get(0);
                if(null!=topicUser&&topicUser.isEmcee){
                    topicUser.topicId=mTopic.id;
                    mView.setData(topicUser);
                    users.remove(0);
                }

                mAdapter.addHisData(users);

            }
            mAdapter.finishLoadMore();
            mAdapter.goneLoadMore();
        } else {
            mAdapter.loadError();
        }
        if (!CollectionUtil.isEmpty(users) || mAdapter.getRealItemCount() > 1) {
            mView.showContent();
        } else if (isSuccess) {
            mView.showEmptyView();
        } else {
            mView.showErrorView();
        }
    }

    @Override
    public void onReceiveHisTopicUsers(List<TopicUser> users, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (!CollectionUtil.isEmpty(users)) {
                mAdapter.addHisData(users);
            }
            if (last) {
                mAdapter.noMore();
            } else {
                mAdapter.finishLoadMore();
            }
        } else {
            mAdapter.loadError();
        }
    }

}
