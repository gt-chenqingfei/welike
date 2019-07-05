package com.redefine.welike.business.user.ui.listener;

import com.redefine.welike.business.user.management.bean.DeactivateInfoBean;

/**
 * Created by honglin on 2018/5/16.
 */

public interface IUserInfoCallBackListener {

    void onResult(DeactivateInfoBean result, int errorCode);

}
