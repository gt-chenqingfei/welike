package com.redefine.welike.business.publisher.management.draft

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 * Name: DraftVideoAttachment
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 17:55
 *
 */
@Parcelize
class VideoAttachmentDraft constructor(val videoItemDraft: VideoItemDraft, var videoCoverDraft: PicItemDraft? = null): IUploadAttachmentDraft, Parcelable, Serializable {

    override fun getUploadType(): IUploadAttachmentDraft.UploadAttachmentType {
        return IUploadAttachmentDraft.UploadAttachmentType.VIDEO
    }

}