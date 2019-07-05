/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/8/6
 * <p>
 * Description : Page配置的堆栈，PageStack根据PageConfig的配置初始化Page显示，用于保存Page的持久化对象，
 * 被干掉或者杀进程恢复，会恢复当前页面的堆栈状态
 * <p>
 * Creation    : 2017/8/6
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/8/6, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework;

import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.PageStackSaveState;
import com.pekingese.pagestack.framework.page.BasePage;

import java.util.List;
import java.util.Stack;

public interface IPageConfigManager {

    int size();

    PageConfig getPageConfig(int position);

    void pushPageConfig(PageConfig pageConfig);

    List<PageConfig> removePositionRight(int curItem);

    int getPagePosition(PageConfig pageConfig);

    Stack<PageConfig> getSaveState();

    void restoreState(PageStackSaveState saveState);

    Stack<PageConfig> getAllPageConfigs();

    List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass);

    PageConfig findPageConfigByIndex(int index);
}
