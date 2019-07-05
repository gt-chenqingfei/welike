package com.redefine.welike.business.topic.ui.vm;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.management.SinglePostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.topic.management.provider.TopicHotPostsProvider;
import com.redefine.welike.business.topic.management.provider.TopicPostsProvider;
import com.redefine.welike.business.topic.ui.page.TopicLandingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/7/9.
 * <p>
 * viewmodel
 */
public class TopicSubViewModel extends AndroidViewModel implements SinglePostsManager.PostsCallback {


    private SinglePostsManager mPostModel;
    private SinglePostsManager mHotPostModel;


    private MutableLiveData<List<PostBase>> mRefreshPosts = new MutableLiveData<>();
    private MutableLiveData<List<PostBase>> mMorePosts = new MutableLiveData<>();

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();

    private MutableLiveData<Integer> mPageRefreshCount = new MutableLiveData<>();

    public TopicSubViewModel(@NonNull Application application) {
        super(application);

    }


    public void init(String topicId, boolean isBrowse) {

        mPostModel = new SinglePostsManager();
        mHotPostModel = new SinglePostsManager();
        mPostModel.setDataSourceProvider(new TopicPostsProvider(topicId, isBrowse));
        mHotPostModel.setDataSourceProvider(new TopicHotPostsProvider(topicId, isBrowse));
        mPostModel.setListener(this);
        mHotPostModel.setListener(this);


    }

    public void refresh(int tabId) {

        mPageStatus.postValue(PageStatusEnum.LOADING);

        if (tabId == TopicLandingActivity.TAB_HOT) {
            mHotPostModel.tryRefreshPosts();
        } else {
            mPostModel.tryRefreshPosts();
        }

    }


    public void loadMoreHotData(int tabId) {
        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOADING);
        if (tabId == TopicLandingActivity.TAB_HOT) {
            mHotPostModel.tryHisPosts();
        } else {
            mPostModel.tryHisPosts();
        }

    }


    public MutableLiveData<List<PostBase>> getHotPosts() {
        return mRefreshPosts;
    }

    public MutableLiveData<List<PostBase>> getMorePosts() {
        return mMorePosts;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<PageLoadMoreStatusEnum> getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    @Override
    public void onRefreshPosts(SinglePostsManager manager, List<PostBase> posts, int newCount, boolean last, int errCode) {

        if (errCode == ErrorCode.ERROR_SUCCESS) {

            if (!CollectionUtil.isEmpty(posts)) {
                mPageRefreshCount.postValue(newCount);
                mRefreshPosts.postValue(posts);
                mPageStatus.postValue(PageStatusEnum.CONTENT);
            } else {
                mPageStatus.postValue(PageStatusEnum.EMPTY);
                mPageRefreshCount.postValue(0);
                mRefreshPosts.postValue(new ArrayList<PostBase>());


            }
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

        if (last) {
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        }else{
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
        }

    }

    @Override
    public void onReceiveHisPosts(SinglePostsManager manager, List<PostBase> posts, boolean last, int errCode) {


        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mMorePosts.postValue(posts);
            mPageStatus.postValue(PageStatusEnum.CONTENT);
        } else {
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
        }

        if (last) {
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        } else {
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
        }
    }
}
