package com.redefine.im.management;

/**
 * Created by liubin on 2018/2/11.
 */

public interface IIMCountProvider {

    void reload();
    void register(IMCountProvider.IMCountProviderCallback listener);
    void unregister(IMCountProvider.IMCountProviderCallback listener);
    int getIMMessagesCount();

}
