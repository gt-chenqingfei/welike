package com.redefine.welike.business.publisher.management.handler

import com.redefine.welike.business.publisher.management.draft.*
import com.redefine.welike.business.publisher.management.task.AbstractTask

/**
 *
 * Name: HandlerQueueBuilder
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 18:54
 *
 */

object HandlerQueueBuilder {

    fun <T : IPublishDraft> buildQueue(publishTask: AbstractTask<T>, postDraft: T, publishHandler: AbstractHandler): HandlerQueue? {
        return when (postDraft) {
            is PublishCommentDraft,
            is PublishReplyDraft,
            is PublishForwardDraft,
            is PublishPostDraft -> {
                val finishHandler = FinishHandler(postDraft, 99, 100, null)

//                val publishHandler = PublishPostHandler(postDraft, 97, 99, finishHandler)
                publishHandler.next = finishHandler
                publishHandler.minProgress = 99
                publishHandler.maxProgress = 99

                val hasArticleUploadDraft = postDraft.getArticleDraft() != null && postDraft.getArticleDraft()!!.hasUploadDraft()
                val hasPostUploadDraft = postDraft.hasUploadDraft()
                var uploadHandler: UploadHandler? = null
                var articleUploadHandler: ArticleUploadHandler? = null
                if (!hasPostUploadDraft && !hasArticleUploadDraft) {
                    uploadHandler = UploadHandler(postDraft, 10, 97, publishHandler)
                    articleUploadHandler = ArticleUploadHandler(postDraft, 3, 10, uploadHandler)
                } else if (hasPostUploadDraft && hasArticleUploadDraft) {
                    uploadHandler = UploadHandler(postDraft, 50, 97, publishHandler)
                    articleUploadHandler = ArticleUploadHandler(postDraft, 3, 50, uploadHandler)
                } else if (hasPostUploadDraft && !hasArticleUploadDraft) {
                    uploadHandler = UploadHandler(postDraft, 4, 97, publishHandler)
                    articleUploadHandler = ArticleUploadHandler(postDraft, 3, 4, uploadHandler)
                } else {
                    uploadHandler = UploadHandler(postDraft, 96, 97, publishHandler)
                    articleUploadHandler = ArticleUploadHandler(postDraft, 3, 96, uploadHandler)
                }

                val compressHandler = CompressHandler(postDraft, 2, 3, articleUploadHandler)
                val articleCompressHandler = ArticleCompressHandler(postDraft, 1, 2, compressHandler)
                val startHandler = StartHandler(postDraft, 0, 1, articleCompressHandler)
                HandlerQueue(publishTask, startHandler)
            }
            else -> {
                return null
            }
        }
    }
}