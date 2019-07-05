package com.redefine.welike.business.user.management.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by honglin on 2018/5/23.
 */
open class DeactivateReasonBean {

    @SerializedName("id")
    var id: Int = 0
    @SerializedName("reason")
    var reason: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("icon")
    var icon: String? = null

    var isChecked = false

}