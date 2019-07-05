package com.pekingese.pagestack.framework;

import android.os.Message;

import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/4.
 */

public interface IPageMessageDispatcher {

    void dispatchMessageToPage(Class<? extends BasePage> pageClass, Message message, boolean isToAll);

    void dispatchMessageToPageByIndex(int index, Message message);

    void dispatchMessageToPageByConfig(PageConfig config, Message message);

    void dispatchMessageToPagesByConfig(List<PageConfig> configs, Message message);

    void dispatchMessageToAll(Message message);
}
