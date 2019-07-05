package com.redefine.welike.business.feeds.ui.adapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.richtext.RichItem;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.constant.FeedViewHolderHelper;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultRichItemClickHandler;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailForwardViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwb on 2018/1/12.
 */

public class FeedDetailForwardAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, PostBase> implements SinglePostManager.PostDeleteListener {

    private List<PostBase> mData = new ArrayList<PostBase>();

    private IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener;

    public FeedDetailForwardAdapter() {
        SinglePostManager.getInstance().register(this);
    }

    public void setData(List<PostBase> postBaseList) {
        mData = postBaseList;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new FeedDetailForwardViewHolder(mInflater.inflate(R.layout.feed_detail_forward_item, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, final int position) {
        holder.bindViews(this, getRealItem(position));


        if (holder instanceof FeedDetailForwardViewHolder && iBrowseFeedDetailClickListener != null) {

            final FeedDetailForwardViewHolder forwardViewHolder = (FeedDetailForwardViewHolder) holder;

            forwardViewHolder.mFeedContent.getRichProcessor().setOnRichItemClickListener(new DefaultRichItemClickHandler(forwardViewHolder.mFeedContent.getContext()) {
                @Override
                public void onRichItemClick(RichItem richItem) {
                    if (richItem.isLinkItem()) {
                        String target = TextUtils.isEmpty(richItem.target) ? richItem.source : richItem.target;
                        WebViewActivity.luanch(forwardViewHolder.mFeedContent.getContext(), target);
                    } else if (richItem.isAtItem()) {
                        if (iBrowseFeedDetailClickListener != null) {
                            iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                        }
                        UserHostPage.launch(true, richItem.id);
                    } else if (richItem.isTopicItem()) {

                        if (TextUtils.isEmpty(richItem.id)) return;
                        Bundle bundle = new Bundle();
                        TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
                        String target = TextUtils.isEmpty(richItem.display) ? richItem.source : richItem.display;
                        bean.name = target;
                        bean.id = richItem.id;
                        bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
                        if (iBrowseFeedDetailClickListener != null) {
                            iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                        }
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
                    } else if (richItem.isSuperTopicItem()) {
                        Bundle bundle = new Bundle();
                        if (iBrowseFeedDetailClickListener != null) {
                            iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                        }
                        bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, richItem.id);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
                    }
                }
            });

            forwardViewHolder.mUserHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iBrowseFeedDetailClickListener != null) {
                        iBrowseFeedDetailClickListener.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, false);
                    }
                    UserHostPage.launch(true, mData.get(position).getUid());
                }
            });

        }
    }

    public void setiBrowseFeedDetailClickListener(IBrowseFeedDetailClickListener iBrowseFeedDetailClickListener) {
        this.iBrowseFeedDetailClickListener = iBrowseFeedDetailClickListener;
    }

    @Override
    protected int getRealItemViewType(int position) {
        return FeedViewHolderHelper.FEED_CARD_TYPE_TEXT;
    }

    public void addNewData(PostBase postBase) {
        mData.add(postBase);
        notifyDataSetChanged();
    }

    public void addNewData(List<PostBase> postBases) {

        mData.clear();
        if (!CollectionUtil.isEmpty(postBases)) {
            mData.addAll(0, postBases);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<PostBase> postBases) {
        if (CollectionUtil.isEmpty(postBases)) {
            return;
        }
        mData.addAll(postBases);
        notifyDataSetChanged();
    }

    public void doRealFeedDelete(PostBase postBase) {
        refreshOnDelete(postBase.getPid());
        SinglePostManager.getInstance().delete(postBase);
    }

    private void refreshOnDelete(String pid) {
        // 首先处理forward
        int size = mData.size();
        PostBase postBase = null;
        for (int i = 0; i < size; i++) {
            postBase = mData.get(i);
            if (postBase instanceof ForwardPost && !((ForwardPost) postBase).isForwardDeleted() && ((ForwardPost) postBase).getRootPost() != null && TextUtils.equals(((ForwardPost) postBase).getRootPost().getPid(), pid)) {
                ((ForwardPost) postBase).setForwardDeleted(true);
                this.notifyItemChanged(getAdapterItemPosition(i));
            }
        }
        for (int i = 0; i < size; i++) {
            postBase = mData.get(i);
            if (TextUtils.equals(postBase.getPid(), pid)) {
                int position = getAdapterItemPosition(i);
                int last = getAdapterItemPosition(mData.size() - 1);
                mData.remove(postBase);
                this.notifyItemRemoved(position);
                this.notifyItemRangeChanged(position, last);
                break;
            }
        }
    }

    public void destroy() {
        SinglePostManager.getInstance().unregister(this);
    }

    @Override
    public int getRealItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    protected PostBase getRealItem(int position) {
        return mData.get(position);
    }

    @Override
    public void onPostDeleted(String pid) {
        refreshOnDelete(pid);
    }
}
