package com.redefine.welike.business.videoplayer.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.download.DownloadState;
import com.redefine.commonui.download.MediaDownloadManager;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.foundation.utils.NumberFormatUtil;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.RichTextView;
import com.redefine.richtext.processor.IRichTextProcessor;
import com.redefine.welike.R;
import com.redefine.welike.Switcher;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUserCountCallBack;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.util.PostRichItemClickHandler;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.videoplayer.management.command.CommandId;
import com.redefine.welike.business.videoplayer.management.command.CommandInvoker;
import com.redefine.welike.business.videoplayer.management.command.PostCommand;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.view.ImageWithTextView;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/8/8
 */
public class PostController extends ConstraintLayout {

    private SimpleDraweeView mUserHeader;
    private TextView mNickName;
    private TextView mVideoDuration;
    private UserFollowBtn mUserFollowBtn;
    private ImageWithTextView mLike;
    private ImageWithTextView mComment;
    private ImageWithTextView mShare;
    private ImageWithTextView mDownload;
    private RichTextView mPostContent;
    private View mPostContentContainer;
    private TextView tvOperation;

    private VideoPost mVideoPost;
    private CommandInvoker.PostCommandInvoker mPostCommandInvoker;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public PostController(Context context) {
        super(context);
        init();
    }

    public PostController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PostController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.post_controller_layout, this, true);
        mUserHeader = view.findViewById(R.id.video_user_header);
        mNickName = view.findViewById(R.id.video_nick_name);
        mVideoDuration = view.findViewById(R.id.video_time);
        mUserFollowBtn = view.findViewById(R.id.video_follow_btn);
        mLike = view.findViewById(R.id.video_like);
        mComment = view.findViewById(R.id.video_comment);
        mShare = view.findViewById(R.id.video_share);
        mDownload = view.findViewById(R.id.video_download);
        mPostContent = view.findViewById(R.id.video_post_content);
        mPostContentContainer = view.findViewById(R.id.video_post_content_container);
        tvOperation = findViewById(R.id.tv_common_feed_operation);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mUserFollowBtn, false);

        mUserHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.AVATAR_BTN_CLICK, mVideoPost));
                }
            }
        });

        mNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.NICK_NAME_CLICK, mVideoPost));
                }
            }
        });
        mUserFollowBtn.addOtherListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.FOLLOW_BTN_CLICK, mVideoPost, PostController.this));
                }
            }
        });
        mLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoPost.isLike()) {
                    return;
                }
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.LIKE_BTN_CLICK, mVideoPost, PostController.this));
                }
            }
        });
        mComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.COMMENT_BTN_CLICK, mVideoPost));
                }
            }
        });
        mShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.SHARE_BTN_CLICK, mVideoPost));
                }
            }
        });
        mDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.DOWNLOAD_BTN_CLICK, mVideoPost, PostController.this));
                }
            }
        });
        mPostContentContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.POST_CONTENT_CLICK, mVideoPost));
                }
            }
        });
        mPostContent.getRichProcessor().setOnRichItemClickListener(new PostRichItemClickHandler(mPostContent.getContext(), mVideoPost) {
            @Override
            public void onRichItemClick(RichItem richItem) {
                if (mPostCommandInvoker != null) {
                    mPostCommandInvoker.invoker(new PostCommand(CommandId.RICH_ITEM_CLICK, richItem, mVideoPost));
                }
            }
        });
    }

    public void setPostCommandInvoker(CommandInvoker.PostCommandInvoker postCommandInvoker) {
        mPostCommandInvoker = postCommandInvoker;
    }

    public void bindPost(VideoPost videoPost) {
        mVideoPost = videoPost;
        HeadUrlLoader.getInstance().loadHeaderUrl2(mUserHeader, videoPost.getHeadUrl());
        mNickName.setText(videoPost.getNickName());
        String time = DateTimeUtil.INSTANCE.formatPostPublishTime(mVideoDuration.getResources(), videoPost.getTime());
        mVideoDuration.setText(time);
        if (videoPost.isFollowing()) {
            mUserFollowBtn.setFollowStatus(IFollowBtn.FollowStatus.FOLLOWING);
        } else {
            mUserFollowBtn.setFollowStatus(IFollowBtn.FollowStatus.FOLLOW);
        }
        if (videoPost.isLike()) {
            mLike.setImage(R.drawable.video_like_icon);
        } else {
            mLike.setImage(R.drawable.video_unlike_icon);
        }
        mLike.setText(NumberFormatUtil.formatNumber(videoPost.getLikeCount()));
        mComment.setImageText(NumberFormatUtil.formatNumber(videoPost.getCommentCount()), R.drawable.video_comment_icon);
        String shareText;
        if (videoPost.getShareCount() > 0) {
            shareText = String.valueOf(videoPost.getShareCount());
        } else {
            shareText = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_share");
        }
        mShare.setImageText(shareText, R.drawable.video_share_icon);
        mDownload.setImageText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "download"), R.drawable.video_download_icon);
        DownloadState downloadState = MediaDownloadManager.getInstance().getDownloadState(videoPost.getVideoUrl());
        if (downloadState == DownloadState.COMPLETED) {
            videoPost.setDownloadProgress(100);
        }
        int downloadProgress = videoPost.getDownloadProgress();
        if (downloadProgress == 100) {
            mDownload.goneProgressBar();
            mDownload.setImage(R.drawable.video_download_success);
        } else {
            mDownload.setProgress(downloadProgress);
        }
        mFollowBtnPresenter.bindView(videoPost.getUid(), videoPost.isFollowing(), false, new FollowEventModel(EventConstants.FEED_PAGE_VIDEO_PLAYER, videoPost));
        IRichTextProcessor richProcessor = mPostContent.getRichProcessor();
        richProcessor.setRichContent(videoPost.getText(), videoPost.getRichItemList());
        onConfigurationChanged();

        if (Switcher.SHOW_FEED_INFO) {
            tvOperation.setVisibility(View.VISIBLE);
            StringBuilder buffer = new StringBuilder("Language=");
            buffer.append(videoPost.getLanguage());
            buffer.append(";tags=[");
            if (videoPost.getTags() != null) {
                for (String t : videoPost.getTags()) {
                    buffer.append(t);
                    buffer.append(",");
                }
            }
            buffer.append("];Strategy=");
            buffer.append(videoPost.getStrategy());
            buffer.append(";origin=");
            buffer.append(videoPost.origin);
            buffer.append(";operationType=");
            buffer.append(videoPost.getOperationType());
            tvOperation.setText(buffer.toString());
        } else {
            tvOperation.setVisibility(View.GONE);
        }
    }

    public void disableFollow() {
        mUserFollowBtn.setOnClickFollowBtnListener(null);
        mVideoPost.setFollowing(false);
        bindPost(mVideoPost);
    }

    public void doBrowseFollow() {
        mUserFollowBtn.setOnClickFollowBtnListener(null);


        BrowseEventStore.INSTANCE.getFollowUserCount(new FollowUser(mVideoPost.getUid(), mVideoPost.getNickName()), new FollowUserCountCallBack() {
            @Override
            public void onLoadEntity(boolean inserted, final int count) {
                if (inserted) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            mUserFollowBtn.setVisibility(View.GONE);
                            mVideoPost.setFollowing(true);
//
                            if (count % 3 == 1) {
                                HalfLoginManager.getInstancce().showLoginDialog(mUserFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
//                                RegisterActivity.Companion.show(mUserFollowBtn.getContext(), 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                            }
                        }
                    });

                } else {
                    HalfLoginManager.getInstancce().showLoginDialog(mUserFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
//                    RegisterActivity.Companion.show(mUserFollowBtn.getContext(), 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                }
            }
        });
    }

    public void onMediaControllerChange(boolean show) {
        mPostContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void changeConfig(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVisibility(View.INVISIBLE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVisibility(View.VISIBLE);
        }
    }

    public void onConfigurationChanged() {
//        Configuration mConfiguration = getResources().getConfiguration();
//        int ori = mConfiguration.orientation;
//        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
//            setVisibility(View.INVISIBLE);
//        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
//            setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onConfigurationChanged();
    }
}
