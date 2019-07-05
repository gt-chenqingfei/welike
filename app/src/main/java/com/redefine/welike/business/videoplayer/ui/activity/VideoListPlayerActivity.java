package com.redefine.welike.business.videoplayer.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.DownloadStateEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.DefaultShareMenuManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.VideoPlayAction;
import com.redefine.multimedia.player.VideoStateHelper;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.player.mediacontroller.ScreenOrientationManager;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommand;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommandId;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.richtext.RichItem;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.InsertLikeCallBack;
import com.redefine.welike.business.browse.management.request.BrowseLikeRequest;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.business.videoplayer.management.VideoPagerItemCalculator;
import com.redefine.welike.business.videoplayer.management.VideoPlayerConstants;
import com.redefine.welike.business.videoplayer.management.WelikeAudioManager;
import com.redefine.welike.business.videoplayer.management.command.CommandId;
import com.redefine.welike.business.videoplayer.management.command.CommandInvoker;
import com.redefine.welike.business.videoplayer.management.command.PostCommand;
import com.redefine.welike.business.videoplayer.management.command.VideoCommand;
import com.redefine.welike.business.videoplayer.management.util.ShowGuideUtil;
import com.redefine.welike.business.videoplayer.management.util.VideoPostHelper;
import com.redefine.welike.business.videoplayer.ui.adapter.VideoListAdapter;
import com.redefine.welike.business.videoplayer.ui.view.PostController;
import com.redefine.welike.business.videoplayer.ui.vm.VideoListPlayerViewModel;
import com.redefine.welike.common.WindowUtil;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareManagerWrapper;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by nianguowang on 2018/8/7
 */
public class VideoListPlayerActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private ErrorView mErrorView;
    private View mContentView;
    private RecyclerView mRecyclerView;
    private LottieAnimationView mLottieAnimationView;
    private TextView mGuideHint;
    private View mGuideContainer;
    private boolean mAuth;
    private VideoListAdapter mAdapter;
    private ScreenOrientationManager mScreenOrientationManager;
    private VideoListPlayerViewModel mVideoListViewModel;
    private VideoPost mCurrentDownloadVideo;
    private PostController mCurrentDownloadPostController;
    private VideoPost mCurrentPlayingVideo;
    private VideoStateHelper mVideoStateHelper;
    private int mSeekPosition;
    private String mSource;
    private String mVideoListType = VideoPlayerConstants.VIDEO_LIST_TYPE_PROFILE;
    private int mStartPosition;
    private LinearLayoutManager layoutManager;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        WindowUtil.INSTANCE.setFullScreen(this);
        mContentView = findViewById(R.id.video_content);
        mRecyclerView = findViewById(R.id.video_recycler_view);
        mGuideContainer = findViewById(R.id.video_guide_container);
        mLottieAnimationView = findViewById(R.id.video_guide);
        mGuideHint = findViewById(R.id.video_guide_hint);
        mErrorView = findViewById(R.id.common_error_view);

        Intent intent = getIntent();
        if (intent == null) {
            showErrorView();
            return;
        }
        mAuth = AccountManager.getInstance().isLogin();
        mSeekPosition = intent.getIntExtra(VideoPlayerConstants.INTENT_EXTRA_SEEK, 0);

        mSource = intent.getStringExtra(VideoPlayerConstants.INTENT_EXTRA_SOURCE);
        String videoListType = intent.getStringExtra(VideoPlayerConstants.INTENT_EXTRA_TYPE);
        if (!TextUtils.isEmpty(videoListType)) {
            mVideoListType = videoListType;
        }
        List<PostBase> postBases = (List<PostBase>) intent.getSerializableExtra(VideoPlayerConstants.INTENT_EXTRA_POST);
        mStartPosition = intent.getIntExtra(VideoPlayerConstants.INTENT_EXTRA_POSITION, 0);
        List<VideoPost> videoPosts = VideoPostHelper.filterVideoPost(postBases);
        if (CollectionUtil.isEmpty(videoPosts) || mStartPosition >= CollectionUtil.getCount(videoPosts)) {
            showErrorView();
            return;
        } else {
            mCurrentPlayingVideo = videoPosts.get(mStartPosition);
        }

        showContent();
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mAdapter = new VideoListAdapter(mPostCommandInvoker, mVideoCommandInvoker, mAuth);
        mAdapter.setOrientation(getResources().getConfiguration().orientation);
        mAdapter.addNewVideos(videoPosts);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mVideoCalculator);
        mPostViewTimeManager.attach(mRecyclerView, mAdapter, EventConstants.FEED_PAGE_VIDEO_PLAYER);

        mVideoStateHelper = new VideoStateHelper();
        mVideoStateHelper.mOpenType = 1;
        initVideoStateHelper();
        mScreenOrientationManager = new ScreenOrientationManager(this);

        mVideoListViewModel = ViewModelProviders.of(this).get(VideoListPlayerViewModel.class);
        mVideoListViewModel.init(videoPosts, mCurrentPlayingVideo, mAuth, mVideoListType);
        mVideoListViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

            }
        });
        mVideoListViewModel.getVideoPostLiveData().observe(this, new Observer<List<VideoPost>>() {
            @Override
            public void onChanged(@Nullable List<VideoPost> videoPosts) {
                mAdapter.addNewVideos(videoPosts);
                if (CollectionUtil.getCount(videoPosts) > 1 && ShowGuideUtil.shouldShowGuide(getApplicationContext())) {
                    mGuideContainer.setVisibility(View.VISIBLE);
                    mGuideHint.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "video_guide_hint"));
                    mLottieAnimationView.setAnimation("video_guide.json");
                    mLottieAnimationView.setRepeatCount(1);
                    mLottieAnimationView.playAnimation();
                } else {
                    mGuideContainer.setVisibility(View.GONE);
                }
                mVideoCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
            }
        });
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mStartPosition);
            }
        });
        mVideoListViewModel.getDownloadStatus().observe(this, new Observer<DownloadStateEnum>() {
            @Override
            public void onChanged(@Nullable DownloadStateEnum downloadStateEnum) {
                if (downloadStateEnum == DownloadStateEnum.CANCELED ||
                        downloadStateEnum == DownloadStateEnum.ERROR ||
                        downloadStateEnum == DownloadStateEnum.SUCCESS) {
                    mCurrentDownloadVideo = null;
                    mCurrentDownloadPostController = null;
                }
            }
        });
        mVideoListViewModel.getDownloadProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (mCurrentDownloadVideo != null) {
                    mCurrentDownloadVideo.setDownloadProgress(integer);
                    if (mCurrentDownloadPostController != null) {
                        mCurrentDownloadPostController.bindPost(mCurrentDownloadVideo);
                    }
                }
            }
        });
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mGuideContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mGuideContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mGuideContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mGuideContainer.setVisibility(View.GONE);
                }
                return true;
            }
        });
        EventBus.getDefault().register(this);
        WelikeAudioManager.INSTANCE.requestAudioFocus(this, mAfChangeListener);
    }

    @Override
    protected boolean isFullScreenActivity() {
        return true;
    }

    private void showErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.INVISIBLE);
    }

    private void showContent() {
        mErrorView.setVisibility(View.INVISIBLE);
        mContentView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScreenOrientationManager != null) {
            mScreenOrientationManager.disable();
        }
        if (mAdapter != null) {
            mAdapter.onPause();
        }
        mPostViewTimeManager.onDetach();
        mPostViewTimeManager.onHide();
        mPostViewTimeManager.onPause();
        WelikeAudioManager.INSTANCE.abandonAudioFocus(this, mAfChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScreenOrientationManager != null) {
            mScreenOrientationManager.enable();
        }
        if (mAdapter != null) {
            mAdapter.onResume();
        }
        mPostViewTimeManager.onAttach();
        mPostViewTimeManager.onShow();
        mPostViewTimeManager.onResume();
        WelikeAudioManager.INSTANCE.requestAudioFocus(this, mAfChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mScreenOrientationManager != null) {
            mScreenOrientationManager.onDestroy();
        }
        if (mLottieAnimationView != null) {
            mLottieAnimationView.clearAnimation();
        }
        if (mAdapter != null) {
            mAdapter.destroy();
        }
        if (mVideoStateHelper != null) {
            mVideoStateHelper.postLog(VideoStateHelper.LOG_TYPE_DESTROY_PAGE);
        }
        mPostViewTimeManager.onDestroy();
        WelikeAudioManager.INSTANCE.abandonAudioFocus(this, mAfChangeListener);
    }

    private void initVideoStateHelper() {
        mVideoStateHelper.mPostUid = mCurrentPlayingVideo.getUid();
        mVideoStateHelper.mRootPostUid = mCurrentPlayingVideo.getUid();
        mVideoStateHelper.mPostId = mCurrentPlayingVideo.getPid();
        mVideoStateHelper.mRootPostId = mCurrentPlayingVideo.getPid();
        mVideoStateHelper.mSequenceId = mCurrentPlayingVideo.getSequenceId();
        mVideoStateHelper.mPlayerSource = mSource;
        mVideoStateHelper.mPostUserHeader = mCurrentPlayingVideo.getHeadUrl();
        mVideoStateHelper.mPostUserNick = mCurrentPlayingVideo.getNickName();
        mVideoStateHelper.mVideoPath = mCurrentPlayingVideo.getVideoUrl();
        mVideoStateHelper.mVideoSource = TextUtils.equals(mCurrentPlayingVideo.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE) ? 1 : 0;
        mVideoStateHelper.mStrategy = mCurrentPlayingVideo.getStrategy();
        mVideoStateHelper.mPostLa = mCurrentPlayingVideo.getLanguage();
        mVideoStateHelper.mPostTags = mCurrentPlayingVideo.getTags();
        mVideoStateHelper.mMuteType = true;
    }

    private VideoPagerItemCalculator mVideoCalculator = new VideoPagerItemCalculator(new VideoPagerItemCalculator.VideoPlayStateCallback() {
        @Override
        public void startLoadMore() {
            mVideoListViewModel.loadMore();
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView, int scrollDirection) {
            VideoPost videoPost = mAdapter.playVideo(position, videoPlayerView);
            if (mSeekPosition > 0) {
                mAdapter.seekPosition(mSeekPosition);
                mSeekPosition = 0;
            }
            if (mCurrentPlayingVideo != videoPost) {
                mVideoStateHelper.postLog(VideoStateHelper.LOG_TYPE_SWITCH);
                mVideoStateHelper.resetLog(true);
                mVideoStateHelper.mOpenType = scrollDirection;
                mCurrentPlayingVideo = videoPost;
                initVideoStateHelper();

                if (!videoPost.isFollowing()) {
                    EventLog1.Follow.report3(EventConstants.FEED_PAGE_VIDEO_PLAYER, videoPost.getUid(), videoPost.getPid(), videoPost.getStrategy(), videoPost.getOperationType(), videoPost.getLanguage(), videoPost.getTags());
                }
            }
        }
    }, new MediaPlayerInvoker() {
        @Override
        public void doCommand(MediaPlayerCommand command) {
            if (command == null) {
                return;
            }
            if (command.cid == MediaPlayerCommandId.ON_PREPARED) {
                mVideoStateHelper.mPreparedTime = SystemClock.elapsedRealtime() - mVideoStateHelper.baseTime;
            } else if (command.cid == MediaPlayerCommandId.ON_BUFFERING_START) {
                mVideoStateHelper.mBufferedCount++;
                mVideoStateHelper.mBufferedStartTime = SystemClock.elapsedRealtime();
            } else if (command.cid == MediaPlayerCommandId.ON_BUFFERING_END) {
                if (mVideoStateHelper.mBufferedStartTime != 0) {
                    mVideoStateHelper.mBufferedTime += (SystemClock.elapsedRealtime() - mVideoStateHelper.mBufferedStartTime);
                }
            } else if (command.cid == MediaPlayerCommandId.ON_FIRST_FRAME_RENDERING) {
                mVideoStateHelper.mVideoFirstFrame = SystemClock.elapsedRealtime() - mVideoStateHelper.baseTime;
                mVideoStateHelper.mAudioFirstFrame = SystemClock.elapsedRealtime() - mVideoStateHelper.baseTime;
            } else if (command.cid == MediaPlayerCommandId.ON_PLAY_END) {
                mVideoStateHelper.mEndTime = SystemClock.elapsedRealtime() - mVideoStateHelper.baseTime;
                mVideoStateHelper.postLog(VideoStateHelper.LOG_TYPE_VIDEO_END);
                mVideoStateHelper.resetLog(false);
            } else if (command.cid == MediaPlayerCommandId.ON_SEEK) {
                mVideoStateHelper.mSeekCount++;
            } else if (command.cid == MediaPlayerCommandId.ON_CANCEL_PROCESS) {
                finish();
            }
        }
    });

    private CommandInvoker.VideoCommandInvoker mVideoCommandInvoker = new CommandInvoker.VideoCommandInvoker() {
        @Override
        public void invoke(VideoCommand videoCommand) {

            if (videoCommand.commandId == CommandId.BACK_BTN_CLICK) {
                mVideoStateHelper.appendPlayAction(VideoPlayAction.CLOSE);
                if (mCurrentPlayingVideo != null) {
                    EventLog.VIDEO.report6(EventConstants.VIDEO_BUTTON_TYPE_CLOSE, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                    EventLog1.Video.report6(EventLog1.Video.ButtonType.CLOSE, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                }
                finish();
            } else if (videoCommand.commandId == CommandId.ROTATE_BTN_CLICK) {
                if (mCurrentPlayingVideo != null) {
                    EventLog.VIDEO.report6(EventConstants.VIDEO_BUTTON_TYPE_ROTATE, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                    EventLog1.Video.report6(EventLog1.Video.ButtonType.ROTATE, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                }
                mScreenOrientationManager.toggleLandScreen(mOrientation == Configuration.ORIENTATION_PORTRAIT);
                mVideoStateHelper.appendPlayAction(VideoPlayAction.ROTATE);
//                mScreenOrientationManager.toggleLandScreen();
            } else if (videoCommand.commandId == CommandId.PLAY_VIDEO) {
                mVideoStateHelper.appendPlayAction(VideoPlayAction.PLAY_PAUSE);
            } else if (videoCommand.commandId == CommandId.PAUSE_VIDEO) {
                mVideoStateHelper.appendPlayAction(VideoPlayAction.PLAY_PAUSE);
            } else if (videoCommand.commandId == CommandId.UPDATE_PROGRESS) {
                mVideoStateHelper.mPlayDuration = Math.max(mVideoStateHelper.mPlayDuration, videoCommand.arg1);
                mVideoStateHelper.mDurationTime = Math.max(mVideoStateHelper.mDurationTime, videoCommand.arg2);
            } else if (videoCommand.commandId == CommandId.SEEK_PROGRESS) {
                mVideoStateHelper.appendPlayAction(VideoPlayAction.SEEK);
            }
        }
    };

    private CommandInvoker.PostCommandInvoker mPostCommandInvoker = new CommandInvoker.PostCommandInvoker() {
        @Override
        public void invoker(final PostCommand postCommand) {
            if (postCommand.commandId == CommandId.AVATAR_BTN_CLICK) {

                EventLog.Feed.report8((mAuth) ? EventConstants.FEED_SOURCE_DISCOVER_VIDEO_AFTER : EventConstants.FEED_SOURCE_UNLOGIN_VIDEO_AFTER, postCommand.videoPost.getUid(), PostEventManager.getPostRootUid(postCommand.videoPost),
                        postCommand.videoPost.getPid(), PostEventManager.getPostRootPid(postCommand.videoPost), EventConstants.CLICK_AREA_HEAD,
                        PostEventManager.getPostType(postCommand.videoPost), postCommand.videoPost.getStrategy(), postCommand.videoPost.getSequenceId());

                if (mCurrentPlayingVideo != null) {
                    EventLog.VIDEO.report6(EventConstants.VIDEO_BUTTON_TYPE_AVATAR, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                    EventLog1.Video.report6(EventLog1.Video.ButtonType.AVATAR, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                }
                UserHostPage.launch(true, postCommand.videoPost.getUid());
                mVideoStateHelper.appendPlayAction(VideoPlayAction.AVATAR);
            } else if (postCommand.commandId == CommandId.NICK_NAME_CLICK) {

                EventLog.Feed.report8((mAuth) ? EventConstants.FEED_SOURCE_DISCOVER_VIDEO_AFTER : EventConstants.FEED_SOURCE_UNLOGIN_VIDEO_AFTER, postCommand.videoPost.getUid(), PostEventManager.getPostRootUid(postCommand.videoPost),
                        postCommand.videoPost.getPid(), PostEventManager.getPostRootPid(postCommand.videoPost), EventConstants.CLICK_AREA_HEAD,
                        PostEventManager.getPostType(postCommand.videoPost), postCommand.videoPost.getStrategy(), postCommand.videoPost.getSequenceId());

                if (mCurrentPlayingVideo != null) {
                    EventLog.VIDEO.report6(EventConstants.VIDEO_BUTTON_TYPE_AVATAR, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                    EventLog1.Video.report6(EventLog1.Video.ButtonType.AVATAR, mCurrentPlayingVideo.getPid(), mCurrentPlayingVideo.getUid(), mCurrentPlayingVideo.getLanguage(), mCurrentPlayingVideo.getTags());
                }
                UserHostPage.launch(true, postCommand.videoPost.getUid());
                mVideoStateHelper.appendPlayAction(VideoPlayAction.AVATAR);
            } else if (postCommand.commandId == CommandId.DOWNLOAD_BTN_CLICK) {
                if (mCurrentDownloadVideo != null) {
                    return;
                }
                mCurrentDownloadVideo = postCommand.videoPost;
                mCurrentDownloadPostController = postCommand.postController;
                downloadVideoBtnClick();
                mVideoStateHelper.appendPlayAction(VideoPlayAction.DOWNLOAD);
                if (mCurrentDownloadVideo != null) {
                    EventLog1.Video.report3(mCurrentDownloadVideo.getPid(), mCurrentDownloadVideo.getUid(), mCurrentDownloadVideo.getLanguage(), mCurrentDownloadVideo.getTags());
                    EventLog.VIDEO.report3(mCurrentDownloadVideo.getPid(), mCurrentDownloadVideo.getUid(), mCurrentDownloadVideo.getLanguage(), mCurrentDownloadVideo.getTags());
                }
            } else if (postCommand.commandId == CommandId.COMMENT_BTN_CLICK) {
                doCommentClick(postCommand.videoPost);
            } else if (postCommand.commandId == CommandId.FOLLOW_BTN_CLICK) {
                doFollow(postCommand.videoPost, postCommand.postController);
            } else if (postCommand.commandId == CommandId.LIKE_BTN_CLICK) {
                doLike(postCommand.videoPost, postCommand.postController);
            } else if (postCommand.commandId == CommandId.SHARE_BTN_CLICK) {
                doShare(postCommand.videoPost);
            } else if (postCommand.commandId == CommandId.POST_CONTENT_CLICK) {
                doPostContentClick(postCommand.videoPost);
                mVideoStateHelper.appendPlayAction(VideoPlayAction.TEXT);
            } else if (postCommand.commandId == CommandId.RICH_ITEM_CLICK) {
                doRichItemClick(postCommand.richItem, postCommand.videoPost);
            }
        }
    };

    private void doRichItemClick(RichItem richItem, final VideoPost videoPost) {

        EventLog.Feed.report8((mAuth) ? EventConstants.FEED_SOURCE_DISCOVER_VIDEO_AFTER : EventConstants.FEED_SOURCE_UNLOGIN_VIDEO_AFTER,
                videoPost.getUid(), PostEventManager.getPostRootUid(videoPost),
                videoPost.getPid(), PostEventManager.getPostRootPid(videoPost), EventConstants.CLICK_AREA_TEXT,
                PostEventManager.getPostType(videoPost), videoPost.getStrategy(), videoPost.getSequenceId());


        if (richItem.isTopicItem()) {
            if (TextUtils.isEmpty(richItem.id)) return;
            Bundle bundle = new Bundle();
            TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
            bean.name = TextUtils.isEmpty(richItem.display) ? richItem.source : richItem.display;
            bean.id = richItem.id;
            bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
        } else if (richItem.isAtItem()) {
            UserHostPage.launch(true, richItem.id);
        } else if (richItem.isLinkItem()) {
            String target = TextUtils.isEmpty(richItem.target) ? richItem.source : richItem.target;
            WebViewActivity.luanch(this, target);
        } else if (richItem.isMoreItem()) {
            Bundle bundle = new Bundle();
            bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
            bundle.putSerializable(FeedConstant.KEY_FEED, videoPost);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        } else if (richItem.isArticleItem()) {
            if (mAuth) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(FeedConstant.KEY_FEED, videoPost);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_POST_ARTICLE_PAGE, bundle));
            } else {
//                if (BrowseManager.getLoginB()) {
//                    RegisterLoginMethodDialog.show(mRecyclerView.getContext());
//                    return;
//                }
//                ActionSnackBar.getInstance().showLoginLightSnackBar(mRecyclerView,
//                        ResourceTool.getString("common_continue_by_login"),
//                        ResourceTool.getString("common_login"), 3000, new ActionSnackBar.ActionBtnClickListener() {
//                            @Override
//                            public void onActionClick(View view) {
//                                BrowseSchemeManager.getInstance().setPostDetail(videoPost.getPid());
//                                if (BrowseManager.getLoginA())
//                                    RegisterLoginMethodDialog.show(view.getContext());
//                                else
//                                    RegistActivity.show(view.getContext());
//                            }
//                        });
            }
        } else if (richItem.isSuperTopicItem()) {
            Bundle bundle = new Bundle();
            bundle.putString(RouteConstant.ROUTE_KEY_SUPER_TOPIC_ID, richItem.id);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE, bundle));
        }
    }

    private void doPostContentClick(final VideoPost videoPost) {
        EventLog.VIDEO.report6(EventConstants.VIDEO_BUTTON_TYPE_TEXT, videoPost.getPid(), videoPost.getUid(), videoPost.getLanguage(), videoPost.getTags());
        EventLog1.Video.report6(EventLog1.Video.ButtonType.TEXT, videoPost.getPid(), videoPost.getUid(), videoPost.getLanguage(), videoPost.getTags());
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        bundle.putSerializable(FeedConstant.KEY_FEED, videoPost);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    private void doCommentClick(final VideoPost videoPost) {
        EventLog.Feed.report7(21, videoPost.getPid(), PostEventManager.getPostType(videoPost), videoPost.getStrategy(), videoPost.getSequenceId());

        String source;
        if (AccountManager.getInstance().isLoginComplete()) {
            source = EventConstants.FEED_PAGE_DISCOVER_VIDEO_AFTER;
        } else {
            source = EventConstants.FEED_PAGE_UNLOGIN_VIDEO_AFTER;
        }
        EventLog1.FeedForment.report1(ShareEventHelper.convertPostType(videoPost), source,
                FeedButtonFrom.VIDEO_PLAYER, videoPost.getPid(), videoPost.getStrategy(), videoPost.getOperationType(), videoPost.getLanguage(), videoPost.getTags(),
                FeedHelper.getRootPostLanguage(videoPost), FeedHelper.getRootPostTags(videoPost), FeedHelper.getRootOrPostUid(videoPost), videoPost.getSequenceId(), videoPost.getReclogs());
        if (videoPost.getCommentCount() == 0) {
            PublishCommentStarter.INSTANCE.startActivityFromVideoDetail(mRecyclerView.getContext(), videoPost);
            PublisherEventManager.INSTANCE.setSource(2);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.PAGE_COMMENT_INDEX);
            bundle.putSerializable(FeedConstant.KEY_FEED, videoPost);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        }
    }

    private void doShare(final VideoPost videoPost) {
        EventLog.Share.report5();
        ShareModel shareModel = new ShareModel();
        shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_POST);
        shareModel.setImageUrl(videoPost.getCoverUrl());
        shareModel.setVideoUrl(videoPost.getVideoUrl());
        shareModel.setH5Link(videoPost.getVideoUrl());
        shareModel.setTitle(videoPost.getSummary());

        SharePackageModel shareLink = new SharePackageModel(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "share_link"),
                R.drawable.share_share_link_icon, SharePackageFactory.SharePackage.MENU1, new DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU1) {
            @Override
            public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {
                NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_VIDEO);
                ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_LINK, 7);
                EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, PostType.VIDEO, EventLog1.Share.VideoPostType.POST_LINK,
                        EventLog1.Share.ShareFrom.VIDEO_DETAIL, EventLog1.Share.PopPage.OTHER, videoPost.getStrategy(), videoPost.getOperationType(),
                        EventConstants.FEED_PAGE_VIDEO_PLAYER, videoPost.getPid(), videoPost.getLanguage(), videoPost.getTags(), videoPost.getUid(),
                        FeedHelper.getRootPostId(videoPost), FeedHelper.getRootPostUid(videoPost), FeedHelper.getRootPostLanguage(videoPost), FeedHelper.getRootPostTags(videoPost), videoPost.getSequenceId(), videoPost.getReclogs());
                ShareHelper.sharePostToWhatsApp(VideoListPlayerActivity.this, videoPost, false, eventModel);
            }
        });
        SharePackageModel shareVideo = new SharePackageModel(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "share_video"),
                R.drawable.share_share_video_icon, SharePackageFactory.SharePackage.MENU2, new DefaultShareMenuManager(SharePackageFactory.SharePackage.MENU2) {
            @Override
            public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {
                NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_VIDEO);
                ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_VIDEO, 7);
                EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, PostType.VIDEO, EventLog1.Share.VideoPostType.VIDEO_FILE,
                        EventLog1.Share.ShareFrom.VIDEO_DETAIL, EventLog1.Share.PopPage.OTHER, videoPost.getStrategy(), videoPost.getOperationType(),
                        EventConstants.FEED_PAGE_VIDEO_PLAYER, videoPost.getPid(), videoPost.getLanguage(), videoPost.getTags(), videoPost.getUid(),
                        FeedHelper.getRootPostId(videoPost), FeedHelper.getRootPostUid(videoPost), FeedHelper.getRootPostLanguage(videoPost), FeedHelper.getRootPostTags(videoPost), videoPost.getSequenceId(), videoPost.getReclogs());
                ShareHelper.sharePostToWhatsApp(VideoListPlayerActivity.this, videoPost, true, eventModel);
            }
        });
        SharePackageModel report = CustomShareMenuFactory.Companion.createMenu(ShareMenu.REPORT, new Function1<ShareMenu, Unit>() {
            @Override
            public Unit invoke(ShareMenu shareMenu) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(FeedbackConstants.FEEDBACK_KEY_POST, videoPost);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_REPOER_PAGE, bundle));
                return null;
            }
        });
        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, PostType.VIDEO, null,
                EventLog1.Share.ShareFrom.VIDEO_DETAIL, EventLog1.Share.PopPage.OTHER, videoPost.getStrategy(), videoPost.getOperationType(),
                EventConstants.FEED_PAGE_VIDEO_PLAYER, videoPost.getPid(), videoPost.getLanguage(), videoPost.getTags(), videoPost.getUid(), FeedHelper.getRootPostId(videoPost),
                FeedHelper.getRootPostUid(videoPost), FeedHelper.getRootPostLanguage(videoPost), FeedHelper.getRootPostTags(videoPost), videoPost.getSequenceId(), videoPost.getReclogs());
        ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
        builder.with(this)
                .shareModel(shareModel)
                .eventModel(eventModel)
                .shareCustomMenus(shareVideo, shareLink, CustomShareMenuFactory.Companion.createEmptyMenu(),
                        CustomShareMenuFactory.Companion.createEmptyMenu(), CustomShareMenuFactory.Companion.createEmptyMenu(), report)
                .build().share();
    }

    private void doFollow(VideoPost videoPost, PostController postController) {
        if (mAuth) {

        } else {
            postController.doBrowseFollow();
        }
    }

    private void downloadVideoBtnClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doDownload();
        } else {
            EasyPermissions.requestPermissions(this
                    , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_write_permission")
                    , PermissionRequestCode.DOWNLOAD_VIDEO_PERMISSION
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doDownload() {
        if (mCurrentDownloadVideo == null) {
            return;
        }
        String downloadUrl = mCurrentDownloadVideo.getDownloadUrl();
        if (TextUtils.isEmpty(downloadUrl)) {
            downloadUrl = mCurrentDownloadVideo.getVideoUrl();
        }
        mVideoListViewModel.downloadVideo(downloadUrl, mCurrentDownloadVideo.getVideoUrl());
    }

    private void doLike(final VideoPost videoPost, PostController postController) {
        EventLog.Feed.report5(videoPost.getPid(), 0, 1, 21, PostEventManager.getPostType(videoPost), videoPost.getStrategy(), videoPost.getSequenceId());
        EventLog1.FeedLike.report1(ShareEventHelper.convertPostType(videoPost), EventConstants.FEED_PAGE_VIDEO_PLAYER, FeedButtonFrom.VIDEO_PLAYER, videoPost.getPid(), videoPost.getUid(), videoPost.getStrategy(),
                videoPost.getOperationType(), videoPost.getLanguage(), videoPost.getTags(), FeedHelper.getRootOrPostUid(videoPost), FeedHelper.getRootPostLanguage(videoPost), FeedHelper.getRootPostTags(videoPost), videoPost.getSequenceId(),videoPost.getReclogs());
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LIKE);
        videoPost.setLike(true);
        videoPost.setLikeCount(videoPost.getLikeCount() + 1);
        if (postController != null) {
            postController.bindPost(videoPost);
        }
        if (mAuth) {
            SinglePostManager.like(videoPost.getPid());

            HalfLoginManager.updateClickCount(new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));

        } else {
            BrowseEventStore.INSTANCE.updateLikeCount(videoPost, new InsertLikeCallBack() {
                @Override
                public void onLoadEntity(boolean inserted, int count) {
                    if (inserted) {

                        if (count % 3 == 1) {
                            HalfLoginManager.getInstancce().showLoginDialog(mRecyclerView.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
//                            RegisterActivity.Companion.show(mRecyclerView.getContext(), 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                        }
                    } else {
                        HalfLoginManager.getInstancce().showLoginDialog(mRecyclerView.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
//                        RegisterActivity.Companion.show(mRecyclerView.getContext(), 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                    }
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            browseLike(videoPost);
                        }
                    });
                }
            });


        }
    }

    private static void browseLike(final PostBase post) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new BrowseLikeRequest(MyApplication.getAppContext()).request(post.getPid());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PermissionRequestCode.DOWNLOAD_VIDEO_PERMISSION) {
            doDownload();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PermissionRequestCode.DOWNLOAD_VIDEO_PERMISSION) {
            Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
        }
    }

    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;
        mAdapter.setOrientation(newConfig.orientation);
    }

    private AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                //pause play
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                //resume play
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                //stop
            }
        }
    };

}
