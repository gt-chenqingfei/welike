package com.redefine.multimedia.player.h5;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.h5.WebViewDelegate;
import com.redefine.multimedia.player.base.IVideoView;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommand;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommandId;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.multimedia.player.request.YoutubeProtocolParser;

public class WebVideoView implements IVideoView, WebViewDelegate.ISslErrorCancelProcess {

    private final WebViewDelegate mWebViewDelegate;
    private MediaPlayerInvoker mInvoker;

    public WebVideoView(ViewGroup viewGroup) {
        mWebViewDelegate = new WebViewDelegate(viewGroup, new YoutubeProtocolParser(), "", this);
        mWebViewDelegate.setWebViewBgColor(Color.BLACK);
    }

    @Override
    public void setDataSource(String url) {
        mWebViewDelegate.loadUrl(url);
    }

    @Override
    public void setMediaController(IMediaController mediaController) {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public boolean pause() {
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void destroy() {
        mWebViewDelegate.destroy();
    }

    @Override
    public void setMediaPlayerInvoker(MediaPlayerInvoker invoker) {
        mInvoker = invoker;
    }

    @Override
    public void onPause() {
        mWebViewDelegate.onActivityPause();
    }

    @Override
    public void onResume() {
        mWebViewDelegate.onActivityResume();
    }

    @Override
    public String getPlayUrl() {
        return null;
    }

    @Override
    public void onCancelProcess() {
        if (mInvoker != null) {
            mInvoker.doCommand(new MediaPlayerCommand(MediaPlayerCommandId.ON_CANCEL_PROCESS));
        }
    }
}
