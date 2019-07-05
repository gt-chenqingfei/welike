package com.redefine.welike.business.publisher.management.draft

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 * Name: DraftPicAttachment
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 17:54
 *
 */
@Parcelize
class PicAttachmentDraft constructor(var picList : ArrayList<PicItemDraft> = ArrayList()): IUploadAttachmentDraft, Parcelable, Serializable {

    override fun getUploadType(): IUploadAttachmentDraft.UploadAttachmentType {
        return IUploadAttachmentDraft.UploadAttachmentType.PIC
    }

}