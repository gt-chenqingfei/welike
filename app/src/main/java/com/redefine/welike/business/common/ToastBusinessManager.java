package com.redefine.welike.business.common;

import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.BlockUserManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.commonui.util.ToastUtils;

public class ToastBusinessManager implements BlockUserManager.BlockUserCallback, FollowUserManager.FollowUserCallback {

    public void onBind() {
        BlockUserManager.getInstance().register(this);
        FollowUserManager.getInstance().register(this);


    }

    public void onUnBind() {
        BlockUserManager.getInstance().unregister(this);
        FollowUserManager.getInstance().unregister(this);

    }


    @Override
    public void onBlockCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "block_success"));
        } else {
            ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        }
    }

    @Override
    public void onUnBlockCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "un_block_success"));
        } else {
            ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        }
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode != ErrorCode.ERROR_SUCCESS) {
            ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));

        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode != ErrorCode.ERROR_SUCCESS) {
            ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        }
    }
}
