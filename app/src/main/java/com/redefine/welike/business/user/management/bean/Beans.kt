package com.redefine.welike.business.user.management.bean

import com.google.gson.annotations.SerializedName
import com.redefine.welike.business.feeds.management.bean.PostBase
import kotlin.math.acos

class FollowPost : PostBase {

    constructor() {
        type = POST_TYPE_FOLLOW
    }

    var list: List<RecommendSlideUser>? = null

    var fromPosition = 0

}


/**
 * 推荐人
 * */
class RecommendUser(
        @SerializedName("id")
        var uid: String,
        @SerializedName("nickName")
        var name: String,
        @SerializedName("avatarUrl")
        var avatar: String,
        @SerializedName("introduction")
        var intro: String,

        @SerializedName("vip")
        var vip: Int = 1,
        @SerializedName("curLevel")
        var curLevel: Int = 0,
        @SerializedName("followedUsersCount")
        var followedUsersCount: Int = 1,
        @SerializedName("follow")
        var following: Boolean = false,  // 我关注的
        @SerializedName("followed")
        var follower: Boolean = false,
        var select: Boolean = true
)


/**
 * slide推荐人
 * */
class RecommendSlideUser(
        //标示类型，0是默认类型，服务器返回的推荐用户，1为联系人卡片。
        @SerializedName("type")
        var type: Int,
        @SerializedName("userId")
        var uid: String,
        @SerializedName("nickName")
        var name: String,
        @SerializedName("avatarUrl")
        var avatar: String,
        @SerializedName("introduction")
        var intro: String,

        @SerializedName("abilityTagName")
        var abilityTagName: String,
        @SerializedName("certifyType")
        var certifyType: Int,
        @SerializedName("certifyTypeId")
        var certifyTypeId: Int,
        @SerializedName("certifyTypeName")
        var certifyTypeName: String,
        @SerializedName("recommendReason")
        var recommendReason: Int,


        @SerializedName("curLevel")
        var curLevel: Int = 0,
        @SerializedName("followedNum")
        var followedUsersCount: Int = 1,
        @SerializedName("follow")
        var following: Boolean = false,  // 我关注的
        @SerializedName("followed")
        var follower: Boolean = false,
        var select: Boolean = true
)

/**
 * slide推荐列表
 * */
class RecommendSlideUserList(
        @SerializedName("hasNextPage")
        var hasNextPage: Boolean = false,
        @SerializedName("totalSize")
        var totalSize: Int = 0,
        @SerializedName("cursor")
        var cursor: String? = null,
        @SerializedName("list")
        var list: List<RecommendSlideUser>? = null
)

/**
 * 推荐列表
 * */
class RecommendUserList(@SerializedName("list") var list: List<RecommendUser>? = null)

/**
 * 推荐人
 * */
class RecommendUser1(
        @SerializedName("userId")
        var uid: String,
        @SerializedName("nickName")
        var name: String,
        @SerializedName("avatarUrl")
        var avatar: String,
        @SerializedName("introduction")
        var intro: String,
        @SerializedName("vip")
        var vip: Int = 1,
        @SerializedName("followedUsersCount")
        var followedUsersCount: Int = 1,
        @SerializedName("postsCount")
        var postsCount: Int = 1,
        @SerializedName("curLevel")
        var curLevel: Int = 0,
        @SerializedName("postImages")
        var postImages: List<AttachmentBean>? = null, // 我关注的
        @SerializedName("follow")
        var following: Boolean = false,  // 我关注的
        @SerializedName("followed")
        var follower: Boolean = false,
        @SerializedName("likedCount")
        var likeCount: Long
) {
    fun copy(): RecommendUser {

        return RecommendUser(uid, name, avatar, intro, vip, curLevel, followedUsersCount, following, follower, true)

    }
        fun toRecommendSlideUser(): RecommendSlideUser {
                return RecommendSlideUser(0, uid, name, avatar, intro, "", 0, 0, "", 0,
                        curLevel, followedUsersCount, following, follower, false)
        }
}

/**
 * 推荐人
 * */
class AttachmentBean(
        @SerializedName("type")
        var type: String,
        @SerializedName("source")
        var source: String,
        @SerializedName("icon")
        var icon: String,
        @SerializedName("content")
        var content: AttachmentContent
)

class AttachmentContent(
        @SerializedName("id")
        var id: String,
        @SerializedName("content")
        var content: String,
        @SerializedName("summary")
        var summary: String,
        @SerializedName("origin")
        var origin: Int,
        @SerializedName("language")
        var language: String
)


/**
 * 推荐列表
 * */
class RecommendUserList1(@SerializedName("list") var list: List<RecommendUser1>? = null)


/**
 * 推荐列表 with tag
 * */
class RecommendUserListWithTag(@SerializedName("list") var list: List<RecommendTagBean>? = null)

class Tag(@SerializedName("value") var tag: String, @SerializedName("isDefault") var isDefault: Int = 0, @SerializedName("labelId") var labelId: Int)

/**
 * 推荐列表
 * */
class RecommendTagBean(@SerializedName("users") var list: List<RecommendUser>? = null, @SerializedName("tag") var tag: Tag)



/**
 * face to face
 * */
class FaceToFaceRecommendUserList(@SerializedName("users") var list: List<RecommendUser>? = null)



