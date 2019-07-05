package com.redefine.multimedia.player.apollo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.UCMobile.Apollo.MediaPlayer;
import com.UCMobile.Apollo.TimedText;
import com.redefine.multimedia.player.base.IVideoView;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediaplayer.IMediaPlayerControl;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommand;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommandId;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.multimedia.player.view.ITextureStrategy;
import com.redefine.multimedia.player.view.TextureStrategyFactory;

import java.util.HashMap;
import java.util.Map;

public class ApolloVideoView implements IVideoView, TextureView.SurfaceTextureListener, MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnCachedPositionsListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener
        , MediaPlayer.OnTimedTextListener, MediaPlayer.OnVideoSizeChangedListener, IMediaPlayerControl {

    private final MediaPlayer mMediaPlayer;
    private final ITextureStrategy mTextureView;
    private IMediaController mMediaController;
    private boolean isPrepared = false;
    private int mSeekWhenPrepared = 0;
    private MediaPlayerInvoker mCommandInvoker;
    private boolean isPausedInPreparing;
    private String mUrl;

    public ApolloVideoView(Context context) {
        mMediaPlayer = new MediaPlayer(context);
        mTextureView = TextureStrategyFactory.getTextureStrategy(context);
        mTextureView.getTextureView().setSurfaceTextureListener(this);

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCachedPositionsListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnTimedTextListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOption("rw.instance.set_looping", "1");

        mMediaPlayer.setScreenOnWhilePlaying(true);
        MediaPlayer.setGlobalOption("rw.global.cache_dir", context.getCacheDir() + "/video/.applocache");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mMediaPlayer.setSurface(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        pause();
        mMediaPlayer.setSurface(null);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void setDataSource(String url) {
        try {
            mUrl = url;
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMediaController(IMediaController mediaController) {
        mMediaController = mediaController;
        mMediaController.setMediaPlayerController(this);
    }

    @Override
    public View getView() {
        return mTextureView.getTextureView();
    }

    @Override
    public boolean start() {
        try {
            if (isPrepared) {
                mMediaPlayer.start();
                return true;
            }
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }

    @Override
    public boolean pause() {
        try {
            if (isPrepared) {
                mMediaPlayer.pause();
            } else {
                isPausedInPreparing = true;
            }

        } catch (Exception e) {
            // do nothing
        }
        return true;
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        if (isPrepared) {
            try {
                if (mCommandInvoker != null) {
                    mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_SEEK));
                }
                if (pos == mMediaPlayer.getDuration()) {
                    pos = mMediaPlayer.getDuration() - 5 * 1000;
                }
                mMediaPlayer.seekTo(pos);
            } catch (Exception e) {
                // do nothing
            }
        } else {
            mSeekWhenPrepared = pos;
        }

    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null && mMediaPlayer.getDuration() > 0) {
            return (mMediaPlayer.getPlayableDuration() * 100) / mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void setMute(boolean isMute) {
        if (isMute) {
            mMediaPlayer.setGeneralOption("rw.instance.mute", null);
        } else {
            Map<String, Float> m = new HashMap<>();
            m.put("left", Float.parseFloat("1.0"));
            m.put("right", Float.parseFloat("1.0"));
            mMediaPlayer.setGeneralOption("rw.instance.setvolume", m);
        }
    }

    @Override
    public String getPlayUrl() {
        return mUrl;
    }

    @Override
    public void destroy() {
        mMediaController.release();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void setMediaPlayerInvoker(MediaPlayerInvoker invoker) {
        mCommandInvoker = invoker;
    }

    @Override
    public void onPause() {
        mTextureView.onPause();
        mMediaController.autoPause();
    }

    @Override
    public void onResume() {
        isPausedInPreparing = false;
        mTextureView.onResume();
        mMediaController.autoPlay();
    }

    private void autoPlay() {
        mMediaController.autoPlay();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
    }

    @Override
    public void onCachedPositions(MediaPlayer mediaPlayer, Map map) {
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START:
                mMediaController.showLoading();
                if (mCommandInvoker != null) {
                    mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_BUFFERING_START));
                }
                break;
            case android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mMediaController.dismissLoading();
                if (mCommandInvoker != null) {
                    mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_BUFFERING_END));
                }
                break;
            case 609:
                mMediaController.onCompletion();
                if (mCommandInvoker != null) {
                    mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_PLAY_END));
                }
                break;
            case android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (mCommandInvoker != null) {
                    mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_FIRST_FRAME_RENDERING));
                }
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPrepared = true;
        mMediaController.onPrepared();
        if (mCommandInvoker != null) {
            mCommandInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_PREPARED));
        }
        if (mSeekWhenPrepared != 0) {
            seekTo(mSeekWhenPrepared);
            mSeekWhenPrepared = 0;
        }
        if (!isPausedInPreparing) {
            autoPlay();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        mTextureView.setVideoSize(width, height);
    }
}
