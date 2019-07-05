package com.redefine.welike.business.feeds.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.feeds.ui.bean.HomeHeaderBean;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedVotePicViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.ForwardFeedVoteTextViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.HomeHeaderViewHoler;
import com.redefine.welike.business.feeds.ui.viewholder.VotePicViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.VoteTextFeedViewHolder;
import com.redefine.welike.statistical.EventConstants;

import java.util.List;

public class HomeFeedRecyclerViewAdapter extends FeedRecyclerViewAdapter<HomeHeaderBean> {
    private boolean mIsResume = true;
//    private HomeTaskViewHolder.OnTaskActionListener mListener;

    public HomeFeedRecyclerViewAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_HOME);
        setHeader(new HomeHeaderBean());
    }


    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.home_header_item, parent, false);
        return new HomeHeaderViewHoler(view);
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (holder instanceof HomeHeaderViewHoler) {
            holder.bindViews(this, getHeader());
            ((HomeHeaderViewHoler) holder).setCurrentActivityState(getHeader(), mIsResume);
//            ((HomeHeaderViewHoler) holder).setOnActionClickListener(mListener);
        }
    }

    private int firstVoteItem = -1;

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);
        if (position == 0) {
//            if (listener != null) {
//                listener.onShow(holder, null);
//            }
        }
        if (holder instanceof VotePicViewHolder
                || holder instanceof ForwardFeedVotePicViewHolder
                || holder instanceof ForwardFeedVoteTextViewHolder
                || holder instanceof VoteTextFeedViewHolder) {
            if (firstVoteItem < 0) {
                firstVoteItem = position;
            }
//            if (firstVoteItem == position && voteShownListener != null) {
//                voteShownListener.onShow(holder, (ArrowTextView) holder.itemView.findViewById(R.id.guide));
//                if (holder instanceof VotePicViewHolder) {
//                    ((VotePicViewHolder) holder).voteListener = voteShownListener;
//                }
//                if (holder instanceof ForwardFeedVotePicViewHolder) {
//                    ((ForwardFeedVotePicViewHolder) holder).voteListener = voteShownListener;
//                }
//                if (holder instanceof ForwardFeedVoteTextViewHolder) {
//                    ((ForwardFeedVoteTextViewHolder) holder).voteListener = voteShownListener;
//                }
//                if (holder instanceof VoteTextFeedViewHolder) {
//                    ((VoteTextFeedViewHolder) holder).voteListener = voteShownListener;
//                }
//            }
        }
    }


//    public void setFirstShownListener(GuideListener<BaseRecyclerViewHolder> listener) {
//        this.listener = listener;
//    }

//    private GuideListener<BaseRecyclerViewHolder> listener;
//    private GuideListener<BaseRecyclerViewHolder> voteShownListener;

    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder holder = super.onCreateItemViewHolder(parent, viewType);
//        if (holder instanceof BaseFeedViewHolder) {
//            ((BaseFeedViewHolder) holder).getmCommonFeedBottomView().setLikeListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        //dismiss Guide.
//                        listener.onClick(v);
//                    }
//                }
//            });
//        }
        return holder;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HomeHeaderViewHoler) {
            ((HomeHeaderViewHoler) holder).stopBanner();
        }
    }

    public void updateBanner(List<Banner> bannerList) {
        if (getHeader() != null) {
            getHeader().setBanner(bannerList);
//            if (CollectionUtil.isEmpty(getHeader().getBanner()) && getHeader().getNewMission() == null) {
//            if (CollectionUtil.isEmpty(getHeader().getBanner())) {
//                hideHeader();
//            } else {
//                showHeader();
//            }
        }
    }

//    public void updateTask(Mission mission) {
//        if (getHeader() != null) {
//            getHeader().setNewMission(mission);
//            if (CollectionUtil.isEmpty(getHeader().getBanner()) && getHeader().getNewMission() == null) {
//                hideHeader();
//            } else {
//                showHeader();
//            }
//        }
//    }
//
//    public void setOnActionClickListener(HomeTaskViewHolder.OnTaskActionListener listener) {
//        mListener = listener;
//    }

    public void onActivityResume() {
        mIsResume = true;
        notifyItemChanged(getHeaderPosition());
    }

    public void onActivityPause() {
        mIsResume = false;
        notifyItemChanged(getHeaderPosition());
    }

//    public void setVoteShownListener(GuideListener<BaseRecyclerViewHolder> voteShownListener) {
//        this.voteShownListener = voteShownListener;
//    }
}
