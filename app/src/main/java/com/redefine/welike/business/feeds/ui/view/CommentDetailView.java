package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.CommentsManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.ICommentDetailContract;
import com.redefine.welike.business.feeds.ui.viewholder.CommentDetailMainViewHolder;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/16.
 */

public class CommentDetailView implements ICommentDetailContract.ICommentDetailView,
        ILoadMoreDelegate, View.OnClickListener, ErrorView.IErrorViewClickListener {

    private PostBase mPost;
    private Comment mComment;
    private RecyclerView mRecyclerView;
    private ICommentDetailContract.ICommentDetailPresenter mPresenter;
    private View mForwardBtn;
    private TextView mCommentBtn;
    private View mLikeBtn;
    private TextView mTitleView;
    private View mBackBtn;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private CommentDetailMainViewHolder mMainViewHolder;
    private View mRootView;
    private ImageView mMoreView;

    public CommentDetailView() {
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.comment_detail_fragment, null);
        mRecyclerView = mRootView.findViewById(R.id.comment_detail_recycler_view);
        mRootView.findViewById(R.id.comment_detail_title_view).setBackgroundResource(R.color.white);
        mTitleView = mRootView.findViewById(R.id.tv_common_title);
        mBackBtn = mRootView.findViewById(R.id.iv_common_back);
        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_detail"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mForwardBtn = mRootView.findViewById(R.id.comment_detail_bottom_forward);
        mCommentBtn = mRootView.findViewById(R.id.comment_detail_bottom_comment);
        mMoreView = mRootView.findViewById(R.id.iv_common_next);
        mLikeBtn = mRootView.findViewById(R.id.comment_detail_bottom_like);
        mEmptyView = mRootView.findViewById(R.id.common_empty_view);
        mErrorView = mRootView.findViewById(R.id.common_error_view);
        mLoadingView = mRootView.findViewById(R.id.common_loading_view);
        mErrorView.setOnErrorViewClickListener(this);
        mEmptyView.showEmptyText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_detail_no_reply"));
        mBackBtn.setOnClickListener(this);
        mForwardBtn.setOnClickListener(this);
        mCommentBtn.setOnClickListener(this);
        mMoreView.setOnClickListener(this);
        mMoreView.setImageResource(R.drawable.common_more_btn);
        mCommentBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_detail_input_placeholder"));
        mLikeBtn.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {

    }


    @Override
    public void setPresenter(ICommentDetailContract.ICommentDetailPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setAdapter(LoadMoreFooterRecyclerAdapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setMainComment(Comment comment, PostBase postBase) {
        mComment = comment;
        mPost = postBase;
        mMoreView.setVisibility(AccountManager.getInstance().isSelf(comment.getUid()) ? View.VISIBLE : View.GONE);
        mMainViewHolder = new CommentDetailMainViewHolder(mRootView.findViewById(R.id.comment_detail_main_comment_root), mPost);
        mMainViewHolder.bindViews(null, mComment);
        notifyLikeBtnChange();
    }

    @Override
    public void onClickLikeBtn() {
        if (mComment == null) {
            return;
        }
        if (!mComment.isLike()) {
            EventLog.Feed.report5(mComment.getCid(), 0, 1, 18, PostEventManager.getPostType(mPost), mPost.getStrategy(), mPost.getSequenceId());
            CommentsManager.likeComment(mComment.getCid());
            mComment.setLike(true);
            mComment.setLikeCount(mComment.getLikeCount() + 1);
        }
        notifyLikeBtnChange();
    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
    }

    @Override
    public void onClick(View v) {
        if (v == mForwardBtn) {
            if (mComment != null) {
                mPresenter.doForwardComment(v.getContext(), mComment);
            }
        } else if (v == mCommentBtn) {
            if (mComment != null) {
                mPresenter.doCommentReply(v.getContext(), mComment);
            }
        } else if (v == mLikeBtn) {
            onClickLikeBtn();

        } else if (v == mBackBtn) {
//            mPresenter.onBackPressed();
            mPresenter.onBackPressed();
        } else if (v == mMoreView) {
            MenuItem menuItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_DELETE
                    , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm")
                    , v.getResources().getColor(R.color.common_menu_delete_text_color));
            List<MenuItem> list = new ArrayList<>();
            list.add(menuItem);
            SimpleMenuDialog.show(v.getContext(), list, new OnMenuItemClickListener() {
                @Override
                public void onMenuClick(MenuItem menuItem) {
                    if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_DELETE) {
                        mPresenter.deleteMainComment();
                        mPresenter.onBackPressed();
                    }
                }
            });
        }
    }


    private void notifyLikeBtnChange() {
        if (mComment == null) {
            return;
        }
        mLikeBtn.setSelected(mComment.isLike());
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
    }
}
