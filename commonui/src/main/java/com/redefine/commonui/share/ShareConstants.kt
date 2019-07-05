package com.redefine.commonui.share

/**
 * Created by nianguowang on 2019/1/11
 */
class ShareConstants {

    companion object {
        const val PACKAGE_NAME_WHATS_APP = "com.whatsapp"
        const val PACKAGE_NAME_INSTAGRAM = "com.instagram.android"

        const val INTENT_TYPE_TEXT = "text/plain"
        const val INTENT_TYPE_IMAGE = "image/*"
        const val INTENT_TYPE_VIDEO = "video/*"
        const val INTENT_TYPE_ALL_FILE = "*/*"

        const val SHARE_REQUEST_CODE_WHATS_APP = 1
        const val SHARE_REQUEST_CODE_INSTAGRAM = 2

        const val SHARE_RESULT_CODE_SUCCESS_WHATS_APP = -1
        const val SHARE_RESULT_CODE_FAIL_WHATS_APP = 0

        const val SHARE_RESULT_CODE_SUCCESS_INSTAGRAM = 0
    }
}