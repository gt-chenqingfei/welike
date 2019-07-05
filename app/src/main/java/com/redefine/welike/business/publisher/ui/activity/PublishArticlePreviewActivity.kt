package com.redefine.welike.business.publisher.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.fresco.loader.VideoCoverUrlLoader
import com.redefine.welike.R
import com.redefine.welike.base.constant.CommonRequestCode
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.publisher.management.draft.ArticleDraft
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.kext.translate
import kotlinx.android.synthetic.main.publish_article_preview.*

/**
 * Name: PublishArticlePreviewActivity
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-24 21:17
 */
class PublishArticlePreviewActivity : BaseActivity() {
    private var articleDraft: ArticleDraft? = null

    companion object {
        const val EXTRA_ARTICLE_DRAFT = "article_draft"
        fun launchArticlePreviewActivity(context: Activity, articleDraft: ArticleDraft) {
            val intent = Intent()
            val bundle = Bundle()
            intent.setClass(context, PublishArticlePreviewActivity::class.java)
            bundle.putSerializable(EXTRA_ARTICLE_DRAFT, articleDraft)
            intent.putExtras(bundle)
            context.startActivityForResult(intent, CommonRequestCode.PUBLISH_ARTICLE_PREVIEW)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publish_article_preview)
        parseBundle(intent)
        if (articleDraft == null) {
            finish()
        }
        common_back_btn.setOnClickListener {
            finish()
        }
        publish_preview_edit.setOnClickListener {
            finish()
        }

        common_title_view.text = "article_preview".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        publish_preview_title.text = articleDraft!!.mPostName
        publish_preview_content.richProcessor.setRichContent(articleDraft!!.mRichContent!!.text, articleDraft!!.mRichContent!!.richItemList)

        if (articleDraft!!.videoAttachmentDraft != null) {
            publish_video_container.visibility = View.VISIBLE
            VideoCoverUrlLoader.getInstance().loadVideoThumbFile(publish_video_container, publish_video_thumb
                    , articleDraft!!.videoAttachmentDraft!!.videoItemDraft.localFileName
                    , articleDraft!!.videoAttachmentDraft!!.videoItemDraft.width
                    , articleDraft!!.videoAttachmentDraft!!.videoItemDraft.height)
            publish_video_play.setOnClickListener {
                PhotoSelector.launchPlayPostVideo(this, articleDraft!!.videoAttachmentDraft!!.videoItemDraft.localFileName)
            }
        }
        publish_next.text = "done".translate(ResourceTool.ResourceFileEnum.PUBLISH)
        editor_photo.isEnabled = false
        publish_emoji.isEnabled = false
        publish_link.isEnabled = false
        publish_next.setOnClickListener {
            setResult(Activity.RESULT_OK)
            PublishPostStarter.startActivityFromArticle(this, articleDraft)
            finish()
        }
    }


    private fun parseBundle(intent: Intent?) {

        if (intent != null && intent.hasExtra(EXTRA_ARTICLE_DRAFT)) {
            articleDraft = intent.getSerializableExtra(EXTRA_ARTICLE_DRAFT) as ArticleDraft?
        }
    }

}
