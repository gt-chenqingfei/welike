/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/8/6
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/8/6
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/8/6, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.transformer;

import com.pekingese.pagestack.framework.view.PageTransformer;

public class TransitionEffectFactory {

    public static PageTransformer getPageTransformer(TransitionEffect effect) {
        PageTransformer transformer = null;
        switch (effect) {
            case Stack:
                transformer = new StackTransformer();
                break;
            case Ios_Stack:
            default:
                transformer = new IosStackTransformer();
                break;
        }
        return transformer;
    }
}
