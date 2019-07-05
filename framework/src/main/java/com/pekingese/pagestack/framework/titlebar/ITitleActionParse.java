/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/30
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/30
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/30, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

import android.content.Context;
import android.view.View;

import java.util.List;

public interface ITitleActionParse {

    List<View> parseTitleActions(Context context, List<TitleAction> actions);
}
