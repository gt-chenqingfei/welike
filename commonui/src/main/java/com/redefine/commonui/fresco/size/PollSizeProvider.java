package com.redefine.commonui.fresco.size;

import com.redefine.foundation.utils.ScreenUtils;

public class PollSizeProvider implements ISizeProvider {

    private Size size = new Size();
    private static final int POLL_MAX_WIDTH = 160;
    private static final int POLL_MAX_HEIGHT = 120;

    public PollSizeProvider() {
        if (ScreenUtils.getScreenDensity() > 2.0f) {
            size.width = POLL_MAX_WIDTH * 3;
            size.height = POLL_MAX_HEIGHT * 3;
        } else {
            size.width = POLL_MAX_WIDTH * 2;
            size.height = POLL_MAX_HEIGHT * 2;
        }
    }

    @Override
    public int getWidth() {
        return size.width;
    }

    @Override
    public int getHeight() {
        return size.height;
    }
}
