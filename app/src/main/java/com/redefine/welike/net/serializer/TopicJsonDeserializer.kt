package com.redefine.welike.net.serializer

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.api.bean.Topic
import java.lang.reflect.Type

/**
 *
 * Name: TopicJsonDeserializer
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-09-05 14:41
 *
 */

class TopicJsonDeserializer  : JsonDeserializer<Topic> {

    companion object {
        private const val SUPER_TOPIC_TYPE = "1"
        private const val COMMON_TOPIC_TYPE = "2"
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Topic? {
        if (json == null || !json.isJsonObject) {
            return null
        }
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        return when {
            TextUtils.equals(type, SUPER_TOPIC_TYPE) -> Gson().fromJson(json, SuperTopic::class.java)
            TextUtils.equals(type, COMMON_TOPIC_TYPE) -> Gson().fromJson(json, CommonTopic::class.java)
            else -> null
        }
    }

}