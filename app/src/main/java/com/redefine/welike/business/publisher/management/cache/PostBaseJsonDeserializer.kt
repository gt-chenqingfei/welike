package com.redefine.welike.business.publisher.management.cache

import com.google.gson.*
import com.redefine.welike.business.browse.management.bean.InterestPost
import com.redefine.welike.business.feeds.management.bean.*
import com.redefine.welike.business.user.management.bean.FollowPost
import java.lang.reflect.Type

/**
 * @author qingfei.chen
 * @date 2018/11/27
 * Copyright (C) 2018 redefine , Inc.
 */
class PostBaseJsonDeserializer : JsonDeserializer<PostBase> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): PostBase? {
        if (json == null || !json.isJsonObject) {
            return null
        }
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asInt

        return when (type) {
            PostBase.POST_TYPE_ART -> Gson().fromJson(json, ArtPost::class.java)
            PostBase.POST_TYPE_VIDEO -> Gson().fromJson(json, VideoPost::class.java)
            PostBase.POST_TYPE_PIC -> Gson().fromJson(json, PicPost::class.java)
            PostBase.POST_TYPE_TEXT -> Gson().fromJson(json, TextPost::class.java)
            PostBase.POST_TYPE_FOLLOW -> Gson().fromJson(json, FollowPost::class.java)
            PostBase.POST_TYPE_GP_SCORE -> Gson().fromJson(json, GPScorePost::class.java)
            PostBase.POST_TYPE_INTEREST -> Gson().fromJson(json, InterestPost::class.java)
            PostBase.POST_TYPE_LINK -> Gson().fromJson(json, LinkPost::class.java)
            PostBase.POST_TYPE_POLL -> Gson().fromJson(json, PollPost::class.java)
            PostBase.POST_TYPE_FORWARD -> {
                val gson = GsonBuilder()
                        .registerTypeAdapter(PostBase::class.java, PostBaseJsonDeserializer())
                        .create()
                gson.fromJson(json, ForwardPost::class.java)
            }
            else -> null
        }
    }

}