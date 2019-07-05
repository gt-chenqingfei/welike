package com.redefine.multimedia.player.view;

import android.content.Context;
import android.os.Build;

/**
 * @author qingfei.chen
 * @date 2019/2/22
 * Copyright (C) 2018 redefine , Inc.
 */
public class TextureStrategyFactory {
    public static ITextureStrategy getTextureStrategy(Context context) {
        if ("EML-AL00".equals(Build.MODEL)) {
            return new VideoTextureView4HuaWei(context);
        }
        return new VideoTextureView(context);
    }
}
