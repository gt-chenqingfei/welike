package com.redefine.welike.common;


import com.redefine.welike.Switcher;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;

/**
 * Created by honglin on 2018/6/21.
 * <p>
 * current : no login browse manager
 */

public class BrowseManager {

    public static final boolean VIDMATE_ABTEST = true;

    /**
     * 是否有免登陆模式
     */
    public static boolean isVidMate() {

        return true;
    }

    /**
     * 免登陆discover页 ab test switch
     */
    public static final boolean BROWSE_DISCOVER_AB_TEST = false;

    public static boolean isHomeTest() {
        if (Switcher.FOURCE_SHOW_DISCOVER){
            return true;
        }
        int value = ABTest.INSTANCE.check(ABKeys.TEST_LOGIN_HOME);

        switch (value) {

            case 1:
                return true;
            default:
                return false;
        }

//        return BROWSE_DISCOVER_AB_TEST;

    }


}
