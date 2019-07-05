package com.redefine.welike.business.publisher.management.draft

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Name: PicItemDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 22:44
 */
@Parcelize
class VideoItemDraft constructor(val localFileName: String
                                 , var attachmentDraftId: String? = null
                                 , var uploadLoadFileName: String? = null
                                 , var url: String? = null
                                 , var objectKey: String? = null
                                 , var mimeType: String? = null
                                 , var width: Int = 0
                                 , var height: Int = 0
                                 , var fileSize: Long = 0
                                 , var duration: Long = 0,
                                 var isVideoFromRecorder: Boolean) : Parcelable, Serializable