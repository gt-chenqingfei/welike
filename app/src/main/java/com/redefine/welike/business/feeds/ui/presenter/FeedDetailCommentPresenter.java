package com.redefine.welike.business.feeds.ui.presenter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpFragmentPagePresenter;
import com.redefine.richtext.copy.RichTextClipboardManager;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.feeds.management.CommentsDelegateManager;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedDetailCommentAdapterDelegate;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailCommentContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentViewHolder;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishReplyStarter;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.common.BrowseSchemeManager;
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
 * Created by liwb on 2018/1/12.
 */

public class FeedDetailCommentPresenter extends MvpFragmentPagePresenter<IFeedDetailCommentContract.IFeedDetailCommentView> implements IFeedDetailCommentContract.IFeedDetailCommentPresenter
        , CommentsManager.CommentsProviderCallback, OnClickRetryListener, FeedDetailCommentViewHolder.OnCommentOperationListener
        , IFeedDetailCommentOpListener
        , IBrowseFeedDetailClickListener {

    private final IRefreshDelegate mRefreshDelegate;
    private final FeedDetailCommentAdapterDelegate mDelegateAdapter;
    private final CommentsDelegateManager mModel;
    private FeedDetailCommentHeadBean.CommentSortType mSortType;
    private PostBase mPostBase;
    private boolean isArticle = false;

    public FeedDetailCommentPresenter(IPageStackManager manager, IRefreshDelegate delegate, Bundle pageBundle) {
        super(manager, pageBundle);
        if (pageBundle != null) {
            mPostBase = (PostBase) pageBundle.getSerializable(FeedConstant.KEY_FEED);
            if (pageBundle.containsKey(FeedConstant.BUNDLE_KEY_IS_ARTICLE))
                isArticle = pageBundle.getBoolean(FeedConstant.BUNDLE_KEY_IS_ARTICLE, false);

        }
        // ShareSp 存储用户的排序方式 没有存储的情况下 直接判断postBase的commentCount > 10 Top
        int commentSortValue = SpManager.Setting.getCommentSortValue(MyApplication.getAppContext());
        if (commentSortValue == -1) {
            mSortType = mPostBase.getCommentCount() >= FeedConstant.COMMENT_COUNT ?
                    FeedDetailCommentHeadBean.CommentSortType.HOT : FeedDetailCommentHeadBean.CommentSortType.CREATED;
        } else {
            switch (commentSortValue) {
                case FeedConstant.COMMENT_SORT_TOP:
                    mSortType = FeedDetailCommentHeadBean.CommentSortType.HOT;
                    break;
                case FeedConstant.COMMENT_SORT_LATEST:
                    mSortType = FeedDetailCommentHeadBean.CommentSortType.CREATED;
                    break;
            }
        }

        if (isArticle)
            mDelegateAdapter = new FeedDetailCommentAdapterDelegate(mSortType, this, this);
        else mDelegateAdapter = new FeedDetailCommentAdapterDelegate(mSortType, this);
        mModel = new CommentsDelegateManager(mSortType, this);
        mRefreshDelegate = delegate;
        mDelegateAdapter.setOnCommentOperationListener(this);
        if (!AccountManager.getInstance().isLogin())
            mDelegateAdapter.setBrowseFeedDetailClickListener(this);
        mDelegateAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final RecyclerView.ViewHolder viewHolder, final Object t) {


                if (t instanceof Comment) {

                    if (!AccountManager.getInstance().isLogin()) {
                        onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, true);
                        return;
                    }

                    List<MenuItem> list = new ArrayList<>();

                    String title = ((Comment) t).getNickName() + ": " + ((Comment) t).getContent();
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_TITLE, title, false, viewHolder.itemView.getResources().getColor(R.color.common_menu_item_title_text_color)));
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_REPLY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_reply")));
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_FORWARD, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_forward")));
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_COPY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "copy")));
                    if (AccountManager.getInstance().isSelf((mPostBase).getUid()) || AccountManager.getInstance().isSelf(((Comment) t).getUid())) {
                        list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                                , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm")
                                , viewHolder.itemView.getResources().getColor(R.color.common_menu_delete_text_color)));
                    }
                    SimpleMenuDialog.show(viewHolder.itemView.getContext(), list, new OnMenuItemClickListener() {
                        @Override
                        public void onMenuClick(MenuItem menuItem) {
                            if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_FORWARD) {
                                PublishForwardStarter.INSTANCE.startActivity4CommentFromFeedDetail(viewHolder.itemView.getContext(), mPostBase, (Comment) t);
                                EventLog.Feed.report6(17, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), mPostBase == null ? null : mPostBase.getStrategy(), mPostBase.getSequenceId());
                                EventLog1.FeedForment.report2(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL_COMMENT, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(),
                                        mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                            } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPLY) {
                                PublishReplyStarter.INSTANCE.startActivityFromFeedDetail(viewHolder.itemView.getContext(), (Comment) t);
                                EventLog.Feed.report7(17, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), mPostBase == null ? null : mPostBase.getStrategy(), mPostBase.getSequenceId());
                                EventLog1.FeedForment.report1(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL_COMMENT, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(),
                                        mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                            } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_COPY) {
                                if (viewHolder instanceof FeedDetailCommentViewHolder) {
                                    RichTextClipboardManager.getInstance().copy(viewHolder.itemView.getContext(), ((FeedDetailCommentViewHolder) viewHolder).mCommentContent.getText());
                                    ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "copy_to_clipboard"));
                                }
                            } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_DELETE) {
                                CommentsManager.deleteComment(((Comment) t).getCid());
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected IFeedDetailCommentContract.IFeedDetailCommentView createFragmentPageView() {
        return IFeedDetailCommentContract.FeedDetailCommentFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mPostBase == null && savedInstanceState != null) {
            mPostBase = (PostBase) savedInstanceState.getSerializable(FeedConstant.KEY_FEED);
        }
        mDelegateAdapter.setFeedBase(mPostBase);
        mView.setPresenter(this);
        mView.setAdapter(mDelegateAdapter.getAdapter());
        mView.showLoading();
        mModel.tryRefresh(mPostBase.getPid());
    }

    @Override
    public void onRefreshComments(CommentsManager manager, FeedDetailCommentHeadBean.CommentSortType sortType, List<Comment> comments, String fid, int errCode) {

        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            mDelegateAdapter.getAdapter(sortType).addNewData(comments);
            mDelegateAdapter.getAdapter(sortType).clearFinishFlag();
        }

        if (!CollectionUtil.isEmpty(comments) || mDelegateAdapter.getAdapter(sortType).getRealItemCount() > 0) {
            mView.showContent();
        } else if (isSuccess) {
            mView.showEmptyView();
        } else {
            mView.showErrorView();
        }
        if (mRefreshDelegate != null) {
            mRefreshDelegate.stopRefresh();
        }
        mDelegateAdapter.getAdapter(sortType).showFooter();
    }

    @Override
    public void onReceiveHisComments(CommentsManager manager, FeedDetailCommentHeadBean.CommentSortType sortType, List<Comment> comments, String fid, boolean last, int errCode) {

        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mDelegateAdapter.getAdapter(sortType).addHisData(comments);
            if (last) {
                mDelegateAdapter.getAdapter(sortType).noMore();
            } else {
                mDelegateAdapter.getAdapter(sortType).finishLoadMore();
            }

        } else {
            mDelegateAdapter.getAdapter(sortType).loadError();
        }
        mRefreshDelegate.setRefreshEnable(true);
    }

    @Override
    public boolean canLoadMore() {
        return mDelegateAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mRefreshDelegate.setRefreshEnable(false);
        mDelegateAdapter.onLoadMore();
        mModel.tryHis(mPostBase.getPid());
    }

    @Override
    public void onRefresh() {
        if (mDelegateAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        }
        mDelegateAdapter.hideFooter();
        mModel.tryRefresh(mPostBase.getPid());
    }

    @Override
    public void onNewComment(Comment comment) {
        mView.showContent();
        mDelegateAdapter.refreshComment(comment);
        mView.scrollToTop();
    }

    @Override
    public void deleteComment(String value) {
        mDelegateAdapter.deleteComment(value);
    }

    @Override
    public void refreshPostBase(PostBase postBase) {
        mPostBase = postBase;
        mDelegateAdapter.setFeedBase(postBase);
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
    public void onCommentReply(Comment comment) {
        EventLog.Feed.report7(17, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), mPostBase.getStrategy(), mPostBase.getSequenceId());
        EventLog1.FeedForment.report1(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL_COMMENT, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(),
                mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
    }

    @Override
    public void onCommentForward(Comment comment) {
        EventLog.Feed.report6(17, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), mPostBase.getStrategy(), mPostBase.getSequenceId());
        EventLog1.FeedForment.report2(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL_COMMENT, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(),
                mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
    }

    @Override
    public void onCommentLike(Comment comment) {
        EventLog.Feed.report5(mPostBase.getPid(), 0, 1, 17, PostEventManager.getPostType(mPostBase), mPostBase.getStrategy(), mPostBase.getSequenceId());
        EventLog1.FeedLike.report1(PostType.COMMENT, EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(), mPostBase.getUid(), mPostBase.getStrategy(),
                mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootOrPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
    }

    @Override
    public void onBrowseFeedDetailClick(int tye, boolean showLoginDialog) {
        if (showLoginDialog) {
            BrowseSchemeManager.getInstance().setPostDetail(mPostBase.getPid());
            StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
            StartEventManager.getInstance().setFrom_page(3);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            mView.showLoginSnackBar(tye);
        }
    }
}
