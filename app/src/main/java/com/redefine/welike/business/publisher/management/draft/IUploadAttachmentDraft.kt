package com.redefine.welike.business.publisher.management.draft

/**
 *
 * Name: IUploadAttachmentDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 18:10
 *
 */
interface IUploadAttachmentDraft {

    fun getUploadType() : UploadAttachmentType

    enum class UploadAttachmentType {
        PIC, VIDEO
    }
}