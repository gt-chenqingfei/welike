package com.redefine.welike.business.user.ui.listener;

import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;

import java.util.List;

/**
 * Created by honglin on 2018/5/16.
 */

public interface IReasonCallBackListener {

    void onReason(List<DeactivateReasonBean> list , int errorCode);

}
