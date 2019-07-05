package com.redefine.welike.business.browse.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.share.request.ShareCountReportManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.constant.FeedViewHolderHelper;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.GPScorePost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.anko.FeedImageItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedPicPollItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedVideoItemViewUI;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.anko.FeedTextItemViewUI;
import com.redefine.welike.business.feeds.ui.viewholder.ArtTextFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedInterestViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedDeleteViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedLinkViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedPicViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedTextViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedVideoViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedVotePicViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedVoteTextViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwordTextArtViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.GPScoreViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.LinkFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.PicFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.TextFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.VideoFeedSpecialViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.VideoFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.VotePicViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.VoteTextFeedViewHolder;

import org.jetbrains.anko.AnkoContext;

import java.util.List;

/**
 * create by honlin 20180621
 */

public class BrowseFeedRecyclerViewAdapter<H> extends FeedRecyclerViewAdapter<H> implements IFeedOperationListener, ShareCountReportManager.ShareCountCallback {


    private IBrowseClickListener iBrowseClickListener;

    private GPScorePost gpScorePost;

    private String interestId = "";

    public BrowseFeedRecyclerViewAdapter(String source) {
        super(null, source);
        ShareCountReportManager.getInstance().register(this);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }

    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        return super.onCreateItemViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindItemViewHolder(final BaseRecyclerViewHolder holder, final int position) {
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).getFeedHeadView().setTrending(true);
        }
        super.onBindItemViewHolder(holder, position);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        holder.itemView.setLayoutParams(marginLayoutParams);

        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).getFeedHeadView().setTrending(true);
            ((BaseFeedViewHolder) holder).dismissDivider(position == mPostBaseList.size() - 1);
            ((BaseFeedViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
        } else if (holder instanceof VideoFeedSpecialViewHolder) {
            ((VideoFeedSpecialViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
        } else if (holder instanceof GPScoreViewHolder) {
            holder.bindViews(this, mPostBaseList.get(position));
            ((GPScoreViewHolder) holder).setChangeListener(new GPScoreViewHolder.GPDataChangeListener() {
                @Override
                public void dataChanged(GPScorePost data) {
                    ((GPScorePost) mPostBaseList.get(position)).setCurrentType(data.getCurrentType());
                    ((GPScorePost) mPostBaseList.get(position)).setCurrentSelect(data.getCurrentSelect());
                    notifyItemChanged(holder.getAdapterPosition());
                }

                @Override
                public void dissmiss() {
                    removeScoreItem(position);
                }
            });

        }

    }

    @Override
    protected int getRealItemViewType(int position) {
        // TODO: 2018/5/12
        if (TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO))
            return FeedViewHolderHelper.FEED_CARD_TYPE_ALL_VIDEO;
        PostBase postBase = mPostBaseList.get(position);
        return FeedViewHolderHelper.getFeedViewHolderType(postBase);
//        return FeedViewHolderHelper.FEED_CARD_TYPE_TEXT;
    }


    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public void addHisData(List<PostBase> feeds) {
        if (CollectionUtil.isEmpty(feeds)) {
            return;
        }
        mPostBaseList.addAll(feeds);
        notifyDataSetChanged();
    }

    public void addNewData(List<PostBase> feeds) {

        mPostBaseList.clear();
        if (!CollectionUtil.isEmpty(feeds)) {
            mPostBaseList.addAll(feeds);
        }

        if (interestPost != null) {
            mPostBaseList.add(mPostBaseList.size() > 0 ? 1 : 0, interestPost);
        }
        notifyDataSetChanged();
    }

    public void addScoreData(int position) {

//        if (getHeader() != null) {
//            position = position + 1;
//        }

        if (gpScorePost == null)
            gpScorePost = new GPScorePost();
        mPostBaseList.add(position, gpScorePost);
        notifyItemInserted(position);

    }

    private void removeScoreItem(int position) {
        mPostBaseList.remove(position);
        notifyItemRemoved(position);
        gpScorePost = null;
        int last = getAdapterItemPosition(mPostBaseList.size() - 1);
        this.notifyItemRangeChanged(position, last);
    }


    public void destroy() {
        destroyVideo();
        ShareCountReportManager.getInstance().unregister(this);
    }

    @Override
    public void onMenuBtnClick(final Context context, final PostBase postBase) {


    }


    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }


    public String getSource() {
        return mFeedSource;
    }

    public void setSource(String source) {
        this.mFeedSource = source;
    }

}
