package com.redefine.welike.business.feeds.ui.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.page.FeedDetailCommentFragmentPage;
import com.redefine.welike.business.feeds.ui.page.FeedDetailForwardFragmentPage;
import com.redefine.welike.business.feeds.ui.page.FeedDetailLikeFragmentPage;
import com.redefine.welike.business.user.management.bean.User;

/**
 * Created by MR on 2018/1/19.
 */

public class FeedDetailPageSwitcher implements IRefreshDelegate {
    private PostBase mPostBase;
    private final IRefreshDelegate mDelegate;
    private final IPageStackManager mPageStackManager;
    private FeedDetailForwardFragmentPage mForwardFragment;
    private FeedDetailCommentFragmentPage mCommentFragment;
    private FeedDetailLikeFragmentPage mLikeFragment;

    private BaseFragmentPage mLastShowFragment;
    private int mLastIndex = -1;

    //    private boolean isBrowse;
    private boolean isArticle;

    public static final int FRAGMENT_FORWARD_POSITION = 0;
    public static final int FRAGMENT_COMMENT_POSITION = 1;
    public static final int FRAGMENT_LIKE_POSITION = 2;

    public FeedDetailPageSwitcher(IPageStackManager pm, PostBase mFeed, IRefreshDelegate delegate) {
        mPageStackManager = pm;
        mPostBase = mFeed;
        mDelegate = delegate;
    }


    public void setArticle(boolean article) {
        isArticle = article;
    }

    public void setCurrentItem(ViewGroup viewGroup, int index) {
        if (mLastIndex == index || viewGroup == null) {
            return;
        }
        mLastIndex = index;
        // Do we already have this fragment?
        BaseFragmentPage fragment = findFragmentByIndex(index);
        if (mLastShowFragment != null) {
            mLastShowFragment.detach(viewGroup);
        }
        if (fragment != null) {
            if (fragment.getView() == null) {
                fragment.createAndAttach(viewGroup);
            } else {
                fragment.attach(viewGroup);
            }
        } else {
            fragment = getFragmentByIndex(index);
            fragment.createAndAttach(viewGroup);
        }
        mLastShowFragment = fragment;
    }

    private BaseFragmentPage findFragmentByIndex(int index) {
        BaseFragmentPage fragment;
        switch (index) {
            case FRAGMENT_COMMENT_POSITION:
                fragment = mCommentFragment;
                break;
            case FRAGMENT_LIKE_POSITION:
                fragment = mLikeFragment;
                break;
            case FRAGMENT_FORWARD_POSITION:
            default:
                fragment = mForwardFragment;
                break;

        }
        return fragment;
    }

    private BaseFragmentPage getFragmentByIndex(int index) {
        BaseFragmentPage fragment;
        switch (index) {
            case FRAGMENT_COMMENT_POSITION:
                if (mCommentFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
                    if (isArticle)
                        bundle.putBoolean(FeedConstant.BUNDLE_KEY_IS_ARTICLE, isArticle);
                    mCommentFragment = new FeedDetailCommentFragmentPage(mDelegate, mPageStackManager, new PageConfig.Builder(FeedDetailCommentFragmentPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mCommentFragment;
                break;
            case FRAGMENT_LIKE_POSITION:
                if (mLikeFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
                    mLikeFragment = new FeedDetailLikeFragmentPage(mDelegate, mPageStackManager, new PageConfig.Builder(FeedDetailCommentFragmentPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mLikeFragment;
                break;
            case FRAGMENT_FORWARD_POSITION:
            default:
                if (mForwardFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
                    mForwardFragment = new FeedDetailForwardFragmentPage(mDelegate, mPageStackManager, new PageConfig.Builder(FeedDetailForwardFragmentPage.class).setPageBundle(bundle).build(), null);
                }
                fragment = mForwardFragment;
                break;

        }
        return fragment;
    }

    @Override
    public void startRefresh() {
        if (mLastShowFragment != null && mLastShowFragment instanceof IRefreshDelegate) {
            ((IRefreshDelegate) mLastShowFragment).startRefresh();
        }
    }

    @Override
    public void stopRefresh() {
        if (mDelegate != null) {
            mDelegate.stopRefresh();
        }
    }

    @Override
    public void setRefreshEnable(boolean isEnable) {
        if (mDelegate != null) {
            mDelegate.setRefreshEnable(isEnable);
        }
    }

    public void destroy(ViewGroup container) {
        if (mLikeFragment != null) {
            mLikeFragment.destroy(container);
        }
        if (mForwardFragment != null) {
            mForwardFragment.destroy(container);
        }
        if (mCommentFragment != null) {
            mCommentFragment.destroy(container);
        }
    }

    public void onNewComment(Comment comment) {
        if (mCommentFragment != null) {
            mCommentFragment.onNewComment(comment);
        }
    }

    public void onChangeCommentOrder(FeedDetailCommentHeadBean.CommentSortType sortType) {//setSwitchCommentOrder
        if (mCommentFragment != null) {
            mCommentFragment.setSwitchCommentOrder(sortType);
        }
    }


    public void onNewLike(User user, long superLikeExp) {
        if (mLikeFragment != null) {
            user.setSuperLikeExp(superLikeExp);
            mLikeFragment.onNewLike(user);
        }
    }

    public void onNewForward(PostBase postBase) {
        if (mForwardFragment != null) {
            mForwardFragment.onNewForward(postBase);
        }

    }

    public void refreshPost(PostBase postBase) {
        mPostBase = postBase;
        if (mCommentFragment != null) {
            mCommentFragment.refreshPostBase(postBase);
        }

        if (mForwardFragment != null) {
            mForwardFragment.refreshPostBase(postBase);
        }

        if (mLikeFragment != null) {
            mLikeFragment.refreshPostBase(postBase);
        }
    }
}
