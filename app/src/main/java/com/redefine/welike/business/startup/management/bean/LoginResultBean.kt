package com.redefine.welike.business.startup.management.bean;

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by honglin on 2018/6/13.
 */

data class LoginResultBean(
        @SerializedName("result")
        var result: ResultBean?,
        @SerializedName("resultMsg")
        var resultMsg: String?,
        @SerializedName("code")
        var code: Int

)

open class ResultBean(
        @SerializedName("access_token")
        var access_token: String?,
        @SerializedName("refresh_token")
        var refresh_token: String?,
        @SerializedName("token_type")
        var token_type: String?,
        @SerializedName("expires_in")
        var expires_in: Int,
        @SerializedName("userinfo")
        var userinfo: UserinfoBean?


)

open class BlockBean(
        @SerializedName("userId")
        var userId: String?,
        @SerializedName("blockReason")
        var blockReason: String?,
        @SerializedName("jumpUrl")
        var blockUrl: String?,
        @SerializedName("status")
        var status: Int?

)

open class UserinfoBean(
        @SerializedName("nextUpdateNickNameTimestamp")
        var nextUpdateNickNameTimestamp: Long,
        @SerializedName("honorLevel")
        var honorLevel: Int,
        @SerializedName("honorPicUrl")
        var honorPicUrl: String?,
        @SerializedName("isAllowUpdateSex")
        var isAllowUpdateSex: Boolean,
        @SerializedName("sexUpdateCount")
        var sexUpdateCount: Int,
        @SerializedName("uid")
        var uid: String,
        @SerializedName("userLevel")
        var userLevel: Int,
        @SerializedName("postsCount")
        var postsCount: Int,
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("vip")
        var vip: Int,
        @SerializedName("introduction")
        var introduction: String?,
        @SerializedName("settings")
        var settings: String?,
        @SerializedName("avatarUrl")
        var avatarUrl: String,
        @SerializedName("nickName")
        var nickName: String,
        @SerializedName("sex")
        var sex: Byte,
        @SerializedName("followedUsersCount")
        var followedUsersCount: Int,
        @SerializedName("likedCount")
        var likedCount: Int,
        @SerializedName("userId")
        var userId: Int,
        @SerializedName("isAllowUpdateNickName")
        var isAllowUpdateNickName: Boolean,
        @SerializedName("finishLevel")
        var finishLevel: Int,
        @SerializedName("la")
        var la: String?,
        @SerializedName("followUsersCount")
        var followUsersCount: Int,
        @SerializedName("likePostsCount")
        var likePostsCount: Int,
        @SerializedName("nextUpdateNickNameDate")
        var nextUpdateNickNameDate: Long,
        @SerializedName("status")
        var status: Int,
        @SerializedName("firstLogin")
        var firstLogin: Boolean,
        @SerializedName("source")
        var source: Int,
        @SerializedName("registerWay")
        var registerWay: Int,
        @SerializedName("phone")
        var phone: String="",
        @SerializedName("interests")
        var interests: List<InterestsBean>?)


open class InterestsBean(
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
        var subset: ArrayList<InterestsBean>?,
        //for ui item select
        @SerializedName("isSelected")
        var isSelected: Boolean = false,
// /for ui item select
        @SerializedName("isExpose")
        var isExpose: Boolean = false
)
