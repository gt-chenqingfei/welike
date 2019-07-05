package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.feeds.ui.adapter.FeedItemFollowAdapter;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.business.feeds.ui.listener.DefaultFollowClickListener;
import com.redefine.welike.business.user.management.bean.FollowPost;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.ui.activity.RecommendFollowActivity;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;

/**
 * Created by honglin on 2018/7/20.
 */

public class FeedFollowViewHolder extends BaseRecyclerViewHolder<FollowPost> {


    private RecyclerView rvFollow;
    private TextView tvTitle;
    private TextView tvMore;
    private FeedItemFollowAdapter feedAdapter;
    private OnFollowChangeListener followChangeListener;
    private OnRequestPermissionCallback mPermissionCallback;
    private String mFeedSource;

    public FeedFollowViewHolder(View itemView, String feedSource) {
        super(itemView);
        this.mFeedSource = feedSource;
        rvFollow = itemView.findViewById(R.id.rv_follow);
        tvTitle = itemView.findViewById(R.id.tv_item_title);
        tvMore = itemView.findViewById(R.id.tv_more_recommend);
    }


    public void setFollowChangeListener(OnFollowChangeListener followChangeListener) {
        this.followChangeListener = followChangeListener;
    }

    public void setPermissionCallback(OnRequestPermissionCallback callback) {
        mPermissionCallback = callback;
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final FollowPost data) {
        super.bindViews(adapter, data);
        tvTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "follow_recommend_user_title", false));
        tvMore.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "follow_recommend_more", false));
        rvFollow.removeAllViews();
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ABTest.INSTANCE.check(ABKeys.TEST_RECOMMEND) == 1 && data.getFromPosition() == 1)
                    RecommendFollowActivity.launch();
                else com.redefine.welike.business.user.ui.page.RecommendFollowActivity.launch();
                InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_MORE);
                InterestAndRecommendCardEventManager.INSTANCE.report7();
            }
        });
        String source = null;
        if (TextUtils.equals(mFeedSource, EventConstants.FEED_PAGE_HOME)) {
            source = EventConstants.FEED_PAGE_HOME_CARD_RECOMMEND;
        } else if (TextUtils.equals(mFeedSource, EventConstants.FEED_PAGE_DISCOVER_FOR_YOU)) {
            source = EventConstants.FEED_PAGE_DISCOVER_CARD_RECOMMEND;
        }
        feedAdapter = new FeedItemFollowAdapter(source);
        feedAdapter.setPermissionCallback(mPermissionCallback);
        feedAdapter.setUsers(data.getList());
        rvFollow.setAdapter(feedAdapter);
        rvFollow.setLayoutManager(new LinearLayoutManager(rvFollow.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (adapter instanceof FeedRecyclerViewAdapter) {
            feedAdapter.setListener(new DefaultFollowClickListener(rvFollow, ((FeedRecyclerViewAdapter) adapter).getFeedSource(), followChangeListener));
        }
    }


    public interface OnFollowChangeListener {

        void onCancel();

    }


}
