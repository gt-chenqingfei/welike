package com.redefine.im.management;

import com.redefine.im.bean.IMSession;

import java.util.List;
import java.util.Set;

/**
 * Created by liubin on 2018/2/6.
 */

public interface IIMSessionProvider {

    void register(IMSessionProvider.IMSessionProviderCallback listener);
    void unregister(IMSessionProvider.IMSessionProviderCallback listener);

    /**
     * session list
     * @param session
     * @param callback
     */
    void goInSession(IMSession session, IMSessionGotoCallback callback);

    /**
     * profile
     * @param uid
     * @param callback
     */
    void goInSingleSession(String uid, final IMSessionGotoCallback callback);

    /**
     * client
     * @param callback
     */
    void goInCustomerSession(final IMSessionGotoCallback callback);

    void listAllSessions(boolean greet);
    void getSessions(Set<String> sids);
    void countAllSessions(boolean greet);
    void removeSession(IMSession session);

}
