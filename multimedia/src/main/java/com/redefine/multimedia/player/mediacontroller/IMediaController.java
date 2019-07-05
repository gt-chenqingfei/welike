package com.redefine.multimedia.player.mediacontroller;

import com.redefine.commonui.download.DownloadState;
import com.redefine.multimedia.player.mediaplayer.IMediaPlayerControl;

public interface IMediaController {
    void setMediaPlayerController(IMediaPlayerControl apolloVideoView);

    void showLoading();

    void dismissLoading();

    void dismissMediaController();

    void showMediaController();

    void updateProgress();

    void autoPlay();

    void autoPause();

    void release();

    void setPauseFlag(boolean b);

    void onPrepared();

    void onCompletion();

    void setMediaControllerInvoker(MediaControllerInvoker invoker);

    void gone();

    void dismissTrackThumb();

    void downloadError(String url);

    void downloadStart(String url);

    void downloadSuccess(String url);

    void downloadCancel(String url);

    void downloadProgressChanged(Integer integer);

    void updateDownloadStatus(String url, DownloadState status);

    void goneDownloadBtn();
    void showDownloadBtn();

    void goneShareBtn();

    void showShareBtn();
}
