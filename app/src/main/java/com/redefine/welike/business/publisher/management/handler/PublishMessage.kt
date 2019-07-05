package com.redefine.welike.business.publisher.management.handler

/**
 *
 * Name: PublishMessage
 * Author: liwenbo
 * Email:
 * Comment: //发布流程中的状态信息及发布状态的回调
 * Date: 2018-07-10 18:21
 *
 */
class PublishMessage(val progress: Int? = 0,
                     val state: PublishState = PublishState.INIT,
                     val errorCode: ErrorCode = ErrorCode.NONE,
                     val errorMsg: String? = null
) {

    enum class PublishState {
        INIT, START, RUNNING, SUCCESS, ERROR, CANCELED
    }

    enum class ErrorCode {
        NONE, NETWORK_DIS_CONNECT, ACCOUNT_INVALID, EXCEPTION,
        SERVER_ERROR, PICTURE_UPLOAD_FAIL, VIDEO_UPLOAD_FAIL
    }
}