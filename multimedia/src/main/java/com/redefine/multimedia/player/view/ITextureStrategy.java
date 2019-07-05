package com.redefine.multimedia.player.view;

import android.view.TextureView;

/**
 * @author qingfei.chen
 * @date 2019/2/22
 * Copyright (C) 2018 redefine , Inc.
 */
public interface ITextureStrategy {
    void onPause();

    void onResume();

    void setVideoSize(int width, int height);

    TextureView getTextureView();
}
