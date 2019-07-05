package com.redefine.welike.business.contact.bean

import com.google.gson.annotations.SerializedName

data class FriendList(
        @SerializedName("list")
        var list: ArrayList<Friend>,

        @SerializedName("cursor")
        var cursor: String?)

open class Friend(
        @SerializedName("id")
        var id: String?,

        @SerializedName("avatarUrl")
        var avatarUrl: String?,

        @SerializedName("nickName")
        var nickName: String?,

        @SerializedName("contactName")
        var contactName: String?,

        @SerializedName("phone")
        var phone: String?,

        @SerializedName("follow")
        var follow: Boolean,

        @SerializedName("followed")
        var followed: Boolean,

        @SerializedName("registed")
        var registed: Boolean,

        @SerializedName("vip")
        var vip: Int = 0,

        @SerializedName("gender")
        var gender: Int = 0
)

fun Friend.clone() = Friend(id, avatarUrl, nickName, contactName, phone, follow, followed, registed, vip, gender)