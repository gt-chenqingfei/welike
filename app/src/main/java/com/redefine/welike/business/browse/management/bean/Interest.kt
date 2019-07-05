package com.redefine.welike.business.browse.management.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName

/**
 * Created by honglin on 2018/7/19.
 */
@Entity(tableName = "interest_table")
open class Interest(
        @PrimaryKey
        @NonNull
        @SerializedName("id")
        var id: String = "",
        @SerializedName("icon")
        var icon: String?,
        @SerializedName("name")
        var name: String?
)
