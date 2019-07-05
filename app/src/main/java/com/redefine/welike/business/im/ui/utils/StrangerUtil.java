package com.redefine.welike.business.im.ui.utils;

import com.redefine.im.Constants;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.MessageSession;

/**
 * Created by nianguowang on 2018/6/12
 */
public class StrangerUtil {

    private StrangerUtil(){}

    /**
     * Both {@link MessageSession} and {@link MESSAGE} has isGreet property.
     * Weight : {@link MESSAGE}'isGreet > {@link MessageSession}'isGreet
     * @param session
     * @return
     */
    public static boolean isGreet(MessageSession session) {
        MESSAGE message = session.getMessage();
        Integer greet = message.isGreet();
        int isGreet = greet == null? Constants.MESSAGE_GREET_INVALID : greet;
        if(Constants.MESSAGE_GREET_INVALID == isGreet) {
            return session.getSIsGreet() == Constants.SESSION_STRANGE;
        } else {
            return greet == Constants.MESSAGE_GREET_STRANGE;
        }
    }
}
