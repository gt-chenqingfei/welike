package com.redefine.welike.business.startup.management.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by honglin on 2018/7/10.
 */

class NickNameCheckerBean(
        @SerializedName("contentNum")
        var repeatNum: Int = 0,
        @SerializedName("gt")
        var gt: Boolean,
        @SerializedName("repeat")
        var repeat: Boolean,
        var errCode: Int

)

class HalfAccountBean(
        var nickName: String = "",
        var phone: String = "",
        var source: Int = 0,
        var registerWay: Int = 0,
        var status: Int = 0,
        var avatarUrl: String = ""
)