package com.redefine.welike.business.common.mission

import com.google.gson.annotations.SerializedName

class Mission(
        @SerializedName("type")
        var type: Int?,

        @SerializedName("times")
        var times: Int?,

        @SerializedName("goalNum")
        var goalNum: String?,

        @SerializedName("title")
        var title: String?,

        @SerializedName("content")
        var content: String?,

        @SerializedName("icon")
        var icon: String?,

        @SerializedName("url")
        var url: String?,

        @SerializedName("reward")
        var reward: String?,

        @SerializedName("finish")
        var finish: Boolean = false,

        @SerializedName("button")
        var button: String?
)

class TaskDetail(
        @SerializedName("taskLevel")
        var taskLevel: Int?,

        @SerializedName("allFinish")
        var allFinish: Boolean = false, //show at Homepage.

        @SerializedName("tasks")
        var list: ArrayList<Mission>?
)

class MissionTask(

        @SerializedName("title")
        var title: String?,

        @SerializedName("forwardUrl")
        var forwardUrl: String?,

        @SerializedName("honorPicUrl")
        var honorPicUrl: String,

        @SerializedName("curTaskType")
        var curTaskType: Int?,

        @SerializedName("display")
        var display: Boolean = false, //show at Homepage.

        @SerializedName("taskDetail")
        var taskDetail: TaskDetail?
)