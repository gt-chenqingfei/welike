package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 * @author qingfei.chen
 * @date 2018/12/4
 * Copyright (C) 2018 redefine , Inc.
 */

data class User(@SerializedName("id") val id: String)

data class Attachment(@SerializedName("id") val id: String, val type: String)

data class Post(@SerializedName("id") val id: String,
                @SerializedName("forwardPost") val forwardPost: Forward?,
                @SerializedName("community") val community: Community?,
                @SerializedName("attachments") val attachments: ArrayList<Attachment>?,
                @SerializedName("user") val user: User?)

data class Forward(@SerializedName("id") val id: String,
                   @SerializedName("attachments") val attachments: ArrayList<Attachment>?,
                   @SerializedName("language") val language: String?,
                   @SerializedName("tags") val tags: ArrayList<String>?,
                   @SerializedName("user") val user: User?)

data class Comment(@SerializedName("id") val id: String,
                   @SerializedName("post") val post: Post,
                   @SerializedName("attachments") val attachments: ArrayList<Attachment>?,
                   @SerializedName("user") val user: User?)

data class Reply(@SerializedName("id") val id: String,
                 @SerializedName("comment") val comment: Comment,
                 @SerializedName("reply") val reply: Reply,
                 @SerializedName("attachments") val attachments: ArrayList<Attachment>?,
                 @SerializedName("user") val user: User?)

data class Community(@SerializedName("id") val id: String)

data class ResponsePostBean(@SerializedName("post") val post: Post)

data class ResponseCommentBean(@SerializedName("post") val post: Post?,
                               @SerializedName("comment") val comment: Comment)

data class ResponseReplyBean(@SerializedName("reply") val reply: Reply,
                             @SerializedName("post") val post: Post?)

data class ResponseForwardBean(@SerializedName("post") val post: Post,
                               @SerializedName("comment") val comment: Comment?)
