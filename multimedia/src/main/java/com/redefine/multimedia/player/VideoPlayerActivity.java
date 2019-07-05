package com.redefine.multimedia.player;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.download.DownloadState;
import com.redefine.commonui.download.MediaDownloadManager;
import com.redefine.commonui.enums.DownloadStateEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.multimedia.R;
import com.redefine.multimedia.player.base.VideoViewDelegate;
import com.redefine.multimedia.player.base.VideoViewType;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediacontroller.MediaControllerCommand;
import com.redefine.multimedia.player.mediacontroller.MediaControllerCommandId;
import com.redefine.multimedia.player.mediacontroller.MediaControllerInvoker;
import com.redefine.multimedia.player.mediacontroller.ScreenOrientationManager;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommand;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommandId;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.resource.ResourceTool;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class VideoPlayerActivity extends AppCompatActivity implements MediaControllerInvoker, MediaPlayerInvoker, EasyPermissions.PermissionCallbacks {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void launch(Activity mContext, String nick, String videoCover, String videoSource, String videoUrl, String postId, String postUid, String rootPostId, String rootPostUid, String playSource, String userHeader, String userNick) {
        Intent intent = new Intent(mContext, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, videoUrl);
        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, videoSource);
        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_NICK, nick);

        bundle.putString(PlayerConstant.POST_ID, postId);
        bundle.putString(PlayerConstant.POST_UID, postUid);
        bundle.putString(PlayerConstant.POST_USER_HEADER, userHeader);
        bundle.putString(PlayerConstant.POST_USER_NICK, userNick);
        bundle.putString(PlayerConstant.ROOT_POST_ID, rootPostId);
        bundle.putString(PlayerConstant.ROOT_POST_UID, rootPostUid);
        bundle.putString(PlayerConstant.PLAY_SOURCE, playSource);
        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_COVER, videoCover);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private IMediaController mMediaController;
    private ViewGroup mVideoViewRoot;
    private VideoViewDelegate mVideoView;
    private String mVideoPath;
    private String mVideoSource;
    private VideoPlayerViewModel mViewModel;
    private boolean isPaused = false;
    private SimpleDraweeView mVideoPlayerCover;
    private String mVideoCoverUrl;
    private ScreenOrientationManager mScreenOrientationManager;
    private View mErrorView;
    private TextView mErrorText;
    private VideoStateHelper mStateHelper;
    private String mVideoNick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseBundle(getIntent(), savedInstanceState);
        if (TextUtils.isEmpty(mVideoPath)) {
            finish();
        }
        setContentView(R.layout.video_player_layout);
        mViewModel = ViewModelProviders.of(this).get(VideoPlayerViewModel.class);

        mMediaController = findViewById(R.id.video_media_controller_root_view);
        mVideoViewRoot = findViewById(R.id.video_view_root_view);
        mVideoPlayerCover = findViewById(R.id.video_player_cover);

        mErrorText = findViewById(R.id.video_player_error_text);
        mErrorText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "video_player_net_error"));

        mErrorView = findViewById(R.id.video_player_error_view);

        mScreenOrientationManager = new ScreenOrientationManager(this);

        mViewModel.getViewType().observe(this, new Observer<VideoViewType>() {
            @Override
            public void onChanged(@Nullable VideoViewType videoViewType) {
                if (videoViewType == null) {
                    return;
                }

                if (videoViewType == VideoViewType.APOLLO) {
                    mStateHelper.mPlayerType = PlayerConstant.LOG_PLAYER_APOLLO;
                    mStateHelper.mStartTime = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
                    mVideoView = new VideoViewDelegate(VideoPlayerActivity.this, mVideoViewRoot, VideoViewType.APOLLO);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                    mVideoViewRoot.addView(mVideoView.getView(), layoutParams);
                    mVideoView.setMediaPlayerInvoker(VideoPlayerActivity.this);
                    mVideoView.setMediaController(mMediaController);
                    if (TextUtils.equals(mVideoSource, PlayerConstant.VIDEO_SITE_DEFAULT)) {
                        mMediaController.dismissTrackThumb();
                    }
                    if (TextUtils.equals(mVideoSource, PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                        mMediaController.goneDownloadBtn();
                    } else {
                        mMediaController.showDownloadBtn();
                    }
                } else if (videoViewType == VideoViewType.YOUTUBE) {
                } else if (videoViewType == VideoViewType.WEB) {
                    dismissVideoCover();
                    mStateHelper.mPlayerType = PlayerConstant.LOG_PLAYER_WEB;
                    mMediaController.gone();
                    mVideoView = new VideoViewDelegate(VideoPlayerActivity.this, mVideoViewRoot, VideoViewType.WEB);
                    mVideoView.setMediaPlayerInvoker(VideoPlayerActivity.this);
                }
            }
        });

        mViewModel.getVideoUrl().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (TextUtils.isEmpty(s) || mVideoView == null) {
                    return;
                }
                mMediaController.setPauseFlag(isPaused);
                mVideoView.setDataSource(s);
                mMediaController.updateDownloadStatus(s, MediaDownloadManager.getInstance().getDownloadState(s));
                LogUtil.d("wng_", "video url : " + s);
                if (UrlFileTypeUtil.isM3U8Type(s)) {
                    mMediaController.goneDownloadBtn();
                }
            }
        });

        mViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == PageStatusEnum.LOADING) {
                    mMediaController.showLoading();
                    mMediaController.goneDownloadBtn();
                } else if (pageStatusEnum == PageStatusEnum.CONTENT) {
                    mMediaController.dismissLoading();
                } else if (pageStatusEnum == PageStatusEnum.ERROR) {
                    mMediaController.dismissLoading();
                    mErrorView.setVisibility(View.VISIBLE);
                    mErrorView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorView.setVisibility(View.GONE);
                            mMediaController.showLoading();
                            mViewModel.setVideoUrlAndSource(mVideoPath, mVideoSource);
                        }
                    });
                }
            }
        });

        mViewModel.getDownloadStatus().observe(this, new Observer<DownloadStateEnum>() {
            @Override
            public void onChanged(@Nullable DownloadStateEnum downloadStateEnum) {
                if (downloadStateEnum == null) {
                    return;
                }
                switch (downloadStateEnum) {
                    case ERROR:
                        mMediaController.downloadError(mVideoView.getPlayUrl());
                        break;
                    case START:
                        mMediaController.downloadStart(mVideoView.getPlayUrl());
                        break;
                    case SUCCESS:
                        mMediaController.downloadSuccess(mVideoView.getPlayUrl());
                        break;
                    case CANCELED:
                        mMediaController.downloadCancel(mVideoView.getPlayUrl());
                        break;
                }
            }
        });

        mViewModel.getDownloadProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mMediaController.downloadProgressChanged(integer);
            }
        });

        if (TextUtils.isEmpty(mVideoCoverUrl)) {
            mVideoPlayerCover.setVisibility(View.GONE);
        } else {
            mVideoPlayerCover.setImageURI(mVideoCoverUrl);
        }

        mMediaController.setMediaControllerInvoker(this);

        mViewModel.setVideoUrlAndSource(mVideoPath, mVideoSource);

    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        mScreenOrientationManager.disable();
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        mScreenOrientationManager.enable();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStateHelper.mEndTime = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
        mStateHelper.postLog(VideoStateHelper.LOG_TYPE_DESTROY_PAGE);

        mScreenOrientationManager.onDestroy();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
    }

    private void parseBundle(Intent intent, Bundle savedInstanceState) {
        mStateHelper = new VideoStateHelper();

        mVideoPath = intent.getStringExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH);
        mVideoSource = intent.getStringExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE);
        mVideoCoverUrl = intent.getStringExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_COVER);
        mVideoNick = intent.getStringExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_NICK);
        if (TextUtils.isEmpty(mVideoPath) && savedInstanceState != null) {
            mVideoPath = savedInstanceState.getString(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH);
            mVideoSource = savedInstanceState.getString(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE);
        }
        if (TextUtils.isEmpty(mVideoSource)) {
            mVideoSource = PlayerConstant.VIDEO_SITE_DEFAULT;
        }

        if (!TextUtils.isEmpty(mVideoPath) && mVideoPath.contains("file.welike.in") && mVideoPath.startsWith("https")) {
            mVideoPath = mVideoPath.replace("https", "http");
        }
        mStateHelper.mPostUid = getIntent().getStringExtra(PlayerConstant.POST_UID);
        mStateHelper.mRootPostUid = getIntent().getStringExtra(PlayerConstant.ROOT_POST_UID);
        mStateHelper.mPostId = getIntent().getStringExtra(PlayerConstant.POST_ID);
        mStateHelper.mRootPostId = getIntent().getStringExtra(PlayerConstant.ROOT_POST_ID);
        mStateHelper.mPlayerSource = getIntent().getStringExtra(PlayerConstant.PLAY_SOURCE);
        mStateHelper.mPostUserHeader = getIntent().getStringExtra(PlayerConstant.POST_USER_HEADER);
        mStateHelper.mPostUserNick = getIntent().getStringExtra(PlayerConstant.POST_USER_NICK);

        mStateHelper.mVideoPath = mVideoPath;
        mStateHelper.mVideoSource = TextUtils.equals(mVideoSource, PlayerConstant.VIDEO_SITE_YOUTUBE) ? 1 : 0;

    }

    @Override
    public void doCommand(MediaControllerCommand command) {
        if (command == null) {
            return;
        }
        if (command.cid == MediaControllerCommandId.BACK_BTN_CLICK) {
            finish();
        } else if (command.cid == MediaControllerCommandId.ROTATE_BTN_CLICK) {
            mScreenOrientationManager.toggleLandScreen();
        } else if (command.cid == MediaControllerCommandId.UPDATE_PROGRESS) {
            mStateHelper.mPlayDuration = Math.max(mStateHelper.mPlayDuration, command.arg1);
            mStateHelper.mDurationTime = Math.max(mStateHelper.mDurationTime, command.arg2);
        } else if (command.cid == MediaControllerCommandId.DOWNLOAD_BTN_CLICK) {
            downloadVideoBtnClick();
        } else if (command.cid == MediaControllerCommandId.SHOW_CONTROLLER_BAR) {
            if (TextUtils.equals(mVideoSource, PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                mMediaController.goneDownloadBtn();
            } else if (TextUtils.equals(mVideoSource, PlayerConstant.VIDEO_SITE_DEFAULT)) {
                String url = mVideoView.getPlayUrl();
                DownloadState status = MediaDownloadManager.getInstance().getDownloadState(url);
                mMediaController.updateDownloadStatus(url, status);
            }

        } else if (command.cid == MediaControllerCommandId.SHARE_BTN_CLICK) {
            doShare();
        }
    }

    private void doShare() {
//        ShareModel shareModel = new ShareModel();
//        shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_POST);
//        shareModel.setImageUrl(mVideoCoverUrl);
//        shareModel.setVideoUrl(mVideoPath);
//        shareModel.setH5Link(mVideoPath);
//        shareModel.setTitle(mVideoNick);
//
//        ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
//        builder.login(AccountManager.getInstance().isLogin())
//                .with(this)
//                .sharePackage(SharePackageFactory.SharePackage.WHATS_APP)
//                .shareModel(shareModel)
//                .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_POST, TextUtils.isEmpty(mStateHelper.mPostId) ? mStateHelper.mRootPostId : mStateHelper.mPostId))
//                .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_POST, mStateHelper.mPostUserNick))
//                .waterMarkModel(new WaterMarkModel(mStateHelper.mPostUserHeader, mStateHelper.mPostUserNick, null))
//                .build()
//                .share();
    }

    private void downloadVideo() {
        String url = mVideoView.getPlayUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mViewModel.downloadVideo(url, GlobalConfig.AT + mVideoNick);
    }

    private void downloadVideoBtnClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            downloadVideo();
        } else {
            EasyPermissions.requestPermissions(this
                    , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_write_permission")
                    , PermissionRequestCode.DOWNLOAD_VIDEO_PERMISSION
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void doCommand(MediaPlayerCommand command) {
        if (command == null) {
            return;
        }
        if (command.cid == MediaPlayerCommandId.ON_PREPARED) {
            dismissVideoCover();
            mStateHelper.mPreparedTime = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
        } else if (command.cid == MediaPlayerCommandId.ON_BUFFERING_START) {
            mStateHelper.mBufferedCount++;
            mStateHelper.mBufferedStartTime = SystemClock.elapsedRealtime();
        } else if (command.cid == MediaPlayerCommandId.ON_BUFFERING_END) {
            if (mStateHelper.mBufferedStartTime != 0) {
                mStateHelper.mBufferedTime += (SystemClock.elapsedRealtime() - mStateHelper.mBufferedStartTime);
            }
        } else if (command.cid == MediaPlayerCommandId.ON_FIRST_FRAME_RENDERING) {
            mStateHelper.mVideoFirstFrame = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
            mStateHelper.mAudioFirstFrame = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
        } else if (command.cid == MediaPlayerCommandId.ON_PLAY_END) {
            mStateHelper.mEndTime = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
            mStateHelper.postLog(VideoStateHelper.LOG_TYPE_VIDEO_END);
            mStateHelper.resetLog(false);
        } else if (command.cid == MediaPlayerCommandId.ON_SEEK) {
            mStateHelper.mSeekCount++;
        } else if (command.cid == MediaPlayerCommandId.ON_CANCEL_PROCESS) {
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
            downloadVideo();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PermissionRequestCode.DOWNLOAD_VIDEO_PERMISSION) {
            Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissVideoCover() {
        mVideoPlayerCover.setImageURI("");
        mVideoPlayerCover.setVisibility(View.GONE);
    }
}
