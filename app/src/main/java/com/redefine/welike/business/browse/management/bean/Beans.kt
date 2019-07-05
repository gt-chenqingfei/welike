package com.redefine.welike.business.browse.management.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName

class Campaign(
        @SerializedName("id")
        var id: String = "",
        @SerializedName("topicName")
        var name: String?,
        @SerializedName("usersCount")
        var usersCount: Int?,
        @SerializedName("bannerUrl")
        var bannerUrl: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("postsCount")
        var postsCount: Int?
)


class CampaignList(

        @SerializedName("list")
        var list: ArrayList<Campaign>

)


class TrendPeople(@SerializedName("userId")
                  var id: String = "",
                  @SerializedName("avatarUrl")
                  var avatarUrl: String?,
                  @SerializedName("vip")
                  var vip: Int)

class TrendPeopleList(
        @SerializedName("users")
        var list: ArrayList<TrendPeople>,
        @SerializedName("forwardUrl")
        var forwardUrl: String

)


class TrendWords(@SerializedName("topicName")
                 var name: String = "",
                 @SerializedName("topicId")
                 var id: String)

class TrendWordsList(
        @SerializedName("list")
        var list: ArrayList<TrendWords>

)


class Attachment(@SerializedName("type")
                 var type: String,
                 @SerializedName("id")
                 var id: String,
                 @SerializedName("icon")
                 var icon: String,
                 @SerializedName("source")
                 var source: String,
                 @SerializedName("image-height")
                 var imageHeight: String,
                 @SerializedName("image-width")
                 var magewidth: String
)

class TopicPosts(
        @SerializedName("attachments")
        var attachments: ArrayList<Attachment>
)


class TrendTopic(@SerializedName("id")
                 var id: String = "",
                 @SerializedName("score")
                 var score: Int?,
                 @SerializedName("postsCount")
                 var postsCount: Int?,
                 @SerializedName("usersCount")
                 var usersCount: Int?,
                 @SerializedName("viewsCount")
                 var viewsCount: Int,
                 @SerializedName("bannerUrl")
                 var bannerUrl: String?,
                 @SerializedName("description")
                 var description: String?,
                 @SerializedName("icon")
                 var icon: String?,
                 @SerializedName("topPosts")
                 var topPosts: ArrayList<TopicPosts>,
                 @SerializedName("topicName")
                 var topicName: String?
)

class TrendTopicList(
        @SerializedName("list")
        var list: ArrayList<TrendTopic>,
        @SerializedName("cursor")
        var cursor: String?

)



@Entity(tableName = "follow_table")
open class FollowUser(
        @PrimaryKey
        @NonNull
        @SerializedName("id")
        var id: String = "",
        @SerializedName("name")
        var name: String?
)


@Entity(tableName = "like_table")
open class LikePostCount(
        @PrimaryKey
        @NonNull
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("time")
        var time: Long?
)

@Entity(tableName = "like_feed_table")
open class LikePost(
        @PrimaryKey
        @NonNull
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("pid")
        var pid: String = ""
)


