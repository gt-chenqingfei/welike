package com.redefine.welike.business.user.management.bean

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by honglin on 2018/6/29.
 */
open class Interest(
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("icon")
        var icon: String?,
        @SerializedName("name")
        var name: String?,
        // 云控是否默认勾选
        @SerializedName("isDefault")
        var checked: Int = 0,
        // 云控是否默认勾选
        @SerializedName("subset")
        var subset: ArrayList<Interest>?,
        //for ui item select
        @SerializedName("isSelected")
        var isSelected: Boolean = false,
// /for ui item select
        @SerializedName("isExpose")
        var isExpose: Boolean = false
)

data class InterestRequestBean(
        @SerializedName("list")
        val list: ArrayList<Interest>
)



