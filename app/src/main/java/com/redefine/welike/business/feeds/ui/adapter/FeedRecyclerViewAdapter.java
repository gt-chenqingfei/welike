package com.redefine.welike.business.feeds.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.share.request.ShareCountReportManager;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.constant.FeedViewHolderHelper;
import com.redefine.welike.business.browse.ui.listener.InterestChangeListener;
import com.redefine.welike.business.browse.ui.listener.InterestSelectListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;
import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.UserVoteManager;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.GPScorePost;
import com.redefine.welike.business.feeds.management.bean.PollInfo;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.bean.PollPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardArtItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedArtItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedFollowItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardDeleteItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardPicPollItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedGPScoreItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedImageItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardLinkItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedInterestItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedLinkItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardImageItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedPicPollItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedSpecialVideoItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardTextItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedTextItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardTextPollItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedTextPollItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardVideoItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedVideoItemViewUI;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.listener.IMenuBtnClickListener;
import com.redefine.welike.business.feeds.ui.listener.IVoteChoiceListener;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.feeds.ui.viewholder.ArtTextFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedFollowViewHolder;
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
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.bean.FollowPost;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.videoplayer.management.manager.IVideoPlayerManager;
import com.redefine.welike.business.videoplayer.management.manager.SingleVideoPlayerManager;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.anko.AnkoContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class FeedRecyclerViewAdapter<H> extends LoadMoreFooterRecyclerAdapter<H, PostBase> implements IFeedOperationListener
        , FollowUserManager.FollowUserCallback, SinglePostManager.PostDeleteListener, BlockUserManager.BlockUserCallback, UserVoteManager.UserVoteCallback, IVoteChoiceListener, ShareCountReportManager.ShareCountCallback, IDataProvider<List<PostBase>> {

    //    private final IPageStackManager mPageStackManager;
    protected String mFeedSource;
    private RecyclerView mRecyclerView;
    protected List<PostBase> mPostBaseList = new ArrayList<>();

    protected PostBase interestPost;

    private PostBase followPost;

    private GPScorePost gpScorePost;

    private InterestSelectListener interestSelectListener;
    private IMenuBtnClickListener mFeedDeleteCallback;
    private OnPostButtonClickListener mPostButtonListener;
    private OnRequestPermissionCallback mPermissionCallback;
    private IVideoPlayerManager mSingleVideoPlayerManager;
    private LoadingDlg mLoadingDlg;

    private int followPosition = -1;

    private String interestId = "";
    private int mFirstTopMargin;

    public FeedRecyclerViewAdapter(IPageStackManager pageStackManager, String source) {
        FollowUserManager.getInstance().register(this);
        SinglePostManager.getInstance().register(this);
        BlockUserManager.getInstance().register(this);
        UserVoteManager.getInstance().register(this);
        ShareCountReportManager.getInstance().register(this);
        mSingleVideoPlayerManager = new SingleVideoPlayerManager();
        mFeedSource = source;
        mFirstTopMargin = ScreenUtils.dip2Px(8);
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
        BaseRecyclerViewHolder viewHolder;
        switch (viewType) {
            case FeedViewHolderHelper.FEED_CARD_TYPE_TEXT:
                viewHolder = new TextFeedViewHolder(new FeedTextItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
//                viewHolder = new TextFeedViewHolder(mInflater.inflate(R.layout.text_feed_item, parent, false), this);
                break;
            case FeedViewHolderHelper.FEED_CARD_TYPE_VOTE_PIC:
//                viewHolder = new VotePicViewHolder(mInflater.inflate(R.layout.vote_pic_feed_item, parent, false), this, null);
                viewHolder = new VotePicViewHolder(new FeedPicPollItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this, null);
                break;
            case FeedViewHolderHelper.FEED_CARD_TYPE_PIC:
//                viewHolder = new PicFeedViewHolder(mInflater.inflate(R.layout.pic_feed_item, parent, false), this);
                viewHolder = new PicFeedViewHolder(new FeedImageItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;
            case FeedViewHolderHelper.FEED_CARD_TYPE_VIDEO:
//                viewHolder = new VideoFeedViewHolder(mInflater.inflate(R.layout.video_feed_item, parent, false), this);
                viewHolder = new VideoFeedViewHolder(new FeedVideoItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_LINK:
//                viewHolder = new LinkFeedViewHolder(mInflater.inflate(R.layout.link_feed_item, parent, false), this);
                viewHolder = new LinkFeedViewHolder(new FeedLinkItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_VOTE_TEXT:
//                viewHolder = new VoteTextFeedViewHolder(mInflater.inflate(R.layout.vote_feed_text_layout, parent, false), this, this);
                viewHolder = new VoteTextFeedViewHolder(new FeedTextPollItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this, this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_ART:
//                viewHolder = new ArtTextFeedViewHolder(mInflater.inflate(R.layout.art_text_feed_item, parent, false), this);
                viewHolder = new ArtTextFeedViewHolder(new FeedArtItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FOLLOW:
//                viewHolder = new FeedFollowViewHolder(mInflater.inflate(R.layout.item_feed_follow_layout, parent, false));
                viewHolder = new FeedFollowViewHolder(new FeedFollowItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)),mFeedSource);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_ALL_VIDEO:
//                viewHolder = new VideoFeedSpecialViewHolder(mInflater.inflate(R.layout.video_feed_special_layout, parent, false), this);
                viewHolder = new VideoFeedSpecialViewHolder(new FeedSpecialVideoItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_INTEREST:
//                viewHolder = new FeedInterestViewHolder(mInflater.inflate(R.layout.item_feed_interest_layout, parent, false));
                viewHolder = new FeedInterestViewHolder(new FeedInterestItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)));
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_GP_SCORE:
//                viewHolder = new GPScoreViewHolder(mInflater.inflate(R.layout.item_feed_gp_score_layout, parent, false));
                viewHolder = new GPScoreViewHolder(new FeedGPScoreItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)));
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_LINK:
//                viewHolder = new ForwardFeedLinkViewHolder(mInflater.inflate(R.layout.forward_link_feed_item, parent, false), this);
                viewHolder = new ForwardFeedLinkViewHolder(new FeedForwardLinkItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;
            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_TEXT:
//                viewHolder = new ForwardFeedTextViewHolder(mInflater.inflate(R.layout.forward_text_feed_item, parent, false), this);
                viewHolder = new ForwardFeedTextViewHolder(new FeedForwardTextItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_PIC:
//                viewHolder = new ForwardFeedPicViewHolder(mInflater.inflate(R.layout.forward_pic_feed_item, parent, false), this);
                viewHolder = new ForwardFeedPicViewHolder(new FeedForwardImageItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VIDEO:
//                viewHolder = new ForwardFeedVideoViewHolder(mInflater.inflate(R.layout.forward_video_feed_item, parent, false), this);
                viewHolder = new ForwardFeedVideoViewHolder(new FeedForwardVideoItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_DELETE:
//                viewHolder = new ForwardFeedDeleteViewHolder(mInflater.inflate(R.layout.forward_delete_feed_item, parent, false), this);
                viewHolder = new ForwardFeedDeleteViewHolder(new FeedForwardDeleteItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VOTE_PIC:
                viewHolder = new ForwardFeedVotePicViewHolder(new FeedForwardPicPollItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this, this);
//                viewHolder = new ForwardFeedVotePicViewHolder(mInflater.inflate(R.layout.forward_vote_pic_feed_item, parent, false), this, this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VOTE_TEXT:

//                viewHolder = new ForwardFeedVoteTextViewHolder(mInflater.inflate(R.layout.forward_vote_text_feed_item, parent, false), this, this);
                viewHolder = new ForwardFeedVoteTextViewHolder(new FeedForwardTextPollItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent,  false)), this, this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_ART:
//                viewHolder = new ForwordTextArtViewHolder(mInflater.inflate(R.layout.forward_art_text_item, parent, false), this);
                viewHolder = new ForwordTextArtViewHolder(new FeedForwardArtItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent,  false)), this);
                break;

            case FeedViewHolderHelper.FEED_CARD_TYPE_UNKNOWN:
            default:
                viewHolder = new TextFeedViewHolder(new FeedTextItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false)), this);
//                viewHolder = new TextFeedViewHolder(mInflater.inflate(R.layout.text_feed_item, parent, false), this);
                break;
        }
        return viewHolder;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindItemViewHolder(final BaseRecyclerViewHolder holder, final int position) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            marginLayoutParams.topMargin = mFirstTopMargin;
        } else {
            marginLayoutParams.topMargin = 0;
        }
        holder.itemView.setLayoutParams(marginLayoutParams);

        if (holder instanceof BaseFeedViewHolder) {
            mPostBaseList.get(position).setPosition(holder.getAdapterPosition());
            holder.bindViews(this, mPostBaseList.get(position));
            ((BaseFeedViewHolder) holder).dismissDivider(position == mPostBaseList.size() - 1);
            ((BaseFeedViewHolder) holder).getmCommonFeedBottomView().setOnPostButtonClickListener(mPostButtonListener);
            ((BaseFeedViewHolder) holder).getFeedHeadView().setFollowBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPostButtonListener != null) {
                        mPostButtonListener.onFollowClick(mPostBaseList.get(position));
                    }
                }
            });
            ((BaseFeedViewHolder) holder).setOnButtonClickListener(mPostButtonListener);
        } else if (holder instanceof FeedFollowViewHolder) {
            FeedFollowViewHolder feedInterestViewHolder = (FeedFollowViewHolder) holder;
            feedInterestViewHolder.setPermissionCallback(mPermissionCallback);
            feedInterestViewHolder.bindViews(this, (FollowPost) mPostBaseList.get(position));
            feedInterestViewHolder.setFollowChangeListener(new FeedFollowViewHolder.OnFollowChangeListener() {
                @Override
                public void onCancel() {
                    removeFollowItem(holder.getAdapterPosition());
                }
            });
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

        } else if (holder instanceof FeedInterestViewHolder) {
            FeedInterestViewHolder feedInterestViewHolder = (FeedInterestViewHolder) holder;
            feedInterestViewHolder.bindViews(this, (InterestPost) mPostBaseList.get(position));
            feedInterestViewHolder.setListener(new InterestChangeListener() {
                @Override
                public void onInterestSelectChange(@NotNull Interest interest) {
                    PostBase postBase = mPostBaseList.get(position);
                    if (postBase instanceof InterestPost && ((InterestPost) postBase).getList() != null)
                        for (Interest intrest : ((InterestPost) interestPost).getList()) {
                            if (TextUtils.equals(interest.getId(), intrest.getId())) {
                                intrest.setSelected(interest.isSelected());
                            }
                        }
                }

                @Override
                public void onConfirm() {

                    try {
                        int count = 0;
                        List<com.redefine.welike.business.browse.management.bean.Interest> interests = new ArrayList<>();
                        List<String> interestIDs = new ArrayList<>();
                        PostBase postBase = mPostBaseList.get(position);

                        if (postBase instanceof InterestPost && ((InterestPost) postBase).getList() != null) {
                            for (Interest intrest : ((InterestPost) postBase).getList()) {
                                count += intrest.isSelected() ? 1 : 0;
                                if (intrest.isSelected()) {
                                    interestIDs.add(intrest.getId());
                                    interests.add(new com.redefine.welike.business.browse.management.bean.Interest(intrest.getId(), intrest.getIcon(), intrest.getName()));
                                }
                            }
                        }

                        if (count >= 1) {
                            InterestAndRecommendCardEventManager.INSTANCE.setInterest_name(getSelectedInterest(position));
                            InterestAndRecommendCardEventManager.INSTANCE.report3();
                            removeInterestItem(position);

                        }

                        if (interestSelectListener != null) {
                            interestSelectListener.onSelectInterest(interests);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancel() {
                    InterestAndRecommendCardEventManager.INSTANCE.setInterest_name(getSelectedInterest(position));
                    InterestAndRecommendCardEventManager.INSTANCE.report2();
                    removeInterestItem(position);

                }
            });

        } else if (holder instanceof VideoFeedSpecialViewHolder) {
            mPostBaseList.get(position).setPosition(holder.getAdapterPosition());
            ((VideoFeedSpecialViewHolder) holder).bindViews(this, mPostBaseList.get(position));
            ((VideoFeedSpecialViewHolder) holder).setPostButtonListener(mPostButtonListener);
            ((VideoFeedSpecialViewHolder) holder).getFeedHeadView().setFollowBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPostButtonListener != null) {
                        mPostButtonListener.onFollowClick(mPostBaseList.get(position));
                    }
                }
            });
        }
        ShareEventManager.INSTANCE.setFeedname(mFeedSource);
    }

    private List<String> getSelectedInterest(int position) {
        List<String> interests = new ArrayList<>();
        try {
            PostBase postBase = mPostBaseList.get(position);
            if (postBase instanceof InterestPost && ((InterestPost) postBase).getList() != null) {
                for (Interest intrest : ((InterestPost) postBase).getList()) {
                    if (intrest.isSelected()) {
                        interests.add(intrest.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return interests;
    }


    private void removeInterestItem(int position) {
        if (position >= mPostBaseList.size()) return;
        mPostBaseList.remove(position);
        notifyItemRemoved(position);
        interestPost = null;
        int last = getAdapterItemPosition(mPostBaseList.size() - 1);
        this.notifyItemRangeChanged(position, last);
    }


    private void removeScoreItem(int position) {
        if (position >= mPostBaseList.size()) return;
        mPostBaseList.remove(position);
        notifyItemRemoved(position);
        gpScorePost = null;
        int last = getAdapterItemPosition(mPostBaseList.size() - 1);
        this.notifyItemRangeChanged(position, last);
    }

    private void removeFollowItem(int position) {
        if (hasHeader()) {
            position--;
        }
        if (position >= mPostBaseList.size()) return;
        mPostBaseList.remove(position);
        notifyItemRemoved(position);
        followPost = null;
        int last = getAdapterItemPosition(mPostBaseList.size() - 1);
        this.notifyItemRangeChanged(position, last);
    }

    @Override
    protected int getRealItemViewType(int position) {
        // TODO: 2018/5/12
        PostBase postBase = mPostBaseList.get(position);
        if (TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO))
            if (postBase.getType() == PostBase.POST_TYPE_GP_SCORE)
                return FeedViewHolderHelper.FEED_CARD_TYPE_GP_SCORE;
            else
                return FeedViewHolderHelper.FEED_CARD_TYPE_ALL_VIDEO;

        return FeedViewHolderHelper.getFeedViewHolderType(postBase);
    }

    @Override
    public int getRealItemCount() {
        return mPostBaseList.size();
    }

    @Override
    protected PostBase getRealItem(int position) {
        return mPostBaseList.get(position);
    }


    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }


    public List<PostBase> getPostBaseList() {
        return mPostBaseList;
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
            mPostBaseList.addAll(0, feeds);
        }
        Account account = AccountManager.getInstance().getAccount();
        if (interestPost != null && account != null &&
                (account.getIntrests() == null
                        || account.getIntrests().size() == 0)) {
            mPostBaseList.add(mPostBaseList.size() > 0 ? 1 : 0, interestPost);
        }


        notifyDataSetChanged();
    }

    public void addInterestData(PostBase interest) {

        if (interestPost != null) return;
        interestPost = interest;

        if (!CollectionUtil.isEmpty(mPostBaseList)) {
            mPostBaseList.add(mPostBaseList.size() > 0 ? 1 : 0, interestPost);
            notifyDataSetChanged();
        }
    }

    public void setPermissionCallback(OnRequestPermissionCallback callback) {
        mPermissionCallback = callback;
    }

    public void addFollowData(PostBase follow) {
        followPost = follow;
        if (!CollectionUtil.isEmpty(mPostBaseList) && followPost != null) {
            Account account = AccountManager.getInstance().getAccount();
            if ((account == null ? 0 : account.getFollowUsersCount()) > 5)
                followPosition = mPostBaseList.size() > 7 ? 7 : mPostBaseList.size() > 1 ? 1 : 0;
            else followPosition = mPostBaseList.size() > 1 ? 1 : 0;
            if (mPostBaseList.get(followPosition) instanceof FollowPost) return;
            mPostBaseList.add(followPosition, followPost);
            notifyDataSetChanged();
        }
    }

    public void addFollowData4U(PostBase follow) {
        followPost = follow;
        if (!CollectionUtil.isEmpty(mPostBaseList) && followPost != null) {
            followPosition = mPostBaseList.size() > 7 ? 7 : mPostBaseList.size() > 1 ? 1 : 0;
            if (mPostBaseList.get(followPosition) instanceof FollowPost) return;
            mPostBaseList.add(followPosition, followPost);
            notifyDataSetChanged();
        }
    }

    public void addFollowData4Recommend(PostBase follow) {
        followPost = follow;
        if (!CollectionUtil.isEmpty(mPostBaseList) && followPost != null) {
            followPosition = 0;
            mPostBaseList.add(followPosition, followPost);
            notifyDataSetChanged();
        }
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


    public void addFeed(PostBase feed) {
        if (feed == null) {
            return;
        }
        mPostBaseList.add(0, feed);
        notifyDataSetChanged();
    }

    public void refreshOnFollow(String uid) {
        for (int i = 0; i < mPostBaseList.size(); i++) {
            PostBase postBase = mPostBaseList.get(i);
            if (TextUtils.equals(postBase.getUid(), uid)) {
                postBase.setFollowing(true);
                refreshItemFollowStatus(i, postBase);
            }

            if (postBase instanceof ForwardPost && !((ForwardPost) postBase).isForwardDeleted() && ((ForwardPost) postBase).getRootPost() != null) {
                if (TextUtils.equals(((ForwardPost) postBase).getRootPost().getUid(), uid)) {
                    ((ForwardPost) postBase).getRootPost().setFollowing(true);
                    refreshItemFollowStatus(i, postBase);
                }
            }
        }
    }

    public void refreshOnUnFollow(String uid) {
        for (int i = 0; i < mPostBaseList.size(); i++) {
            PostBase postBase = mPostBaseList.get(i);
            if (TextUtils.equals(postBase.getUid(), uid)) {
                postBase.setFollowing(false);
                refreshItemFollowStatus(i, postBase);
            }

            if (postBase instanceof ForwardPost && !((ForwardPost) postBase).isForwardDeleted() && ((ForwardPost) postBase).getRootPost() != null) {
                if (TextUtils.equals(((ForwardPost) postBase).getRootPost().getUid(), uid)) {
                    ((ForwardPost) postBase).getRootPost().setFollowing(false);
                    refreshItemFollowStatus(i, postBase);
                }
            }
        }
    }

    private void refreshItemFollowStatus(int position, PostBase videoPost) {
        if (mRecyclerView == null) {//for feed detail
            return;
        }
        if (hasHeader()) {
            position++;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        View view = layoutManager.findViewByPosition(position);
        if (view != null) {
            UserFollowBtn userFollowBtn = view.findViewById(R.id.common_feed_follow_btn);
            if (videoPost.isFollowing() && userFollowBtn != null) {
                userFollowBtn.setFollowStatus(IFollowBtn.FollowStatus.FOLLOWING);
            }
        }
    }


    private void refreshOnDelete(String pid) {
        // 首先处理forward
        int size = mPostBaseList.size();
        PostBase postBase;
        for (int i = 0; i < size; i++) {
            postBase = mPostBaseList.get(i);
            if (postBase instanceof ForwardPost && !((ForwardPost) postBase).isForwardDeleted() && ((ForwardPost) postBase).getRootPost() != null && TextUtils.equals(((ForwardPost) postBase).getRootPost().getPid(), pid)) {
                ((ForwardPost) postBase).setForwardDeleted(true);
                this.notifyItemChanged(getAdapterItemPosition(i));
            }
        }
        for (int i = 0; i < size; i++) {
            postBase = mPostBaseList.get(i);
            if (TextUtils.equals(postBase.getPid(), pid)) {
                int position = getAdapterItemPosition(i);
                int last = getAdapterItemPosition(mPostBaseList.size() - 1);
                mPostBaseList.remove(postBase);
                this.notifyItemRemoved(position);
                this.notifyItemRangeChanged(position, last);
                break;
            }
        }
    }

    public void refreshFeedsStatus(List<FeedsStatusObserver.FeedStatus> statusList) {
        for (FeedsStatusObserver.FeedStatus s : statusList) {
            for (PostBase p : mPostBaseList) {
                if (TextUtils.equals(p.getPid(), s.getPid())) {
                    p.setCommentCount(s.getCommentCount());
                    p.setForwardCount(s.getForwardCount());
                    p.setLikeCount(s.getLikeCount());
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onMenuBtnClick(final Context context, final PostBase postBase) {
        Function1<ShareMenu, Unit> menuInvoker = new Function1<ShareMenu, Unit>() {
            @Override
            public Unit invoke(ShareMenu shareMenu) {
                switch (shareMenu) {
                    case BLOCK:
                        doBlockUser(context, postBase);
                        break;
                    case DELETE:
                        doRealFeedDelete(context, postBase);
                        break;
                    case REPORT:
                        doReport(postBase);
                        break;
                    case UNLIKE:
                        doRealFeedUnLike(postBase);
                        break;
                    case UNBLOCK:
                        doUnblockUser(context, postBase);
                        break;
                    case TOP:
                        doTopUer(context, postBase);
                        break;
                    case UN_TOP:
                        doUnTopUer(context, postBase);
                        break;
                }
                return null;
            }
        };
        List<SharePackageModel> list = initMenuItemList(postBase, menuInvoker);
        doShare(context, postBase, list);
    }

    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1<ShareMenu, Unit> menuInvoker) {
        SharePackageModel delete = CustomShareMenuFactory.Companion.createMenu(ShareMenu.DELETE, menuInvoker);
        SharePackageModel report = CustomShareMenuFactory.Companion.createMenu(ShareMenu.REPORT, menuInvoker);
        SharePackageModel block = CustomShareMenuFactory.Companion.createMenu(ShareMenu.BLOCK, menuInvoker);
        SharePackageModel unblock = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNBLOCK, menuInvoker);
        SharePackageModel unlike = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNLIKE, menuInvoker);
        List<SharePackageModel> list = new ArrayList<>();
        if (postBase.isLike()) {
            list.add(unlike);
        }
        if (AccountManager.getInstance().isSelf(postBase.getUid())) {
            list.add(delete);
        } else {
            list.add(report);
            if (postBase.isBlock()) {
                list.add(unblock);
            } else {
                list.add(block);
            }
        }
        return list;
    }

    private TopListener mTopListener;

    public interface TopListener {

        void onTop(PostBase post);

        void unTop(PostBase post);

    }

    public void setTopListener(TopListener topListener) {
        mTopListener = topListener;

    }

    private void doTopUer(Context context, PostBase postBase) {
        if (null != postBase && null != mTopListener) {
            mTopListener.onTop(postBase);
        }

    }

    private void doUnTopUer(Context context, PostBase postBase) {
        if (null != postBase && null != mTopListener) {
            mTopListener.unTop(postBase);

        }
    }

    public void doUnblockUser(Context context, PostBase postBase) {
        mLoadingDlg = new LoadingDlg((Activity) context);
        mLoadingDlg.show();
        BlockUserManager.getInstance().unBlock(postBase.getUid());
        EventLog1.BlockUser.report2(postBase.getUid(), getFeedSource(), postBase.getPid(), postBase.getLanguage(), postBase.getTags(), null);
    }

    public void doBlockUser(final Context context, final PostBase postBase) {
        if (postBase == null) {
            return;
        }
        if (context instanceof Activity) {
            String title = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_user_confirm_title");

            CommonConfirmDialog.showCancelDialog(context, String.format(title, GlobalConfig.AT + postBase.getNickName()), new CommonConfirmDialog.IConfirmDialogListener() {
                @Override
                public void onClickCancel() {

                }

                @Override
                public void onClickConfirm() {
                    mLoadingDlg = new LoadingDlg((Activity) context);
                    mLoadingDlg.show();
                    BlockUserManager.getInstance().block(postBase.getUid());
                    EventLog1.BlockUser.report1(postBase.getUid(), getFeedSource(), postBase.getPid(), postBase.getLanguage(), postBase.getTags(), null);
                }
            });
        }

    }

    private void doShare(Context context, PostBase post, List<SharePackageModel> list) {
        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_OTHER);
        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_CARD);
        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(post), null, EventLog1.Share.ShareFrom.DIALOG,
                EventLog1.Share.PopPage.POST_CARD, post.getStrategy(), post.getOperationType(), getFeedSource(), post.getPid(), post.getLanguage(), post.getTags(), post.getUid(), FeedHelper.getRootPostId(post),
                FeedHelper.getRootPostUid(post), FeedHelper.getRootPostLanguage(post), FeedHelper.getRootPostTags(post), post.getSequenceId(),post.getReclogs());
        ShareHelper.sharePostWithCustomMenu(context, post, false, eventModel, list);
    }

    private void doReport(PostBase postBase) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FeedbackConstants.FEEDBACK_KEY_POST, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_REPOER_PAGE, bundle));
    }

    private void doRealFeedUnLike(PostBase postBase) {//
        int position = getAdapterItemPosition(mPostBaseList.indexOf(postBase));
        SinglePostManager.disSuperLike(postBase);
        if (TextUtils.equals(mFeedSource, EventConstants.FEED_PAGE_PROFILE_LIKE_OWNER) || TextUtils.equals(mFeedSource, EventConstants.FEED_PAGE_PROFILE_LIKE_VISIT)) {
            mPostBaseList.remove(postBase);
            this.notifyItemRemoved(position);
        } else {
            this.notifyItemChanged(position);
        }
    }

    public void doRealFeedDelete(Context context, PostBase postBase) {
        refreshOnDelete(postBase.getPid());
        SinglePostManager.getInstance().delete(postBase);
        if (mFeedDeleteCallback != null) {
            mFeedDeleteCallback.onMenuBtnClick(context, postBase);
        }
    }


    public void destroy() {
        FollowUserManager.getInstance().unregister(this);
        SinglePostManager.getInstance().unregister(this);
        BlockUserManager.getInstance().unregister(this);
        ShareCountReportManager.getInstance().unregister(this);
        destroyVideo();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    public void destroyVideo() {
        mSingleVideoPlayerManager.onDestroy();
        if (mRecyclerView != null) {
            mRecyclerView.setKeepScreenOn(false);
        }
    }

    public void playVideo(int position, ApolloVideoView videoPlayerView) {
        if (hasHeader()) {
            position--;
        }
        try {
            PostBase postBase = mPostBaseList.get(position);
            if (postBase instanceof VideoPost) {
                String videoSite = ((VideoPost) postBase).getVideoSite();
                if (!TextUtils.equals(videoSite, PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                    String videoUrl = ((VideoPost) postBase).getVideoUrl();
                    mSingleVideoPlayerManager.playNewVideo(videoPlayerView, videoUrl);
                    if (mRecyclerView != null) {
                        mRecyclerView.setKeepScreenOn(true);
                    }
                }
            } else if (postBase instanceof ForwardPost) {
                PostBase rootPost = ((ForwardPost) postBase).getRootPost();
                if (rootPost instanceof VideoPost) {
                    String videoSite = ((VideoPost) rootPost).getVideoSite();
                    if (!TextUtils.equals(videoSite, PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                        String videoUrl = ((VideoPost) rootPost).getVideoUrl();
                        mSingleVideoPlayerManager.playNewVideo(videoPlayerView, videoUrl);
                        if (mRecyclerView != null) {
                            mRecyclerView.setKeepScreenOn(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            //do nothing
        }
    }

    public void onPause() {
        mSingleVideoPlayerManager.onPause();
    }

    public void onResume() {
        mSingleVideoPlayerManager.onResume();

    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnFollow(uid);
        } else {
            refreshOnUnFollow(uid);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnUnFollow(uid);
        } else {
            refreshOnFollow(uid);
        }
    }

    public void setInterestSelectListener(InterestSelectListener interestSelectListener) {
        this.interestSelectListener = interestSelectListener;
    }

    public void setFeedMenuBtnClickCallback(IMenuBtnClickListener callback) {
        mFeedDeleteCallback = callback;
    }

    public void setOnPostButtonClickListener(OnPostButtonClickListener listener) {
        mPostButtonListener = listener;
    }

    @Override
    public void onPostDeleted(String pid) {
        refreshOnDelete(pid);
    }

    public void removeAll() {
        mPostBaseList.clear();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttachToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetachToWindow();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    public void onBlockCompleted(String uid, int errCode) {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            updatePostBlockStatus(uid, true);
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            updatePostBlockStatus(uid, false);
        }
    }

    private void updatePostBlockStatus(String uid, boolean block) {
        if (!CollectionUtil.isEmpty(mPostBaseList)) {
            for (PostBase postBase : mPostBaseList) {
                if (TextUtils.equals(uid, postBase.getUid())) {
                    postBase.setBlock(block);
                }
            }
        }
    }

    @Override
    public void onUserVotePosts(Object result, int errCode) {
        if (result instanceof PollInfo) {
            try {
                refreshOnVote((PollInfo) result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTextVote(String pid, String pollId, ArrayList<PollItemInfo> choiceIds, boolean isRepost) {
        UserVoteManager.getInstance().tryVote(pid, pollId, choiceIds, isRepost);

    }

    private void refreshOnVote(PollInfo pollInfo) {
        boolean hasVoteChanged = false;
        for (PostBase postBase : mPostBaseList) {

            if (postBase instanceof PollPost) {
                if (TextUtils.equals(((PollPost) postBase).mPollInfo.pollId, pollInfo.pollId)) {
                    hasVoteChanged = true;
                    ((PollPost) postBase).mPollInfo = pollInfo;
                }
            }
            if (postBase instanceof ForwardPost && !((ForwardPost) postBase).isForwardDeleted() && ((ForwardPost) postBase).getRootPost() instanceof PollPost) {
                if (TextUtils.equals(((PollPost) ((ForwardPost) postBase).getRootPost()).mPollInfo.pollId, pollInfo.pollId)) {
                    hasVoteChanged = true;
                    ((PollPost) ((ForwardPost) postBase).getRootPost()).mPollInfo = pollInfo;
                }
            }
        }
        if (hasVoteChanged) {
            notifyDataSetChanged();
        }
    }

    public String getFeedSource() {
        return mFeedSource;
    }

    public void setFeedSource(String source) {
        mFeedSource = source;
    }

    public void setFirstTopMargin(int firstTopMargin) {
        mFirstTopMargin = firstTopMargin;
    }


    @Override
    public void onShareReportSuccess(String postId) {
        if (!CollectionUtil.isEmpty(mPostBaseList)) {
            for (int i = 0; i < mPostBaseList.size(); i++) {
                PostBase postBase = mPostBaseList.get(i);
                if (TextUtils.equals(postBase.getPid(), postId)) {
                    postBase.setShareCount(postBase.getShareCount() + 1);
                    refreshShareCount(i, postBase);
                }
            }
        }
    }

    private void refreshShareCount(int position, PostBase postBase) {
        if (mRecyclerView == null) {
            return;
        }
        if (hasHeader()) {
            position++;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        View view = layoutManager.findViewByPosition(position);
        if (view != null) {
            TextView shareCount = view.findViewById(R.id.common_feed_share);
            if (shareCount != null) {
                if (postBase.getShareCount() > 0) {
                    shareCount.setText(String.valueOf(postBase.getShareCount()));
                } else {
                    shareCount.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_share"));
                }
            }
        }
    }

    @Override
    public void onShareReportFail(int errorCode) {
        //do nothing
    }

    @Override
    public List<PostBase> getData() {
        return getPostBaseList();
    }

    @Override
    public String getSource() {
        return getFeedSource();
    }
}
