package com.redefine.welike.business.publisher.api.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *
 * Name: Topic
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-09-04 14:56
 *
 */


open class Topic(@SerializedName("id") val id: String?, @SerializedName("name") val name: String, @SerializedName("labelId") var labelId: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(labelId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Topic> {
        override fun createFromParcel(parcel: Parcel): Topic {
            return Topic(parcel)
        }

        override fun newArray(size: Int): Array<Topic?> {
            return arrayOfNulls(size)
        }
    }

}

class TopicCategory(@SerializedName("labelId") val labelId: String, @SerializedName("labelName")val labelName: String)

class SuperTopic(id: String, name: String, labelId: String?) : Topic(id.trim(), name.trim(), labelId), Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuperTopic> {
        override fun createFromParcel(parcel: Parcel): SuperTopic {
            return SuperTopic(parcel)
        }

        override fun newArray(size: Int): Array<SuperTopic?> {
            return arrayOfNulls(size)
        }
    }

}

class CommonTopic(id: String?, name: String, labelId: String?) : Topic(id?.trim(), name.trim(), labelId), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommonTopic> {
        override fun createFromParcel(parcel: Parcel): CommonTopic {
            return CommonTopic(parcel)
        }

        override fun newArray(size: Int): Array<CommonTopic?> {
            return arrayOfNulls(size)
        }
    }

}
