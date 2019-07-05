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
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseFeedDetailClickListener;
import com.redefine.welike.business.feeds.management.ForwardPostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedDetailForwardAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailForwardContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailForwardViewHolder;
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.model.FeedFormentModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwb on 2018/1/12.
 * 详情页的转发栏
 */

public class FeedDetailForwardPresenter extends MvpFragmentPagePresenter<IFeedDetailForwardContract.IFeedDetailForwardView> implements IFeedDetailForwardContract.IFeedDetailForwardPresenter,
        ForwardPostsManager.ForwardPostsCallback, OnClickRetryListener, IBrowseFeedDetailClickListener {

    private final FeedDetailForwardAdapter mAdapter;
    private final IRefreshDelegate mRefreshDelegate;
    private final ForwardPostsManager mModel;
    private PostBase mPostBase;

    public FeedDetailForwardPresenter(IPageStackManager pageStackManager, IRefreshDelegate onRefreshDelegate, Bundle pageBundle) {
        super(pageStackManager, pageBundle);
        if (pageBundle != null) {
            mPostBase = (PostBase) pageBundle.getSerializable(FeedConstant.KEY_FEED);
        }

        mView.setPresenter(this);
        mAdapter = new FeedDetailForwardAdapter();
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final RecyclerView.ViewHolder viewHolder, Object t) {
                if (!(t instanceof PostBase)) {
                    return;
                }

                if (!AccountManager.getInstance().isLogin()) {
                    onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, true);
                    return;
                }
                final PostBase postBase = (PostBase) t;
                List<MenuItem> list = new ArrayList<>();
                String title = postBase.getNickName() + ": " + postBase.getSummary();
                list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_TITLE, title, false, viewHolder.itemView.getResources().getColor(R.color.common_menu_item_title_text_color)));
                list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_REPLY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_reply")));
                list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_FORWARD, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_forward")));
                list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_COPY, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "copy")));

                if (AccountManager.getInstance().isSelf(postBase.getUid())) {
                    list.add(new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                            , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm")
                            , viewHolder.itemView.getResources().getColor(R.color.common_menu_delete_text_color)));
                }
                SimpleMenuDialog.show(viewHolder.itemView.getContext(), list, new OnMenuItemClickListener() {
                    @Override
                    public void onMenuClick(MenuItem menuItem) {
                        if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPLY) {
                            //String draftId =EditorActivity.launchCommentActivity(viewHolder.itemView.getContext(), postBase);
                            PublishCommentStarter.INSTANCE.startActivityFromFeedDetail(viewHolder.itemView.getContext(), postBase);
                            EventLog.Feed.report7(16, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), postBase.getStrategy(), mPostBase.getSequenceId());
                        } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_FORWARD) {
//                            EditorActivity.launchForwardActivity(viewHolder.itemView.getContext(), postBase);
                            EventLog.Feed.report6(16, mPostBase.getPid(), PostEventManager.getPostType(mPostBase), postBase.getStrategy(), mPostBase.getSequenceId());
                            FeedFormentModel model = new FeedFormentModel(ShareEventHelper.convertPostType(postBase), "",
                                    FeedButtonFrom.POST_DETAIL, postBase.getPid(), postBase.getStrategy(), postBase.getOperationType(), postBase.getLanguage(), postBase.getTags(), postBase.getUid(), postBase.getSequenceId());


                            PublishForwardStarter.INSTANCE.startActivity4PostFromFeedDetail(viewHolder.itemView.getContext(), postBase);
                        } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_DELETE) {
                            mAdapter.doRealFeedDelete(postBase);
                        } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_COPY) {
                            if (viewHolder instanceof FeedDetailForwardViewHolder) {
                                RichTextClipboardManager.getInstance().copy(viewHolder.itemView.getContext(), ((FeedDetailForwardViewHolder) viewHolder).mFeedContent.getText());
                                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "copy_to_clipboard"));
                            }
                        }
                    }
                });
            }
        });

        if (!AccountManager.getInstance().isLogin()) {
            mAdapter.setiBrowseFeedDetailClickListener(this);
        }

        mAdapter.setRetryLoadMoreListener(this);
        mModel = new ForwardPostsManager();
        mRefreshDelegate = onRefreshDelegate;
        mModel.setListener(this);
    }

    @Override
    protected IFeedDetailForwardContract.IFeedDetailForwardView createFragmentPageView() {
        return IFeedDetailForwardContract.IFeedDetailForwardFeedFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mPostBase == null && savedInstanceState != null) {
            mPostBase = (PostBase) savedInstanceState.getSerializable(FeedConstant.KEY_FEED);
        }
        mView.setAdapter(mAdapter);
        mView.showLoading();
        mModel.tryRefresh(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public void destroy() {
        super.destroy();
        mAdapter.destroy();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mRefreshDelegate.setRefreshEnable(false);
        mAdapter.onLoadMore();
        mModel.tryHis(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public void onRefresh() {
        if (mAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        }
        mAdapter.hideFooter();
        mModel.tryRefresh(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public void onNewForward(PostBase postBase) {
        mAdapter.addNewData(postBase);
        mView.showContent();
    }

    @Override
    public void refreshPostBase(PostBase postBase) {
        mPostBase = postBase;
    }

    @Override
    public void onRefreshForwardPosts(ForwardPostsManager manager, List<PostBase> feeds, String forwardedFid, int errCode) {
        if (mModel == manager) {
            boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
            if (isSuccess) {
                mAdapter.addNewData(feeds);
                mAdapter.clearFinishFlag();
            }
            if (!CollectionUtil.isEmpty(feeds) || mAdapter.getRealItemCount() > 0) {
                mView.showContent();
            } else if (isSuccess) {
                mView.showEmptyView();
            } else {
                mView.showErrorView();
            }
            if (mRefreshDelegate != null) {
                mRefreshDelegate.stopRefresh();
            }
        }
        mAdapter.showFooter();
    }

    @Override
    public void onReceiveHisForwardPosts(ForwardPostsManager manager, List<PostBase> feeds, String forwardedFid, boolean last, int errCode) {
        if (mModel == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addHisData(feeds);
                if (last) {
                    mAdapter.noMore();
                } else {
                    mAdapter.finishLoadMore();
                }

            } else {
                mAdapter.loadError();
            }
        }
        mRefreshDelegate.setRefreshEnable(true);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onBrowseFeedDetailClick(int tye, boolean showDialog) {
        if (showDialog) {
            BrowseSchemeManager.getInstance().setPostDetail(mPostBase.getPid());
            StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
            StartEventManager.getInstance().setFrom_page(3);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            mView.showLoginSnackBar(tye);
        }
    }
}
