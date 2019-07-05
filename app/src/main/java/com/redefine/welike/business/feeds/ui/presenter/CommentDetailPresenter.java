package com.redefine.welike.business.feeds.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.richtext.copy.RichTextClipboardManager;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.CommentDelegateManager;
import com.redefine.welike.business.feeds.management.CommentDetailManager;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.CommentDetailAdapterDelegate;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.ICommentDetailContract;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.feeds.ui.viewholder.CommentDetailMainViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.CommentDetailReplyViewHolder;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishReplyBackStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishReplyStarter;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/16.
 */

public class CommentDetailPresenter extends MvpTitlePagePresenter1<ICommentDetailContract.ICommentDetailView> implements ICommentDetailContract.ICommentDetailPresenter,
        CommentDetailManager.CommentDetailCallback, OnClickRetryListener, IFeedDetailCommentOpListener, CommentDetailReplyViewHolder.OnCommentLikeListener {
    private final CommentDetailAdapterDelegate mDelegateAdapter;
    private PostBase mPost;
    private Comment mComment;
    private final CommentDelegateManager mModel;
    private FeedDetailCommentHeadBean.CommentSortType mSortType = FeedDetailCommentHeadBean.CommentSortType.CREATED;

    public CommentDetailPresenter(Activity activity, Bundle pageBundle) {
        super(activity, pageBundle);
        if (pageBundle != null) {
            mComment = (Comment) pageBundle.getSerializable(FeedConstant.KEY_COMMENT);
            mPost = (PostBase) pageBundle.getSerializable(FeedConstant.KEY_FEED);
            mSortType = pageBundle.getBoolean(FeedConstant.KEY_COMMENT_TYPE) ? FeedDetailCommentHeadBean.CommentSortType.CREATED : FeedDetailCommentHeadBean.CommentSortType.HOT;
        }

        mDelegateAdapter = new CommentDetailAdapterDelegate(mSortType, this, this, this);
        mModel = new CommentDelegateManager(mSortType);
        mModel.getModel(FeedDetailCommentHeadBean.CommentSortType.CREATED).setListener(this);
        mModel.getModel(FeedDetailCommentHeadBean.CommentSortType.HOT).setListener(this);
    }

    @Override
    protected ICommentDetailContract.ICommentDetailView createPageView() {
        return ICommentDetailContract.CommentDetailFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (mComment == null) {
            mComment = (Comment) savedInstanceState.getSerializable(FeedConstant.KEY_COMMENT);
        }
        if (mPost == null) {
            mPost = (PostBase) savedInstanceState.getSerializable(FeedConstant.KEY_FEED);
        }
        if (mComment == null || mPost == null) {
            mView.showErrorView();
        } else {
            mView.setMainComment(mComment, mPost);
            mDelegateAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final RecyclerView.ViewHolder viewHolder, Object t) {
                    if (t instanceof Comment) {
                        final Comment comment = (Comment) t;
                        List<MenuItem> list = new ArrayList<>();
                        String title = ((Comment) t).getNickName() + ": " + comment.getContent();
                        list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_TITLE, title, false
                                , viewHolder.itemView.getResources().getColor(R.color.common_menu_item_title_text_color)));
                        list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_REPLY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_reply")));
                        list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_FORWARD, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_forward")));
                        list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_COPY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "copy")));
                        if (AccountManager.getInstance().isSelf((mPost.getUid())) || AccountManager.getInstance().isSelf(((Comment) t).getUid())) {
                            list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                                    , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm")
                                    , viewHolder.itemView.getResources().getColor(R.color.common_menu_delete_text_color)));
                        }
                        SimpleMenuDialog.show(viewHolder.itemView.getContext(), list, new OnMenuItemClickListener() {
                            @Override
                            public void onMenuClick(MenuItem menuItem) {
                                if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_FORWARD) {
                                    PublishForwardStarter.INSTANCE.startActivity4CommentFromCommentDetail(mActivity, mPost, comment);
                                    EventLog.Feed.report6(20, mPost.getPid(), PostEventManager.getPostType(mPost), mPost == null ? null : mPost.getStrategy(), mPost == null ? "" : mPost.getSequenceId());
                                    EventLog1.FeedForment.report2(PostType.COMMENT, EventConstants.FEED_PAGE_COMMENT_DETAIL, FeedButtonFrom.POST_DETAIL, mPost.getPid(),
                                            mPost.getStrategy(), mPost.getOperationType(), mPost.getLanguage(), mPost.getTags(), FeedHelper.getRootPostLanguage(mPost), FeedHelper.getRootPostTags(mPost), FeedHelper.getRootOrPostUid(mPost), mPost.getSequenceId(),mPost.getReclogs());
                                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPLY) {
                                    if (viewHolder instanceof CommentDetailMainViewHolder) {
                                        PublishReplyStarter.INSTANCE.startActivityFromCommentDetail(mActivity, comment);
                                    } else if (viewHolder instanceof CommentDetailReplyViewHolder) {
                                        PublishReplyBackStarter.INSTANCE.startActivityFromCommentDetail(mActivity, comment, mComment);
                                    }
                                    EventLog.Feed.report7(20, mPost.getPid(), PostEventManager.getPostType(mPost), mPost == null ? null : mPost.getStrategy(), mPost == null ? "" : mPost.getSequenceId());
                                    EventLog1.FeedForment.report1(PostType.COMMENT, EventConstants.FEED_PAGE_COMMENT_DETAIL, FeedButtonFrom.POST_DETAIL, mPost.getPid(),
                                            mPost.getStrategy(), mPost.getOperationType(), mPost.getLanguage(), mPost.getTags(), FeedHelper.getRootPostLanguage(mPost), FeedHelper.getRootPostTags(mPost), FeedHelper.getRootOrPostUid(mPost), mPost.getSequenceId(),mPost.getReclogs());
                                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_COPY) {
                                    if (viewHolder instanceof CommentDetailReplyViewHolder) {
                                        RichTextClipboardManager.getInstance().copy(viewHolder.itemView.getContext(), ((CommentDetailReplyViewHolder) viewHolder).mCommentContent.getText());
                                        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "copy_to_clipboard"));
                                    }
                                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_DELETE) {
                                    CommentsManager.deleteReply(comment.getCid());
                                    mDelegateAdapter.getAdapter().deleteItem(mDelegateAdapter.getAdapter().getRealPosition(viewHolder.getAdapterPosition()));
                                }
                            }
                        });

                    }
                }
            });
            mView.setAdapter(mDelegateAdapter.getAdapter());
            mView.setPresenter(this);
            onRefresh();
        }
    }

    @Override
    public boolean canLoadMore() {
        return mDelegateAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mDelegateAdapter.onLoadMore();
        // do load more data
        mModel.getModel().tryHis(mComment.getCid());
    }

    @Override
    public void doForwardComment(Context context, Comment comment) {
        PublishForwardStarter.INSTANCE.startActivity4CommentFromCommentDetail(mActivity, mPost, comment);
        EventLog.Feed.report6(18, mPost.getPid(), PostEventManager.getPostType(mPost), mPost.getStrategy(), mPost.getSequenceId());
        EventLog1.FeedForment.report1(PostType.COMMENT, EventConstants.FEED_PAGE_COMMENT_DETAIL, FeedButtonFrom.POST_DETAIL, mPost.getPid(),
                mPost.getStrategy(), mPost.getOperationType(), mPost.getLanguage(), mPost.getTags(), FeedHelper.getRootPostLanguage(mPost), FeedHelper.getRootPostTags(mPost), FeedHelper.getRootOrPostUid(mPost), mPost.getSequenceId(),mPost.getReclogs());
    }

    @Override
    public void doCommentReply(Context context, Comment comment) {
        PublishReplyStarter.INSTANCE.startPopUpActivityFromCommentDetail((AppCompatActivity) context, comment);
        EventLog.Feed.report7(18, mPost.getPid(), PostEventManager.getPostType(mPost), mPost.getStrategy(), mPost.getSequenceId());
        EventLog1.FeedForment.report2(PostType.COMMENT, EventConstants.FEED_PAGE_COMMENT_DETAIL, FeedButtonFrom.POST_DETAIL, mPost.getPid(),
                mPost.getStrategy(), mPost.getOperationType(), mPost.getLanguage(), mPost.getTags(), FeedHelper.getRootPostLanguage(mPost), FeedHelper.getRootPostTags(mPost), FeedHelper.getRootOrPostUid(mPost), mPost.getSequenceId(),mPost.getReclogs());
    }

    @Override
    public void onRefresh() {
        mView.showLoading();
        mModel.getModel().tryRefresh(mComment.getCid());
        mDelegateAdapter.onLoadMore();
    }

    @Override
    public void onNewMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
            Comment comment = (Comment) message.obj;
            if (comment == null) {
                return;
            }
            mDelegateAdapter.refreshComment(comment);
            mView.scrollToTop();
        }
    }

    @Override
    public void deleteMainComment() {
        CommentsManager.deleteComment(mComment.getCid());
    }

    @Override
    public void onRefreshCommentDetail(CommentDetailManager manager, List<Comment> replies, String cid, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            if (!CollectionUtil.isEmpty(replies)) {
                mDelegateAdapter.getAdapter().addHisData(replies);
            }
            mDelegateAdapter.getAdapter().finishLoadMore();
            mDelegateAdapter.getAdapter().goneLoadMore();
        } else {
            mDelegateAdapter.getAdapter().loadError();
        }
        if (!CollectionUtil.isEmpty(replies) || mDelegateAdapter.getAdapter().getRealItemCount() > 1) {
            mView.showContent();
        } else if (isSuccess) {
            mView.showEmptyView();
        } else {
            mView.showErrorView();
        }
    }

    @Override
    public void onReceiveCommentDetailHis(CommentDetailManager manager, List<Comment> replies, FeedDetailCommentHeadBean.CommentSortType sortType, String cid, boolean last, int errCode) {
        if (mModel.getModel() == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                if (!CollectionUtil.isEmpty(replies)) {
                    mDelegateAdapter.getAdapter(sortType).addHisData(replies);
                }
                if (last) {
                    mDelegateAdapter.getAdapter(sortType).noMore();
                } else {
                    mDelegateAdapter.getAdapter(sortType).finishLoadMore();
                }
            } else {
                mDelegateAdapter.getAdapter(sortType).loadError();
            }
        }
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onSwitchCommentOrder(FeedDetailCommentHeadBean.CommentSortType sortType) {
        if (mSortType == sortType) {
            return;
        }
        mSortType = sortType;
        mDelegateAdapter.setSortType(sortType);
        mModel.setSortType(sortType);
        mView.setAdapter(mDelegateAdapter.getAdapter());

        if (mDelegateAdapter.getRealItemCount() == 0) {
            onRefresh();
        } else {
            mView.showContent();
        }
    }

    @Override
    public void onCommentLike(Comment comment) {
        EventLog.Feed.report5(mComment.getPid(), 0, 1, 20, PostEventManager.getPostType(mPost), mPost.getStrategy(), mPost.getSequenceId());
        EventLog1.FeedLike.report1(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPost.getPid(), mPost.getUid(), mPost.getStrategy(),
                mPost.getOperationType(), mPost.getLanguage(), mPost.getTags(), FeedHelper.getRootOrPostUid(mPost), FeedHelper.getRootPostLanguage(mPost), FeedHelper.getRootPostTags(mPost), mPost.getSequenceId(),mPost.getReclogs());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
