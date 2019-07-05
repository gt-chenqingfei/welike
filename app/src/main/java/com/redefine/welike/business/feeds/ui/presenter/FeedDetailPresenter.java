package com.redefine.welike.business.feeds.ui.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUserCallBack;
import com.redefine.welike.business.browse.management.dao.FollowUserCountCallBack;
import com.redefine.welike.business.browse.management.request.ReportRequest;
import com.redefine.welike.business.browse.management.request.ReportRequest1;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailContract;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;

import org.jetbrains.annotations.NotNull;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwb on 2018/1/11.
 */

public class FeedDetailPresenter extends MvpTitlePagePresenter<IFeedDetailContract.IFeedDetailView> implements IFeedDetailContract.IFeedDetailPresenter, SinglePostManager.PostDetailCallback,
        SinglePostManager.PostDeleteListener, BlockUserManager.BlockUserCallback, FollowUserManager.FollowUserCallback {
    private String mPostId;
    private PostBase mPostBase;
    private int mIndex;
//    private boolean isBrowse = false;

    private long beginTime = 0;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public FeedDetailPresenter(IPageStackManager pageStackManager, Bundle pageBundle) {
        super(pageStackManager, pageBundle);
    }

    @Override
    protected IFeedDetailContract.IFeedDetailView createPageView() {
        return IFeedDetailContract.FeedDetailFactory.createView(mPageStackManager, mPageBundle);
    }

    @Override
    protected void initView(Bundle saveState) {
        parseBundle(mPageBundle, saveState);
        mView.setPresenter(this);
        beginTime = System.currentTimeMillis();
        if (mPostBase != null) {
            applyPostToView(mPostBase);
            if (!AccountManager.getInstance().isLogin())
                SinglePostManager.getInstance().reqPostDetail4H5(mPostBase.getPid(), this);
            else SinglePostManager.getInstance().reqPostDetail(mPostBase.getPid(), this);
        } else if (!TextUtils.isEmpty(mPostId)) {
            loadPostByPid(mPostId);
        } else {
            mView.showErrorView();
        }
    }

    private void loadPostByPid(String postId) {
        mView.showLoadingView();
        if (!AccountManager.getInstance().isLogin())
            SinglePostManager.getInstance().reqPostDetail4H5(postId, this);
        else SinglePostManager.getInstance().reqPostDetail(postId, this);
    }


    private void updatePostToView(PostBase post) {

        if (mPostBase != null) {
            boolean isRePost = mPostBase.isRePost();
            mPostBase = post;
            mPostBase.setRePost(isRePost);
        } else
            mPostBase = post;
        if (mView.isShowContent()) {
            mView.updatePostToView(mPostBase);
        } else {
//            mView.initViewDetail(mPostBase, mIndex);
//            SinglePostManager.getInstance().register(this);
            applyPostToView(mPostBase);
        }
        ExposeEventReporter.INSTANCE.reportPostDetailExpose(mPostBase);
    }

    private void applyPostToView(PostBase post) {
        mPostBase = post;
        mView.initViewDetail(mPostBase, mIndex);
        SinglePostManager.getInstance().register(this);
        BlockUserManager.getInstance().register(this);
        FollowUserManager.getInstance().register(this);
    }

    private void parseBundle(Bundle pageBundle, Bundle saveState) {
        if (pageBundle != null) {
            mPostBase = (PostBase) pageBundle.getSerializable(FeedConstant.KEY_FEED);
            mPostId = pageBundle.getString(FeedConstant.KEY_FEED_ID);
            mIndex = pageBundle.getInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        }

        if (mPostBase != null) {
            mPostId = mPostBase.getPid();
        }

        if (mPostBase == null && saveState != null) {
            mPostBase = (PostBase) saveState.getSerializable(FeedConstant.KEY_FEED);
        }

        if (mPostBase == null && TextUtils.isEmpty(mPostId) && saveState != null) {
            mPostId = saveState.getString(FeedConstant.KEY_FEED_ID);
        }

        if (mIndex == FeedConstant.ERROR_INDEX && saveState != null) {
            mIndex = saveState.getInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        }
    }

    @Override
    public void destroy() {
        SinglePostManager.getInstance().unregister(this);
        BlockUserManager.getInstance().unregister(this);
        FollowUserManager.getInstance().unregister(this);
        super.destroy();
    }

    @Override
    public void onCommentFeed(Context context, PostBase feed) {
        if (feed != null) {
            EventLog.Feed.report7(15, mPostId, PostEventManager.getPostType(mPostBase), feed.getStrategy(), feed.getSequenceId());
            //String draftId = EditorActivity.launchCommentActivity(context, feed, isBrowse, false, model);
            EventLog1.FeedForment.report1(ShareEventHelper.convertPostType(feed), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, feed.getPid(),
                    feed.getStrategy(), feed.getOperationType(), feed.getLanguage(), feed.getTags(), FeedHelper.getRootPostLanguage(feed), FeedHelper.getRootPostTags(feed), FeedHelper.getRootOrPostUid(feed), feed.getSequenceId(),feed.getReclogs());
        }
        PublishCommentStarter.INSTANCE.startPopupActivityFromFeedDetail((AppCompatActivity) context, feed);
        if (!AccountManager.getInstance().isLogin()) {
            EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_POST_DETAIL);
        }
    }

    @Override
    public void onCommentFeed(Context context, PostBase feed, boolean showEmoji) {
        if (feed != null) {
            EventLog.Feed.report7(15, mPostId, PostEventManager.getPostType(mPostBase), feed.getStrategy(), feed.getSequenceId());

            //String draftId = EditorActivity.launchCommentActivity(context, feed, isBrowse, showEmoji, model);
            EventLog1.FeedForment.report1(ShareEventHelper.convertPostType(feed), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, feed.getPid(),
                    feed.getStrategy(), feed.getOperationType(), feed.getLanguage(), feed.getTags(), FeedHelper.getRootPostLanguage(feed), FeedHelper.getRootPostTags(feed), FeedHelper.getRootOrPostUid(feed), feed.getSequenceId(),feed.getReclogs());
        }
        PublishCommentStarter.INSTANCE.startActivityFromEmoji(context, feed);

        EventLog.Feed.report7(15, mPostId, PostEventManager.getPostType(mPostBase), feed.getStrategy(), mPostBase.getSequenceId());
        if (!AccountManager.getInstance().isLogin()) {
            EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_POST_DETAIL);
        }
    }

    @Override
    public void onForwardFeed(Context context, PostBase feed) {
        EventLog.Feed.report6(15, mPostId, PostEventManager.getPostType(mPostBase), feed.getStrategy(), mPostBase.getSequenceId());

        EventLog1.FeedForment.report2(ShareEventHelper.convertPostType(feed), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, feed.getPid(),
                feed.getStrategy(), feed.getOperationType(), feed.getLanguage(), feed.getTags(), FeedHelper.getRootPostLanguage(feed), FeedHelper.getRootPostTags(feed), FeedHelper.getRootOrPostUid(feed), feed.getSequenceId(),feed.getReclogs());
        PublishForwardStarter.INSTANCE.startActivity4PostFromFeedDetail(context, feed);
    }

    @Override
    public void onReport(Context context, String pid, String uid, String reason) {
        try {
            RequestCallback requestCallback = new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_sms_code_toast"));
                        }
                    });
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_success_toast"));
                        }
                    });
                }
            };
            if (!AccountManager.getInstance().isLogin()) {
                new ReportRequest1(context).request(pid, uid, reason, requestCallback);
            } else {
                new ReportRequest(context).request(pid, uid, reason, requestCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        return mPageBundle;
    }

    @Override
    public void onNewMessage(Message message) {
        if (message == null) {
            return;
        }

        if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
            if (mPostBase == null || !mView.isShowContent()) {
                return;
            }
            Comment comment = (Comment) message.obj;
            if (comment == null) {
                return;
            }
            if (!TextUtils.equals(comment.getPid(), mPostId)) {
                return;
            }
            mView.onNewComment(comment);
        } else if (message.what == MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH) {
            if (mPostBase == null || !mView.isShowContent()) {
                return;
            }
            PostBase postBase = (PostBase) message.obj;
            if (postBase == null || TextUtils.isEmpty(mPostId) || !(postBase instanceof ForwardPost)) {
                return;
            }
            // 其实无法知道此post是转发的我
            mView.onNewForward(postBase);
        }
    }

    @Override
    public void onClickErrorView() {
        loadPostByPid(mPostId);
    }

    @Override
    public void onPostDetailSuccessed(PostBase post) {
        if (post == null) {
            mView.showErrorView();
        } else {
            updatePostToView(post);
        }
    }

    @Override
    public void onPostDeleted(String pid) {
        if (TextUtils.equals(pid, mPostId)) {
            mView.onPostDelete();
        }
    }

    @Override
    public void onPostDetailFailed(int errCode) {
        mView.showErrorView();
    }

    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS &&
                mPostBase != null &&
                TextUtils.equals(uid, mPostBase.getUid())) {
            mPostBase.setBlock(true);
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS &&
                mPostBase != null &&
                TextUtils.equals(uid, mPostBase.getUid())) {
            mPostBase.setBlock(false);
        }
    }


    @Override
    public void onBrowseFeedDetailClick(int tye, boolean isShow, int showType) {
        BrowseSchemeManager.getInstance().setPostDetail(mPostId);
        if (isShow) {
            StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
            StartEventManager.getInstance().setFrom_page(3);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            mView.showLoginSnackBar(tye);
        }
    }


    @Override
    public void onActivityResume() {
        beginTime = System.currentTimeMillis();
        mView.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();

        if (mPostBase != null) {
            ExposeEventReporter.INSTANCE.reportPostDetailView(mPostBase, System.currentTimeMillis() - beginTime);
        }

    }

    @Override
    public void onActivityDestroy() {
        mView.onActivityDestroy();
    }

    @Override
    public void onFollowUser(PostBase postBase, final Context context) {
        BrowseEventStore.INSTANCE.getFollowUserCount(new FollowUser(postBase.getUid(), postBase.getNickName()), new FollowUserCountCallBack() {
            @Override
            public void onLoadEntity(boolean inserted, final int count) {
                if (inserted) {
                    if (count % 3 == 1) {
                        onMainThread(context, true, true);
                    } else {
                        onMainThread(context, true, false);
                    }
                } else {
                    onMainThread(context, false, true);
                }
            }
        });
    }


    @Override
    public void checkShowFollowBtn(final Context context, PostBase postBase) {
        BrowseEventStore.INSTANCE.getFollowUser(postBase.getUid(), new FollowUserCallBack() {
            @Override
            public void onLoadEntity(@NotNull FollowUser user) {
                if (user != null) {
                    onMainThread(context, true, false);
                } else {
                    onMainThread(context, false, false);
                }
            }
        });
    }

    private void onMainThread(final Context context, final boolean isHide, final boolean isShowSalfLogin) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (isHide) {
                    mView.refreshFollow(true);
                }
                if (isShowSalfLogin) {
                    HalfLoginManager.getInstancce().showLoginDialog(context, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                }

            }
        });
    }

    @Override
    public void initFeedFollowBtn(PostBase postBase, UserFollowBtn userFollowBtn) {
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(userFollowBtn, false);
        mFollowBtnPresenter.bindView(postBase.getUid(), postBase.isFollowing(), false, new FollowEventModel(EventConstants.FEED_PAGE_POST_DETAIL, postBase));
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        mPageStackManager.getActivity().onBackPressed();

    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (mPostBase == null || !TextUtils.equals(uid, mPostBase.getUid())) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mView.refreshFollow(true);
        } else {
            mView.refreshFollow(false);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (mPostBase == null || !TextUtils.equals(uid, mPostBase.getUid())) {
            return;
        }
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mView.refreshFollow(true);
        } else {
            mView.refreshFollow(false);
        }
    }
}
