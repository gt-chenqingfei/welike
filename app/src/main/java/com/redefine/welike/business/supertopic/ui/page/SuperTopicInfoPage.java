package com.redefine.welike.business.supertopic.ui.page;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.supertopic.ui.adapter.SuperTopicTopRecylerViewAdapter;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.frameworkmvvm.BaseLifecycleFragmentPage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.List;

public class SuperTopicInfoPage extends BaseLifecycleFragmentPage implements IBrowseClickListener {
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private View emptyInfoView;
    private TextView emptyInfoTextView;
    private SuperTopicTopRecylerViewAdapter adapter;
    private String info;
    private List<PostBase> topPosts;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private VideoItemVisibilityCalculator videoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                videoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).playVideo(position, videoPlayerView);
            }
        }
    });

    public SuperTopicInfoPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return mPageStackManager.getLayoutInflater().inflate(R.layout.super_topic_info_fragment_layout, null);
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        super.initView(container, saveState);

        boolean isBrowse = !AccountManager.getInstance().isLogin();

        emptyInfoView = getView().findViewById(R.id.empty_info_layout);
        emptyInfoView.setVisibility(View.GONE);
        emptyInfoTextView = getView().findViewById(R.id.super_topic_page_info);
        recyclerView = getView().findViewById(R.id.recycler_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayout.VERTICAL, false));
        emptyView = getView().findViewById(R.id.common_empty_view);
        emptyView.showEmptyImageText(R.drawable.ic_common_empty, getView().getResources().getText(R.string.super_topic_empty).toString());

        if (adapter == null) {
            adapter = new SuperTopicTopRecylerViewAdapter(mPageStackManager);
            adapter.setFirstTopMargin(0);
        } else {
            if (!TextUtils.isEmpty(info) && (topPosts != null && topPosts.size() > 0)) {
                recyclerView.setAdapter(adapter);
                adapter.setData(info, topPosts);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
        if (isBrowse) {
            adapter.setBrowseClickListener(this);
        } else {
            adapter.setBrowseClickListener(null);
        }
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(View v) {
                videoItemVisibilityCalculator.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                    ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                    videoItemVisibilityCalculator.resetCurrentPlayItem();
                }
                mPostViewTimeManager.onDetach();
            }

        });
        mPostViewTimeManager.attach(recyclerView, adapter, EventConstants.FEED_SOURCE_SUPER_TOPIC_HOT);
    }

    public void showTopPosts(String info, List<PostBase> topPosts) {
        this.info = info;
        this.topPosts = topPosts;
        if (topPosts != null && topPosts.size() > 0) {
            recyclerView.setAdapter(adapter);
            adapter.setData(info, topPosts);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(info)) {
                emptyInfoView.setVisibility(View.VISIBLE);
                emptyInfoTextView.setText(info);
            } else {
                emptyInfoView.setVisibility(View.GONE);
            }
        }
        mPostViewTimeManager.setData(topPosts, false);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_SUPER_TOPIC_INFO);
        PostEventManager.INSTANCE.setType(adapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(topPosts);
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        if (adapter != null) {
            adapter.destroyVideo();
            videoItemVisibilityCalculator.resetCurrentPlayItem();
        }
        if (mPostViewTimeManager != null) {
            mPostViewTimeManager.onDestroy();
        }
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if (adapter != null) {
            adapter.onResume();
        }
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        if (adapter != null) {
            adapter.destroyVideo();
            videoItemVisibilityCalculator.resetCurrentPlayItem();
        }
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onBasePageStateChanged(int oldPageState, int pageState) {
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            if (adapter != null) {
                adapter.onResume();
            }
        } else {
            if (adapter != null) {
                adapter.destroyVideo();
                videoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            mPostViewTimeManager.onResume();
            mPostViewTimeManager.onShow();
        } else {
            mPostViewTimeManager.onPause();
            mPostViewTimeManager.onHide();
        }
    }

    @Override
    public void onBrowseClick(int tye, boolean isShowLogin, int showType) {


        if (isShowLogin) {
            HalfLoginManager.getInstancce().showLoginDialog(mPageStackManager.getContext(), new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(tye)));
//            RegisterActivity.Companion.show(mPageStackManager.getContext(),0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
        }

    }
}
