/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

public interface OnPageChangeListener {

    public void onPageScrolled(float positionOffset, int offsetPixels);

    public void onPageScrollStart();

    public void onPageScrollEnd(int currentItem);

    public void onPageScrollStateChanged(int state);
}
