package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.util.PostRichItemClickHandler;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.SearchEventManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liwb on 2018/1/10.
 */

public class TextFeedViewHolder extends BaseFeedViewHolder {

    public final RichTextView mText;
    public final ViewGroup mBottomShadowView;

    public TextFeedViewHolder(View itemView, IFeedOperationListener listener) {
        super(itemView, listener);
        mText = itemView.findViewById(R.id.text_feed_content);
        mBottomShadowView = itemView.findViewById(R.id.common_feed_bottom_root_shadow);
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final PostBase postBase) {
        super.bindViews(adapter, postBase);
        mText.getRichProcessor().setOnRichItemClickListener(new PostRichItemClickHandler(mText.getContext(), postBase) {
            @Override
            public void onRichItemClick(RichItem richItem) {

                if (mListener != null) {
                    mListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_TEXT);
                }

                if (richItem.isTopicItem()) {
                    SearchEventManager.INSTANCE.setTopic_name(TextUtils.isEmpty(richItem.display) ? richItem.source : richItem.display);
                    SearchEventManager.INSTANCE.setFrom_page(EventConstants.SEARCH_FROM_PAGE_FEED);
                    SearchEventManager.INSTANCE.report2();
                }
                if (iBrowseClickListener != null) {

                    if (richItem.isTopicItem()) {
                        if (TextUtils.isEmpty(richItem.id)) return;
                        Bundle bundle = new Bundle();
                        TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
                        String target = TextUtils.isEmpty(richItem.display) ? richItem.source : richItem.display;
                        bean.name = target;
                        bean.id = richItem.id;
                        bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, false, 0);

                    } else if (richItem.isMoreItem()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
                        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, false, 0);
                    } else if (richItem.isAtItem()) {
                        UserHostPage.launch(true, richItem.id);
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, false, 0);
                    } else if (richItem.isSuperTopicItem()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, richItem.id);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, false, 0);
                    } else {
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, true, 0);
                    }
                } else
                    super.onRichItemClick(richItem);
            }
        });

        String content = mIsShowContent ? postBase.getText() : postBase.getSummary();
        mText.getRichProcessor().setRichContent(content, postBase.getRichItemList());
        if (!mIsShowContent) {
            int contentLength = TextUtils.isEmpty(postBase.getText()) ? 0 : postBase.getText().length();
            int summaryLength = TextUtils.isEmpty(postBase.getSummary()) ? 0 : postBase.getSummary().length();
            if (contentLength != summaryLength && contentLength != 0 && summaryLength != 0) {
                mText.getRichProcessor().showMoreBtn(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_summary_more"));
            }
        }
        if (mFeedClickEnable) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iBrowseClickListener != null) {
                        iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_POST, false, 0);
                    }
                    if (mListener != null) {
                        mListener.onPostBodyClick(postBase);
                    }
                    onClickFeedRootView(postBase);
                    if (adapter instanceof FeedRecyclerViewAdapter) {
                        ExposeEventReporter.INSTANCE.reportPostClick(postBase, ((FeedRecyclerViewAdapter) adapter).getFeedSource(), EventLog1.FeedView.FeedClickArea.TEXT);
                    }
                }
            });
        }
    }

    private void onClickFeedRootView(PostBase postBase) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        if (mListener != null) {
            mListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_TEXT);
        }
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    public void showBottomShadowView(boolean isShow) {
        mBottomShadowView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
