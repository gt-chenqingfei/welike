package com.redefine.welike.common;

import com.facebook.login.LoginManager;

/**
 * Created by daining on 2018/4/18.
 */

public class FacebookManager {

    private static FacebookManager instance;

    public static synchronized FacebookManager getInstance() {
        if (instance == null) {
            instance = new FacebookManager();
        }
        return instance;
    }

    public void init() {
        Life.regListener(new LifeListener() {
            @Override
            public void onFire(int event) {
                if (event == Life.LIFE_LOGOUT) {
                    LoginManager.getInstance().logOut();
                }
            }
        });
    }
}
