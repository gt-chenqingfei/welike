package com.redefine.welike.business.feeds.management.bean

import android.support.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author redefine honlin
 * @Date on 2018/11/5
 * @Description
 */
open class HeaderAttachment(@SerializedName("type")
                            var type: String = "",
                            @SerializedName("id")
                            var id: String?,
                            @SerializedName("icon")
                            var icon: String,
                            @SerializedName("title")
                            var title: String,
                            @SerializedName("source")
                            var source: String,
                            @SerializedName("height")
                            var height: Int,
                            @SerializedName("width")
                            var width: Int) : Serializable


class HeaderSystemAttachment : HeaderAttachment {

    @DrawableRes
    var drawableRes: Int

    constructor(type: String, title: String, drawable: Int) : super(type, "", "", title, "", 0, 0) {
        drawableRes = drawable
    }
}


class AdAttachment(@SerializedName("type")
                   var type: String = "",
                   @SerializedName("title")
                   var title: String,
                   @SerializedName("startTime")
                   var startTime: Long,
                   @SerializedName("endTime")
                   var endTime: Long,
                   @SerializedName("description")
                   var content: String,
                   @SerializedName("forwardUrl")
                   var forwardUrl: String,


                   @SerializedName("cover")
                   var cover: String,
                   @SerializedName("coverHeight")
                   var coverHeight: String,
                   @SerializedName("coverWidth")
                   var coverWidth: String,

                   @SerializedName("statusInfo")
                   var statusInfo: String,
                   @SerializedName("status")
                   var status: Int,
                   @SerializedName("language")
                   var language: String) : Serializable


class ActiveAttachment : Serializable {

    @SerializedName("topicNames")
    var topicNames: String = ""
    @SerializedName("title")
    var title: String = ""
    @SerializedName("startTimestamp")
    var startTime: Long = 0
    @SerializedName("endTimestamp")
    var endTime: Long = 0
    @SerializedName("communityNames")
    var communityNames: String = ""
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("picUrl")
    var picUrl: String = ""
    @SerializedName("landPage")
    var landPage: String = ""

}







