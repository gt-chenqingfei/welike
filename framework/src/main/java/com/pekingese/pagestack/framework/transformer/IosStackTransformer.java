/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */

package com.pekingese.pagestack.framework.transformer;

import android.view.View;

import com.pekingese.pagestack.framework.view.PageTransformer;

public class IosStackTransformer implements PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        position = position * 3f / 4f;
        if (isSmall(position)) {
            position = 0;
        }
        if (position <= 0) {
            page.setTranslationX(-page.getWidth() * position);
        }
    }

    private boolean isSmall(float position) {
        return Math.abs(position) <= 0.001;
    }
}
