package com.redefine.welike.business.publisher.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(tableName = "t_draft")
data class Draft(
        @PrimaryKey
        @NonNull
        var did: String,

        var uid: String,

        var visibility: Boolean?,

        var time: Long,

        var content: String,

        var type: Int?
)

data class DraftCount(
        val total: Int
)
