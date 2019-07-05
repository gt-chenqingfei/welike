package com.redefine.multimedia.player.youtube;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.redefine.multimedia.R;
import com.redefine.multimedia.player.youtube.constant.YoutubeConstant;

import java.util.List;

public class YoutubePlayerActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnFullscreenListener {

    private static final int REQ_START_STANDALONE_PLAYER = 100;
    private YouTubePlayerView mPlayerView;
    private YouTubePlayer mYoutubePlayer;
    private String mYoutubeVideoId;
    private View mBackBtn;
    private boolean mFullScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player_layout);
        parseBundle(getIntent());
        mPlayerView = findViewById(R.id.youtube_view);
        mBackBtn = findViewById(R.id.common_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPlayerView.initialize(YoutubeConstant.YOUTUBE_DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        playYoutubeByStandalone(mYoutubeVideoId);
    }


    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private void playYoutubeByStandalone(String id) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                this, YoutubeConstant.YOUTUBE_DEVELOPER_KEY, id, 0, true, false);
        if (intent != null) {
            if (canResolveIntent(intent)) {
                finish();
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            }
        }


    }

    private void parseBundle(Intent intent) {
        if (intent == null) {
            return ;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return ;
        }

        if (bundle.containsKey(YoutubeConstant.KEY_URL)) {
            String url = bundle.getString(YoutubeConstant.KEY_URL);
            mYoutubeVideoId = parseYoutubeUrl(url);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String parseYoutubeUrl(String url) {
        return YouTubeUrlParser.getVideoId(url);
    }

    @Override
    public void onBackPressed() {
        try {
            if (mFullScreen && mYoutubePlayer != null) {
                mYoutubePlayer.setFullscreen(false);
            } else {
                super.onBackPressed();
            }
        } catch (Throwable e) {
            super.onBackPressed();
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return mPlayerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.mYoutubePlayer = youTubePlayer;
        this.mYoutubePlayer.setOnFullscreenListener(this);
        if (!wasRestored) {
            if (!TextUtils.isEmpty(mYoutubeVideoId)) {
                mYoutubePlayer.loadVideo(mYoutubeVideoId);
            }
        }
    }

    public static void player(Context context, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, YoutubePlayerActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onFullscreen(boolean b) {
        mFullScreen = b;
    }
}
