package com.redefine.welike.business.publisher.management.draft;

import android.os.Parcelable
import android.text.TextUtils
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.richtext.RichContent
import com.redefine.welike.base.io.WeLikeFileManager
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Name: ArticleDraft
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-21 16:02
 */
@Parcelize
class ArticleDraft constructor(private val draftId: String
                               , var videoAttachmentDraft: VideoAttachmentDraft? = null
                               , var picAttachmentDraft: PicAttachmentDraft = PicAttachmentDraft()
                               , var mRichContent: RichContent? = null
                               , var mPostName: String = "") : Parcelable , Serializable {


    fun getUploadDraft(): IUploadAttachmentDraft? {
        if (videoAttachmentDraft == null) {
            return picAttachmentDraft
        }
        return videoAttachmentDraft
    }

    fun getDraftId(): String {
        return draftId
    }

    fun getRichContent(): RichContent? {
        return mRichContent
    }

    fun setRichContent(postName: String, result: RichContent) {
        mRichContent = result
        mPostName = postName
        val picList = mutableListOf<PicItemDraft>()

        result.richItemList?.forEach {
            if (it.isInnerImageItem) {
                val target = if (TextUtils.isEmpty(it.icon)) it.source else it.icon
                val picItemDraft = PicItemDraft(target)
                picItemDraft.fileSize = it.size
                picItemDraft.height = it.height
                picItemDraft.width = it.width
                picItemDraft.mimeType = it.mimeType
                picList.add(picItemDraft)
            }
        }
        picAttachmentDraft.picList = ArrayList(picList.filter {
            !WeLikeFileManager.isHttp(it.localFileName)
        })
    }

    fun hasUploadDraft(): Boolean {
        return !CollectionUtil.isEmpty(picAttachmentDraft.picList) || videoAttachmentDraft != null
    }
}
