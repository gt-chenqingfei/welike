package com.redefine.welike.business.feeds.ui.listener

/**
 * Created by nianguowang on 2019/1/18
 */
interface OnRequestPermissionCallback {
    fun onRequestPermission(message: String, requestCode: Int, permission: String);
}